package demo.com.ledvediocontroller;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import demo.com.ledvediocontroller.util.SharePreferencesUtil;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SCAN = 1;
    private static final int REQUEST_SETTING = 2;

    ListView listView;
    List<ScanResult> scanResultList;
    List<String> apNameList;

    TextView tvEmptyDataHint;

    //保存手机当前的网络ID
    private int currentNetWorkId = -1;
    //是否连接到设备AP
    private boolean isConnectedDeviceAP;
    //是否正在尝试changeAP
    private boolean isChangingAP;

    private ProgressDialog progressDialog;

    private WifiManager mWifiManager;

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                {
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){
                        Log.i("MainActivity","wifi网络连接断开！");
                        currentNetWorkId = -1;
                    }else if(info.getState().equals(NetworkInfo.State.CONNECTED)){
                        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        currentNetWorkId = wifiInfo.getNetworkId();
                        Log.i("MainActivity","连接到wifi网络--->"+wifiInfo.getSSID());
//                        int p = listView.getSelectedItemPosition();
                        //是否连接到目标AP
                        int p = listView.getCheckedItemPosition();
                        if(p >= 0){
                            String ssid = apNameList.get(p);
                            String currentSSID = wifiInfo.getSSID();
                            if(currentSSID.equals("\"" + ssid + "\"")){
                                //连接到设备AP,会广播2次，所以通过变量isConnectedDeviceAP控制，处理一次
                                if(!isConnectedDeviceAP && isChangingAP){
                                    changeAPSuccess(ssid);
                                }
                            }else{
                                if(isChangingAP) {
                                    changeAPFail();
                                }
                            }
                        }
                    }else if(info.getState().equals(NetworkInfo.State.SUSPENDED)){
                        //其他状态
                        Log.i("MainActivity","wifi网络SUSPENDED！");
                        currentNetWorkId = -1;
                        if(isChangingAP) {
                            changeAPFail();
                        }
                    }else if(info.getState().equals(NetworkInfo.State.UNKNOWN)){
                        Log.i("MainActivity","wifi网络UNKNOWN！");
                        currentNetWorkId = -1;
                        if(isChangingAP) {
                            changeAPFail();
                        }
                    }else if(info.getState().equals(NetworkInfo.State.CONNECTING)){
                        Log.i("MainActivity","CONNECTING wifi网络："+info.getExtraInfo());
                    }else if(info.getState().equals(NetworkInfo.State.DISCONNECTING)){
                        Log.i("MainActivity","DISCONNECTING wifi网络："+info.getExtraInfo());
                    }else{
                        Log.e("MainActivity","未知wifi网络状态-->"+info.getState());
                        currentNetWorkId = -1;
                    }
                }
                return;
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                {
                    //wifi状态变化
                    int status = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,-1);
                    Log.i("MainActivity","wifi status:"+status);
                    if(status == WifiManager.WIFI_STATE_ENABLED && isChangingAP){
                        connectDeviceAP();
                    }

                }
                return;
            }
        }
    };

    //尝试连接连接设备AP
    private void connectDeviceAP() {
        //如果已经连接直接进入
        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo currentWifiInfo = mWifiManager.getConnectionInfo();
        if(currentWifiInfo != null) {
            String s1 = currentWifiInfo.getSSID();
            String s2 = getSelectedSSID();
            if (currentWifiInfo.getSSID().equals("\"" + getSelectedSSID() + "\"")) {
                startSettingActivity(getSelectedSSID());
                return;
            }
        }

        if(!changeAP()){
            changeAPFail();
        }else{
            showChangeAPProgress();
        }
    }

    //显示正在切换到的设备AP的进度条
    private void showChangeAPProgress() {

        if(progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("正在连接设备");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //成功切换到设备AP
    private void changeAPSuccess(String ssid){
        isChangingAP = false;
        isConnectedDeviceAP = true;
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        startSettingActivity(ssid);
    }

    //切换到设备AP失败
    private void changeAPFail(){
        isChangingAP = false;
        isConnectedDeviceAP = false;
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        Toast.makeText(MainActivity.this,"无法连接到设备信号",Toast.LENGTH_SHORT).show();
    }

    private void startSettingActivity(String ssid) {
        Intent intent = new Intent(MainActivity.this,SettingActivity.class);
        intent.putExtra("ssid",ssid);
        startActivityForResult(intent,REQUEST_SETTING);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        tvEmptyDataHint = findViewById(R.id.tv_hint);
        listView = findViewById(android.R.id.list);
        apNameList = new ArrayList<String>();



        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,R.layout.list_menu_item,apNameList));

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,SearchDeviceActivity.class),REQUEST_SCAN);
            }
        });

        findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chanage access point
                int p = listView.getCheckedItemPosition();
                if(p < 0){
                    Toast.makeText(MainActivity.this,"请选择要连接的设备！",Toast.LENGTH_SHORT).show();
                    return;
                }
                connectDeviceAP();
            }
        });

        SharePreferencesUtil spUtil = new SharePreferencesUtil(MainActivity.this);
        String[] ss = spUtil.getSharedPreference(Constants.DEV_AP_RECORD);
        if(ss != null && ss.length > 0){
            for(String ap : ss){
                apNameList.add(ap);
            }
            ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
        }else {
            tvEmptyDataHint.setVisibility(View.VISIBLE);
            startActivityForResult(new Intent(MainActivity.this,SearchDeviceActivity.class),REQUEST_SCAN);
        }
    }

    private void initData() {
        isChangingAP = false;

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo currentWifiInfo = mWifiManager.getConnectionInfo();
        currentNetWorkId = currentWifiInfo.getNetworkId();
    }

    @Override
    protected void onResume() {
        super.onResume();

        isConnectedDeviceAP = false;

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver,filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiReceiver);
        isChangingAP = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String getSelectedSSID(){
        int p = listView.getCheckedItemPosition();
        if(p < 0){
            return null;
        }
        return apNameList.get(p);
    }

    //尝试切换到设备AP
    //返回值只代表操作是否成功，不代表业务是否成功
    private boolean changeAP() {
        isChangingAP = true;
        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if(!mWifiManager.isWifiEnabled()){
            //WIFI未打开
            mWifiManager.setWifiEnabled(true);
            return false;
        }

        WifiInfo currentWifiInfo = mWifiManager.getConnectionInfo();
        //连接到指定AP
        int p = listView.getCheckedItemPosition();
//        int p = listView.getSelectedItemPosition();
        if(p < 0){
            return false;
        }
        String ssid = apNameList.get(p);

        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        if(list == null){
            list = new ArrayList<>();
        }

        //是否保存过AP
//        for (WifiConfiguration wifiConfiguration : list) {
//            Log.e("MainActivity", "ssid:" + wifiConfiguration.SSID);
//            if (wifiConfiguration.SSID != null && wifiConfiguration.SSID.equals("\"" + ssid + "\"")) {
////                WifiInfo currentWifiInfo = mWifiManager.getConnectionInfo();
//                currentNetWorkId = currentWifiInfo.getNetworkId();
////                boolean b = mWifiManager.disconnect();
////                if(!b){
////                    Toast.makeText(MainActivity.this,"断开WIFI操作失败",Toast.LENGTH_SHORT).show();
////                    return;
////                }
//                if(currentNetWorkId != -1) {
//                    mWifiManager.disableNetwork(currentNetWorkId);
//                }
//                boolean b = mWifiManager.enableNetwork(wifiConfiguration.networkId,true);
//                if(!b){
//                    Toast.makeText(MainActivity.this,"切换AP操作失败",Toast.LENGTH_SHORT).show();
//                    return false;
//                }
////                b = mWifiManager.reconnect();
////                if(!b){
////                    Toast.makeText(MainActivity.this,"连接WIFI操作失败",Toast.LENGTH_SHORT).show();
////                    return;
////                }
//                return true;
//            }
//        }

        //未保存过AP add conf
        WifiConfiguration wificonf = createWifiConfig("\"" + ssid   + "\"","\"" + "3.14159265" + "\"",WIFICIPHER_WPA);
        int networkId = mWifiManager.addNetwork(wificonf);

//        WifiInfo currentWifiInfo = mWifiManager.getConnectionInfo();
        currentNetWorkId = currentWifiInfo.getNetworkId();
//        boolean b = mWifiManager.disconnect();
//        if(!b){
//            Toast.makeText(MainActivity.this,"断开WIFI操作失败",Toast.LENGTH_SHORT).show();
//            return;
//        }
        if(currentNetWorkId != -1) {
            mWifiManager.disableNetwork(currentNetWorkId);
        }
        boolean b = mWifiManager.enableNetwork(networkId, true);
        if(!b){
            Toast.makeText(MainActivity.this,"连接到设备失败",Toast.LENGTH_SHORT).show();
            return false;
        }
        b = mWifiManager.reconnect();

        if(!b){
            Toast.makeText(MainActivity.this,"连接WIFI操作失败",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SCAN  && resultCode == RESULT_OK){
            if(data == null){
                return;
            }
            List<ScanResult> wifiList = data.getParcelableArrayListExtra("wifi_info");
            if(wifiList == null){
                return;
            }
            scanResultList = new ArrayList<ScanResult>();
            apNameList.clear();
            for (ScanResult r : wifiList){
//                Log.e("MainActivity","--->"+r.SSID);
                scanResultList.add(r);
                apNameList.add(r.SSID);
            }

            if(scanResultList.size() > 0){
                tvEmptyDataHint.setVisibility(View.INVISIBLE);
            }else{
                tvEmptyDataHint.setVisibility(View.VISIBLE);
            }

            ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
            return;
        }

        if(requestCode == REQUEST_SETTING){
            //从设置界面返回的话恢复原来网络连接状态
//            restoreWifiInfo();不恢复原来的网络了
        }

    }

    //恢复本来的网络状态
    public void restoreWifiInfo() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.disconnect();
        int networkId = currentNetWorkId;
        if (networkId >= 0) {
            wifiManager.enableNetwork(networkId, true);
        } else {
            // 3G
//            wifiManager.setWifiEnabled(false);
        }

    }

    private WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\""+ssid+"\"")) {
                return config;
            }
        }
        return null;
    }


    private static final int WIFICIPHER_NOPASS = 0;
    private static final int WIFICIPHER_WEP = 1;
    private static final int WIFICIPHER_WPA = 2;
    private WifiConfiguration createWifiConfig(String ssid, String password, int type) {
        //初始化WifiConfiguration
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        //指定对应的SSID
        config.SSID = "\"" + ssid + "\"";
        //如果之前有类似的配置
        WifiConfiguration tempConfig = isExist(ssid);
        if(tempConfig != null) {
            //则清除旧有配置
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        //不需要密码的场景
        if(type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //以WEP加密的场景
        } else if(type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
        } else if(type == WIFICIPHER_WPA) {
            config.preSharedKey = "\""+password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }
}