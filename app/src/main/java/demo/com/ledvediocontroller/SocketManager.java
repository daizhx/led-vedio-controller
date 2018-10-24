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
    InetSocketAddress address;
    private volatile InputStream inputStream;
    private volatile OutputStream outputStream;

    //记录连接状态
    private volatile int status = DISCONNECTED;
    public final static int DISCONNECTED  = 0;
    public final static int CONNECTED  = 1;

    private SocketOperatorListener socketOperatorListener;

    public interface SocketOperatorListener {
        void onConnect(boolean b);
        void onRead(byte[] data);
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
        address = new InetSocketAddress(Constants.TCP_SERVER_IP,Constants.TCP_SERVER_PORT);
    }

    //建立连接
    public void connect(){
        if(socket != null && socket.isConnected()){
            if(socketOperatorListener != null){
                socketOperatorListener.onConnect(true);
                return;
            }
        }

        ThreadPoolProxy tpp = ThreadPoolProxy.getInstance();
        tpp.execute(new Runnable() {
            @Override
            public void run() {
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
                        while (inputStream.read(data, 0, data.length) != 0) {
                            if(socketOperatorListener != null) {
                                socketOperatorListener.onRead(data);
                            }
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
    public void disconnect(){
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
