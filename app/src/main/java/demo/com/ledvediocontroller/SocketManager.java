package demo.com.ledvediocontroller;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

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

    public interface SocketOperatorListener {
        boolean onRead(byte[] data);
        void onWrite(boolean b);
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
            return;
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
                } catch (IOException e) {
                    e.printStackTrace();
                    status = DISCONNECTED;
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
    public boolean readData(final SocketOperatorListener handler){
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
                        while (inputStream.read(data, 0, data.length) != 0) {
                            handler.onRead(data);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.onRead(null);
                }

                Log.d("test","readData done");
            }
        });
        return true;
    }

    //写数据到socket中
    public boolean writeData(final byte[] data, final SocketOperatorListener handler){
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
                        handler.onWrite(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.onWrite(false);
                    }
                }
            }
        });
        return true;
    }


}
