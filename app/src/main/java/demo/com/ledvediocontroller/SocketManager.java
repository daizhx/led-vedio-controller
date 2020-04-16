package demo.com.ledvediocontroller;

import android.os.Handler;
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

    //在主线程UI线程上执行回调函数
    private Handler handler;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public interface SocketOperatorListener {
        void onSocketConnect(String ip,boolean b);
        void onSocketRead(String data);
        void onSocketWrite(String hexString,boolean b);
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

    //判断sockect是否连接服务器，不能通过socket.isConnected来判断
    public boolean isConnected(){
        try {
            if(socket != null) {
                socket.sendUrgentData(0xff);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //建立连接
    public void connect(final String ip){
        if(ip.equals(this.ip) && isConnected()){
            if(socketOperatorListener != null && handler != null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        socketOperatorListener.onSocketConnect(ip,true);
                    }
                });
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
                //根据IP创建sokcet连接
                synchronized (SocketManager.this){
                    if(socket != null){
                        return;
                    }
                    socket = new Socket();
                    //创建连接
                    try {
                        socket.connect(address,Constants.CONNECT_TIME_OUT);
                        status = CONNECTED;
                        if(socketOperatorListener != null && handler != null){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    socketOperatorListener.onSocketConnect(ip,true);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        status = DISCONNECTED;

                        if(socketOperatorListener != null && handler != null){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    socketOperatorListener.onSocketConnect(ip,false);
                                }
                            });

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
    public void readData(){
        ThreadPoolProxy tpp = ThreadPoolProxy.getInstance();

        if(inputStream == null || socket == null || socket.isInputShutdown()){
            return;
        }

        tpp.execute(new Runnable() {
            @Override
            public void run() {
                byte[] data = new byte[10];
                try {
                    if(inputStream != null) {
                        Log.i("SocketManager","readData......");
                        int l = inputStream.read(data, 0, data.length);
                        final String str = new String(data,0,l);
                        if(socketOperatorListener != null && handler != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    socketOperatorListener.onSocketRead(str);
                                }
                            });

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if(socketOperatorListener != null  && handler != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                socketOperatorListener.onSocketRead(null);
                            }
                        });

                    }
                }

                Log.i("SocketManager","readData done");
            }
        });
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
//                        if(socketOperatorListener != null) {
//                            socketOperatorListener.onWrite(BytesHexStrTranslate.bytesToHexFun2(data),true);
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
//                        if(socketOperatorListener != null) {
//                            socketOperatorListener.onWrite(BytesHexStrTranslate.bytesToHexFun2(data),false);
//                        }
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
            socket = null;
        }
    }
}
