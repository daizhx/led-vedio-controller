package demo.com.ledvediocontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import demo.com.ledvediocontroller.fragments.FunctionFragment;
import demo.com.ledvediocontroller.fragments.PreSettingFragment;
import demo.com.ledvediocontroller.fragments.ResetDialogFragment;
import demo.com.ledvediocontroller.fragments.SetLightDialogFragment;
import demo.com.ledvediocontroller.fragments.SettingFragment;
import demo.com.ledvediocontroller.fragments.SignalSourceFragment;
import demo.com.ledvediocontroller.util.BottomNavigationViewHelper;

public class SettingActivity extends AppCompatActivity
        implements SettingFragment.OnFragmentInteractionListener,
        SignalSourceFragment.OnFragmentInteractionListener,
        PreSettingFragment.OnFragmentInteractionListener,
        SetLightDialogFragment.OnSetLightListener,ResetDialogFragment.OnResetListener {

    SignalSourceFragment signalSourceFragment;
    FunctionFragment functionFragment;
    PreSettingFragment preSettingFragment;
    SettingFragment settingFragment;

    SetLightDialogFragment setLightDialogFragment;
    ResetDialogFragment resetDialogFragment;


    private Socket socket;
    private SocketThread socketThread;
    private BlockingQueue<byte[]> blockingQueue;

    private MyHandler handler;

    //无法连接TCP/IP服务
    public static final int MSG_TCP_ERROR = 1;
    //socket发送命令失败
    public static final int MSG_SOCKET_WRITE_ERROR = 2;

    private static class MyHandler extends Handler{
        private WeakReference<SettingActivity> settingActivityWeakReference;
        public MyHandler(WeakReference<SettingActivity> settingActivityWeakReference) {
            this.settingActivityWeakReference = settingActivityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            SettingActivity activity = settingActivityWeakReference.get();
            if(activity != null) {
                int w = msg.what;
                if (w == MSG_TCP_ERROR) {
//                    Toast.makeText(activity, "无法建立TCP连接！", Toast.LENGTH_SHORT).show();
                }else if(w == MSG_SOCKET_WRITE_ERROR){
                    Toast.makeText(activity, "发送控制指令失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


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
                        setTitle("wifi未连接");
                    }else if(info.getState().equals(NetworkInfo.State.CONNECTED)){
                        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        Log.i("MainActivity","连接到wifi网络--->"+wifiInfo.getSSID());
                        setTitle("wifi连接到"+wifiInfo.getSSID());
                    }else if(info.getState().equals(NetworkInfo.State.SUSPENDED)){
                        //其他状态
                        Log.i("MainActivity","wifi网络SUSPENDED！");
                        setTitle("wifi未连接");
                    }else if(info.getState().equals(NetworkInfo.State.UNKNOWN)){
                        Log.i("MainActivity","wifi网络UNKNOWN！");
                        setTitle("wifi未连接");
                    }else if(info.getState().equals(NetworkInfo.State.CONNECTING)){
                        Log.i("MainActivity","CONNECTING wifi网络："+info.getExtraInfo());
                        setTitle("wifi未连接");
                    }else if(info.getState().equals(NetworkInfo.State.DISCONNECTING)){
                        Log.i("MainActivity","DISCONNECTING wifi网络："+info.getExtraInfo());
                        setTitle("wifi未连接");
                    }else{
                        Log.e("MainActivity","未知wifi网络状态-->"+info.getState());
                        setTitle("wifi未连接");
                    }
                }
                return;
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                {
                    //wifi状态变化
                    int status = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,-1);
                    Log.i("MainActivity","wifi status:"+status);
                    if(status == WifiManager.WIFI_STATE_DISABLED){
//                        setTitle("wifi已关闭");
                    }else if(status == WifiManager.WIFI_STATE_ENABLED){
//                        setTitle("wifi已打开");
                    }
                }
                return;
            }
        }
    };



    private class SocketThread extends Thread{
        private InetSocketAddress address = new InetSocketAddress(Constants.TCP_SERVER_IP,Constants.TCP_SERVER_PORT);
        private boolean isSocketConnected = false;
        private boolean runningFlag = true;

        public void stopRunning(){
            runningFlag = false;
        }

        public boolean isConnected(){
            return isSocketConnected;
        }

        @Override
        public void run() {
            while (runningFlag) {
                isSocketConnected = false;
                Log.i("SocketThread","create sockect....");
                socket = new Socket();
                try {
                    Log.i("SocketThread","sockect connectting....");
                    socket.connect(address,Constants.CONNECT_TIME_OUT);
                    isSocketConnected = true;

                }  catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    if(handler != null) {
                        handler.sendEmptyMessage(MSG_TCP_ERROR);
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }

                Log.i("SocketThread","sockect waitting command....");
                byte[] bytes;
                try {
                    bytes = blockingQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //关闭socket连接
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }

                try {
                    OutputStream os = socket.getOutputStream();
                    os.write(bytes);
                    os.flush();
                    os.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    if(handler != null) {
                        handler.sendEmptyMessage(MSG_SOCKET_WRITE_ERROR);
                    }
                    continue;
                }

            }
            Log.i("SocketThread","SocketThread stop....");

        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_signal_source:
                    showContentView(signalSourceFragment);
                    return true;
//                case R.id.navigation_function:
//                    if(functionFragment == null){
//                        functionFragment = FunctionFragment.newInstance();
//                    }
//                    showContentView(functionFragment);
//                    return true;
                case R.id.navigation_pre_setting:
                    if(preSettingFragment == null){
                        preSettingFragment = PreSettingFragment.newInstance();
                    }
                    showContentView(preSettingFragment);
                    return true;
                case R.id.navigation_setting:
                    if(settingFragment == null){
                        settingFragment = SettingFragment.newInstance();
                    }
                    showContentView(settingFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Intent intent = getIntent();
        String ssid = intent.getStringExtra("ssid");
        setTitle("连接至"+ssid);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(signalSourceFragment == null) {
            signalSourceFragment = SignalSourceFragment.newInstance();
        }
        showContentView(signalSourceFragment);


//        WeakReference<SettingActivity> wr = new WeakReference<SettingActivity>(SettingActivity.this);
//        handler = new MyHandler(wr);
//        blockingQueue = new LinkedBlockingDeque<>(1);



    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver,filter);

        socketThread = new SocketThread();
        socketThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //释放广播接收器
        unregisterReceiver(wifiReceiver);

        socketThread.stopRunning();
        socketThread.interrupt();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放handler
        handler = null;
    }

    //发送命令
    public void sendCommand(byte[] command){
        blockingQueue.clear();

        if(!socketThread.isConnected()){
            //无法发送命令
            Toast.makeText(SettingActivity.this,"未连接到设备，无法控制设备！",Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            blockingQueue.put(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendCommand(byte[] command, SendCommandResult result) {
        if(!socketThread.isConnected()){
            result.sendCommandFail();
            return;
        }else{
            result.sendCommandSuccess();
            sendCommand(command);
        }

    }

    private void showContentView(Fragment f){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_content,f);
//        ft.show(f);
//        ft.add(R.id.fl_content,signalSourceFragment);
        ft.commit();
    }

    @Override
    public void selectDeviceType() {
        startActivity(new Intent(SettingActivity.this,DeviceTypeActivity.class));
    }

    @Override
    public void setlanguage() {
        startActivity(new Intent(SettingActivity.this,SetLanguageActivity.class));
    }

    @Override
    public void setLight() {
        if(setLightDialogFragment == null) {
            setLightDialogFragment = new SetLightDialogFragment();
            setLightDialogFragment.setOnSetLightListener(SettingActivity.this);
        }
        setLightDialogFragment.show(getSupportFragmentManager(),"light");
    }

    @Override
    public void setTime() {
        startActivity(new Intent(SettingActivity.this,SetDateTimeActivity.class));
    }

    @Override
    public void resetDevice() {
        if(resetDialogFragment == null){
            resetDialogFragment = new ResetDialogFragment();
            resetDialogFragment.setOnResetListener(SettingActivity.this);
        }
        resetDialogFragment.show(getSupportFragmentManager(),"reset");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
