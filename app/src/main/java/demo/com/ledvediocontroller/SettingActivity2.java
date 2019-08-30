package demo.com.ledvediocontroller;

import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class SettingActivity2 extends SettingActivity {

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
        findViewById(R.id.btn_show_key_board).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyBoard();
            }
        });

        editText = findViewById(R.id.edit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.d("daizhx","beforeTextChanged--------->" + s + ",start="+start+",count="+count+",after="+after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d("daizhx","onTextChanged--------->" + s + ",start="+start+",count="+count+",before="+before);
                if(s.length() > 0) {
                    char c = s.charAt(s.length() - 1);
                    byte[] bytes = charToByte(c);
                    getKeyboardCommand(bytes);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.d("daizhx","afterTextChanged--------->" + s.toString());
            }
        });

        findViewById(R.id.view_touch).setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(SettingActivity2.this, new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                    Log.d("daizhx","onScroll-->distanceX=" + distanceX + ",distanceY=" + distanceY);
                    mouseMove(distanceX,distanceY);
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        findViewById(R.id.btn_mouse_left).setOnClickListener(new View.OnClickListener() {
            long lastClick = 0;
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                if(now - lastClick < 1000){
                    doubleClick();
                }else {
                    clickMouseLeft();
                }
                lastClick = now;
            }
        });

        findViewById(R.id.btn_mouse_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMouseRight();
            }
        });

        findViewById(R.id.btn_page_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageUp();
            }
        });

        findViewById(R.id.btn_page_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageDown();
            }
        });

        findViewById(R.id.btn_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenCapture();
            }
        });

        findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        findViewById(R.id.btn_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up();
            }
        });

        findViewById(R.id.btn_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down();
            }
        });

        findViewById(R.id.btn_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left();
            }
        });

        findViewById(R.id.btn_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                right();
            }
        });


        //鼠标滚轮
        findViewById(R.id.iv_scroller).setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(SettingActivity2.this, new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                    Log.d("daizhx","onScroll-->distanceX=" + distanceX + ",distanceY=" + distanceY);
                    if(distanceY > 0){
                        scrollUp();
                    }else{
                        scrollDown();
                    }
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }

    private void mouseMove(float distanceX, float distanceY) {
        sendCommand(getCursorCommand(distanceX,distanceY));
    }

    private void scrollDown() {
        sendCommand(getMouseCommand((byte) 0x05));
    }

    private void scrollUp() {
        sendCommand(getMouseCommand((byte) 0x04));
    }

    private void right() {
        sendCommand(getFuncCommand((byte) 0x03));
    }

    private void left() {
        sendCommand(getFuncCommand((byte) 0x02));
    }

    private void down() {
        sendCommand(getFuncCommand((byte) 0x01));
    }


    private void up() {
        sendCommand(getFuncCommand((byte) 0x00));
    }

    //删除
    private void delete() {
        sendCommand(getFuncCommand((byte) 0x07));
    }

    //截图
    private void screenCapture() {
        sendCommand(getFuncCommand((byte) 0x06));
    }

    //下翻
    private void pageDown() {
        sendCommand(getFuncCommand((byte) 0x05));
    }

    //上翻
    private void pageUp() {
        sendCommand(getFuncCommand((byte) 0x04));
    }

    //鼠标右键
    private void clickMouseRight() {
        sendCommand(getMouseCommand((byte) 0x03));
    }

    //鼠标左键
    private void clickMouseLeft() {
        sendCommand(getMouseCommand((byte) 0x01));
    }

    //鼠标左键双击
    private void doubleClick(){
        sendCommand(getMouseCommand((byte) 0x02));
    }

    private void showKeyBoard() {
        editText.setText("");
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }


    @Override
    protected void onResume() {
        super.onResume();
        editText.setText("");
    }

    //获取光标命令
    private byte[] getCursorCommand(float detaX,float detaY){

        byte[] bytes = new byte[8];
        bytes[0] = 0x08;
        bytes[1] = (byte) 0xAB;
        bytes[2] = (byte) 0x02;

        byte[] tmp = cacluDeta(detaX);
        bytes[3] = tmp[0];
        bytes[4] = tmp[1];

        tmp = cacluDeta(detaY);
        bytes[5] = tmp[0];
        bytes[6] = tmp[1];

        updateCheckByte(bytes);
        return bytes;
    }

    //将位移值转换成2个字节的数组
    private byte[] cacluDeta(float detaX) {
        //正负标志
        int flag;
        if(detaX >= 0){
            flag = 0;
        }else{
            flag = 1;
        }
        detaX = Math.abs(detaX);
        int dx = Math.round(detaX);
        byte[] deta = new byte[2];
        deta[1] = (byte) dx;
        if(flag == 0){
            deta[0] = (byte) (dx >> 8);
        }else{
            //设置符合位
            deta[0] = (byte) ((dx | 0x8000) >> 8);
        }
        return deta;
    }

    //获取功能键命令
    private byte[] getFuncCommand(byte b){
        byte[] bytes = new byte[8];
        bytes[0] = 0x08;
        bytes[1] = (byte) 0xAB;
        bytes[2] = (byte) 0x01;
        bytes[3] = (byte) 0x01;

        bytes[4] = b;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;

        updateCheckByte(bytes);

        return bytes;
    }

    //获取键盘直接输入的字码
    private byte[] getKeyboardCommand(byte[] input){
        byte[] bytes = new byte[8];
        bytes[0] = 0x08;
        bytes[1] = (byte) 0xAB;
        bytes[2] = (byte) 0x01;
        bytes[3] = (byte) 0x00;

        bytes[4] = input[0];
        bytes[5] = input[1];
        bytes[6] = (byte) 0x00;

        updateCheckByte(bytes);

        return bytes;
    }

    //获取鼠标命令
    private byte[] getMouseCommand(byte b){
        byte[] bytes = new byte[8];
        bytes[0] = 0x08;
        bytes[1] = (byte) 0xAB;
        bytes[2] = (byte) 0x00;
        bytes[3] = b;

        bytes[4] = (byte) 0x00;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;

        updateCheckByte(bytes);

        return bytes;
    }

    //添加校验和
    private void updateCheckByte(byte[] bytes) {
        //计算校验和
        int sum = 0;
        for(int i=0;i<(bytes.length - 1);i++){
            sum += bytes[i];
        }
        byte check = (byte) (256 - sum%256);
        bytes[bytes.length-1] = check;
    }

    public byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }
}
