package demo.com.ledvediocontroller.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import demo.com.ledvediocontroller.Constants;
import demo.com.ledvediocontroller.R;
import demo.com.ledvediocontroller.SocketManager;

//输入WIFF SSID和pwd到设备
public class InputWifiDialogFragment extends AppCompatActivity {

    private List<String> ssidList;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private AppCompatSpinner spinner;
    private EditText etPwd;

    private ProgressDialog progressDialog;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //扫描热点结束
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                checkScanResult(mWifiManager.getScanResults());
            }

        }
    };

    private void checkScanResult(List<ScanResult> scanResults) {
        for(ScanResult scanResult : scanResults){
                ssidList.add(scanResult.SSID);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_input_wifi);

        spinner = findViewById(R.id.input_ssid);
        if(ssidList == null){
            ssidList = new ArrayList<String>();
        }
        progressBar = findViewById(R.id.progress_horizontal);
        adapter = new ArrayAdapter<String>(InputWifiDialogFragment.this,android.R.layout.simple_spinner_item,ssidList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etPwd = findViewById(R.id.et_input_pwd);
        findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIP2Device();
            }
        });


        setFinishOnTouchOutside(false);

        progressDialog = new ProgressDialog(InputWifiDialogFragment.this);
    }


    private void sendIP2Device() {
        //TODO
        String ssid = (String) spinner.getSelectedItem();
        String pwd = etPwd.getText().toString();

        SocketManager sm = SocketManager.getInstance();
        sm.setSocketOperatorListener(new SocketManager.SocketOperatorListener() {
            @Override
            public void onConnect(boolean b) {

            }

            @Override
            public void onRead(String data) {
                progressDialog.dismiss();
                if("0.0.0.0".equals(data)){
                    Toast.makeText(InputWifiDialogFragment.this,"配置路由器失败",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("ip",data);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }

            @Override
            public void onWrite(String hexString, boolean b) {
                if(b){
                    progressDialog.setMessage("正在配置路由器信息...");
                    progressDialog.show();
                }else {
                    Toast.makeText(InputWifiDialogFragment.this,"无法连接设备",Toast.LENGTH_SHORT).show();
                }

            }
        });
        sm.readData();
        sm.writeData(Constants.getSSIDConfigBytes(ssid));
        sm.writeData(Constants.getSSIDPWDConfigBytes(pwd));

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mBroadcastReceiver,intentFilter);

        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }


}
