package demo.com.ledvediocontroller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketManager{

    private static SocketManager instance;

    private Socket socket;

    //记录连接状态
    private volatile int status = DISCONNECTED;
    public final static int DISCONNECTED  = 0;
    public final static int CONNECTED  = 1;

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
        new Thread(){
            @Override
            public void run() {
                socket = new Socket();
                InetSocketAddress address = new InetSocketAddress(Constants.TCP_SERVER_IP,Constants.TCP_SERVER_PORT);
                //创建连接
                try {
                    socket.connect(address,Constants.CONNECT_TIME_OUT);
                    status = CONNECTED;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


}
