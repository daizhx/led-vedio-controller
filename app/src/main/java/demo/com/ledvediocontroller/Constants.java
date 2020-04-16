package demo.com.ledvediocontroller;

import demo.com.ledvediocontroller.util.BytesHexStrTranslate;

public class Constants {
    public static final String DEV_AP_RECORD = "DEV_AP_RECORD";

    public static final String TCP_SERVER_IP = "192.168.4.1";
    public static final int TCP_SERVER_PORT = 866;
    public static final int CONNECT_TIME_OUT = 2*5000;

    public static final byte[] CV = new byte[]{0x08, (byte) 0xAB,0x02,0x00,0x00,0x00,0x00,0x4B};
    public static final byte[] VGA = new byte[]{0x08, (byte) 0xAB,0x02,0x00,0x02,0x00,0x00,0x49};
    public static final byte[] DP1 = new byte[]{0x08, (byte) 0xAB,0x02,0x00,0x06,0x00,0x00,0x45};
    public static final byte[] DP2 = new byte[]{0x08, (byte) 0xAB,0x02,0x00,0x07,0x00,0x00,0x44};
    public static final byte[] HDM1 = new byte[]{0x08, (byte) 0xAB,0x02,0x00,0x04,0x00,0x00,0x47};
    public static final byte[] HDM2 = new byte[]{0x08, (byte) 0xAB,0x02,0x00,0x05,0x00,0x00,0x46};

    public static final byte[] DVI = new byte[]{0x08,(byte)0xAB,0x02,0x00,0x03,0x00,0x00,0x48};
    public static final byte[] SDI = new byte[]{0x08,(byte)0xAB,0x02,0x00,0x08,0x00,0x00,0x43};
    public static final byte[] USB = new byte[]{0x08,(byte)0xAB,0x02,0x00,0x08,0x00,0x00,0x43};
    public static final byte[] EXT = new byte[]{0x08,(byte)0xAB,0x02,0x00,0x01,0x00,0x00,0x4A};
    public static final byte[] LayerA = new byte[]{0x08,(byte)0xAB,0x01,0x00,0x00,0x00,0x00,0x4C};
    public static final byte[] LayerB = new byte[]{0x08,(byte)0xAB,0x01,0x01,0x00,0x00,0x00,0x4B};
    public static final byte[] LayerC = new byte[]{0x08,(byte)0xAB,0x01,0x02,0x00,0x00,0x00,0x4A};
    public static final byte[] LayerD = new byte[]{0x08,(byte)0xAB,0x01,0x03,0x00,0x00,0x00,0x49};
    //黑屏
    public static final byte[] BLACK = new byte[]{0x08, (byte) 0xAB,0x03,0x00,0x01,0x00,0x00,0x49};
    //冻结
    public static final byte[] FREEZE= new byte[]{0x08, (byte) 0xAB,0x03,0x00,0x00,0x00,0x00,0x4A};
    //A开关
    public static final byte[] ATOGGLE = new byte[]{0x08,(byte)0xAB,0x0C,0x00,0x00,0x00,0x00,0x41};
    //A镜像
    public static final byte[] AImage = new byte[]{0x08,(byte)0xAB,0x06,0x00,0x00,0x00,0x00,0x47};

    //B开关
    public static final byte[] BTOGGLE = new byte[]{0x08,(byte)0xAB,0x0C,0x01,0x00,0x00,0x00,0x40};
    //B镜像
    public static final byte[] BImage = new byte[]{0x08,(byte)0xAB,0x06,0x01,0x00,0x00,0x00,0x46};

    //C开关
    public static final byte[] CTOGGLE = new byte[]{0x08,(byte)0xAB,0x0C,0x02,0x00,0x00,0x00,0x3F};
    //C镜像
    public static final byte[] CImage = new byte[]{0x08,(byte)0xAB,0x06,0x02,0x00,0x00,0x00,0x45};

