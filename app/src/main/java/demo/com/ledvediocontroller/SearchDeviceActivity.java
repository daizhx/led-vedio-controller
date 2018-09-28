package demo.com.ledvediocontroller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import demo.com.ledvediocontroller.util.SharePreferencesUtil;

public class SearchDeviceActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    private WifiManager mWifiManager;
    private WifiInfo currentWifiInfo;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //扫描热点结束
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                checkScanResult(mWifiManager.getScanResults());
            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                if (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1) == WifiManager.WIFI_STATE_ENABLED) {
                    // scan lens ap and connect
//                    mWifiManager.startScan();
                }
            }

        }
    };


    /**
     * 处理扫描结果
     * @param apList
     */
    private void checkScanResult(List<ScanResult> apList){
        List<ScanResult> lensList = new ArrayList<ScanResult>();
        List<String> apNameList = new ArrayList<>();
        for(ScanResult scanResult : apList){
            Log.e("SearchDeviceActivity","--->"+scanResult.SSID);
            if(scanResult.SSID.equals("Processor")){
                lensList.add(scanResult);
                apNameList.add(scanResult.SSID);
            }
        }

        if(lensList.size() > 0) {
            SharePreferencesUtil util = new SharePreferencesUtil(SearchDeviceActivity.this);
            util.setSharedPreference(Constants.DEV_AP_RECORD,apNameList);

            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("wifi_info", (ArrayList<? extends Parcelable>) lensList);
            setResult(RESULT_OK, intent);
        }else{
            setResult(RESULT_OK);
        }
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }


    public void requestLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(SearchDeviceActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(SearchDeviceActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(SearchDeviceActivity.this, "自Android 6.0开始需要打开位置权限才可以搜索到WIFI设备", Toast.LENGTH_SHORT).show();

                }
                //请求权限
                ActivityCompat.requestPermissions(SearchDeviceActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_ACCESS_COARSE_LOCATION:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(!scanWIFI()){
                        scanWIFIFail();
                    }
                }else{
                    setResult(RESULT_CANCELED);
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(scanWIFI()){
            SharePreferencesUtil spUtil = new SharePreferencesUtil(SearchDeviceActivity.this);
            spUtil.delete(Constants.DEV_AP_RECORD);
        }else{
            scanWIFIFail();
        }
    }

    private void scanWIFIFail(){
        Toast.makeText(SearchDeviceActivity.this,"无法扫描WIFI热点",Toast.LENGTH_LONG).show();
        setResult(RESULT_CANCELED);
        finish();
    }

    private boolean scanWIFI(){
        //SDK>23需要位置服务权限，且需要打开位置服务
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(SearchDeviceActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //没有权限
                requestLocationPermission();
                return false;
            }

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                //TODO 优化为自动打开位置服务
                Toast.makeText(SearchDeviceActivity.this,R.string.open_location_hint,Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
                return false;
            }
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mBroadcastReceiver,intentFilter);
        //该方法在将来的版本会被移除。。。
        return mWifiManager.startScan();
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {

        }
    }
}
