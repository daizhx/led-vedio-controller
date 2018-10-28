package demo.com.ledvediocontroller;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import demo.com.ledvediocontroller.util.BytesHexStrTranslate;
import demo.com.ledvediocontroller.util.ThreadPoolProxy;

public class SocketManager{

    private static SocketManager instance;

    private volatile Socket socket;
    private volatile InetSocketAddress address;
    private volatile InputStream inputStream;
    private volatile OutputStream outputStream;

    //记录连接状态
    private volatile int status = DISCONNECTED;
    public final static int DISCONNECTED  = 0;
    public final static int CONNECTED  = 1;

    private volatile String ip;

    private SocketOperatorListener socketOperatorListener;

    public interface SocketOperatorListener {
        void onConnect(boolean b);
        void onRead(String data);
        void onWrite(String hexString,boolean b);
    }

    public static SocketManager getInstance(){
        if(instance == null){
            synchronized (SocketManager.class){
                if(instance == null){
                    instance = new SocketManager();
                }
            }
        }
        return instance;
    }

    private SocketManager() {

    }

    //建立连接
    public void connect(String ip){
        if(ip.equals(this.ip) && socket != null && socket.isConnected()){
            if(socketOperatorListener != null){
                socketOperatorListener.onConnect(true);
                return;
            }
        }

        this.ip = ip;
        address = new InetSocketAddress(Constants.TCP_SERVER_IP,Constants.TCP_SERVER_PORT);
        close();



        ThreadPoolProxy tpp = ThreadPoolProxy.getInstance();
        tpp.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (SocketManager.this){
                    if(socket != null && socket.isConnected()){
                        if(socketOperatorListener != null){
                            socketOperatorListener.onConnect(true);
                            return;
                        }
                    }

                    socket = new Socket();
                    //创建连接
                    try {
                        socket.connect(address,Constants.CONNECT_TIME_OUT);
                        status = CONNECTED;
                        if(socketOperatorListener != null){
                            socketOperatorListener.onConnect(true);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        status = DISCONNECTED;

                        if(socketOperatorListener != null){
                            socketOperatorListener.onConnect(false);
                        }
                        return;
                    }

                    try {
                        inputStream = socket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                        inputStream = null;
                    }

                    try {
                        outputStream = socket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                        outputStream = null;
                    }
                }

            }
        });

    }

    //从socket读取数据
    public boolean readData(){
        ThreadPoolProxy tpp = ThreadPoolProxy.getInstance();

        if(inputStream == null || socket == null || socket.isInputShutdown()){
            return false;
        }

        tpp.execute(new Runnable() {
            @Override
            public void run() {
                byte[] data = new byte[10];
                try {
                    if(inputStream != null) {
                        Log.i("SocketManager","readData......");
                        int l = inputStream.read(data, 0, data.length);
                        String str = new String(data,0,l);
                        if(socketOperatorListener != null) {
                            socketOperatorListener.onRead(str);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if(socketOperatorListener != null) {
                        socketOperatorListener.onRead(null);
                    }
                }

                Log.i("SocketManager","readData done");
            }
        });
        return true;
    }

    //写数据到socket中
    public boolean writeData(final byte[] data){
        ThreadPoolProxy tpp = ThreadPoolProxy.getInstance();
        if(outputStream == null || socket == null || socket.isOutputShutdown()){
            return false;
        }
        tpp.execute(new Runnable() {
            @Override
            public void run() {
                if(outputStream != null){
                    try {
                        outputStream.write(data);
                        if(socketOperatorListener != null) {
                            socketOperatorListener.onWrite(BytesHexStrTranslate.bytesToHexFun2(data),true);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if(socketOperatorListener != null) {
                            socketOperatorListener.onWrite(BytesHexStrTranslate.bytesToHexFun2(data),false);
                        }
                    }
                }
            }
        });
        return true;
    }

    public void setSocketOperatorListener(SocketOperatorListener socketOperatorListener) {
        this.socketOperatorListener = socketOperatorListener;
    }

    //关闭连接，释放资源
    public void close(){
        try {
            if(inputStream != null) {
                inputStream.close();
            }

            if(outputStream != null) {
                outputStream.close();
            }

            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            inputStream = null;
            outputStream = null;
        }
    }
}