    //D开关
    public static final byte[] DTOGGLE = new byte[]{0x08,(byte)0xAB,0x0C,0x03,0x00,0x00,0x00,0x3E};
    //D镜像
    public static final byte[] DImage = new byte[]{0x08,(byte)0xAB,0x06,0x03,0x00,0x00,0x00,0x44};

    public static final byte[] M1 = new byte[]{0x08,(byte)0xAB,0x00,0x00,0x01,0x00,0x00,0x4C};
    public static final byte[] M2 = new byte[]{0x08,(byte)0xAB,0x00,0x00,0x01,0x01,0x00,0x4B};
    public static final byte[] M3 = new byte[]{0x08,(byte)0xAB,0x00,0x00,0x01,0x02,0x00,0x4A};
    public static final byte[] M4 = new byte[]{0x08,(byte)0xAB,0x00,0x00,0x01,0x03,0x00,0x49};
    public static final byte[] M5 = new byte[]{0x08,(byte)0xAB,0x00,0x00,0x01,0x04,0x00,0x48};
    public static final byte[] M6 = new byte[]{0x08,(byte)0xAB,0x00,0x00,0x01,0x05,0x00,0x47};
    public static final byte[] M7 = new byte[]{0x08,(byte)0xAB,0x00,0x00,0x01,0x06,0x00,0x46};
    public static final byte[] M8 = new byte[]{0x08,(byte)0xAB,0x00,0x00,0x01,0x07,0x00,0x45};
    public static final byte[] M9 = new byte[]{0x08,(byte)0xAB,0x00,0x00,0x01,0x08,0x00,0x44};
    public static final byte[] M10 = new byte[]{0x08,(byte)0xAB,0x00,0x00,0x01,0x09,0x00,0x43};


    //复位
    public static final byte[] RESET = new byte[]{0x08,(byte)0xAB,0x0E,0x00,0x00,0x00,0x00,0x3F};

    //向模块索要IP
    public static final byte[] GIVE_IP = new byte[]{0x08, (byte) 0xAB,0x22,0x00,0x00,0x00,0x00,0x2B};


    /**
     * 亮度调节
     * @param v 1-100
     * @return
     */
    public static final byte[] getLightCommand(int v){
        byte[] c = new byte[8];
        c[0] = 0x08;
        c[1] = (byte) 0xAB;
        c[2] = 0x08;
        c[3] = 0x00;
        c[4] = (byte) v;
        c[5] = 0x00;
        c[6] = 0x00;
        c[7] = (byte) (256 - (0x08 + 0xAB + 0x08 + 0x00 + v + 0x00 + 0x00)%256);
        return c;
    }

    public static final byte[] getSSIDConfigBytes(String ssid){
        byte[] ss = BytesHexStrTranslate.toBytes(ssid);
        int l = 4 + ss.length;
        byte[] ret = new byte[l];
        ret[0] = (byte) l;
        ret[1] = (byte) 0xAB;
        ret[2] = 0x20;
        for(int i=0;i<ss.length;i++){
            ret[3+i] = ss[i];
        }
        //计算校验和
        int sum = 0;
        for(int i=0;i<(ret.length - 1);i++){
            sum += ret[i];
        }
        byte check = (byte) (256 - sum%256);
        ret[l-1] = check;

        return ret;
    }

    public static final byte[] getSSIDPWDConfigBytes(String pwd){
        byte[] ss = BytesHexStrTranslate.toBytes(pwd);
        int l = 4 + ss.length;
        byte[] ret = new byte[l];
        ret[0] = (byte) l;
        ret[1] = (byte) 0xAB;
        ret[2] = 0x21;
        for(int i=0;i<ss.length;i++){
            ret[3+i] = ss[i];
        }
        //计算校验和
        int sum = 0;
        for(int i=0;i<(ret.length - 1);i++){
            sum += ret[i];
        }
        byte check = (byte) (256 - sum%256);
        ret[l-1] = check;

        return ret;
    }

}