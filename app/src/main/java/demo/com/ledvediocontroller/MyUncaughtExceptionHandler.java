package demo.com.ledvediocontroller;

import android.content.Context;

import com.orhanobut.logger.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static MyUncaughtExceptionHandler myUncaughtExceptionHandler = new MyUncaughtExceptionHandler();

    private Context context;
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    public static MyUncaughtExceptionHandler getInstance(){
        return myUncaughtExceptionHandler;
    }

    private MyUncaughtExceptionHandler() {

    }

    public void init(Context context){
        this.context = context;
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    //处理异常
    private void handException(Throwable e){
        if(e == null){
            return;
        }
        //获取堆栈信息
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null){
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String ret = printWriter.toString();

        //日志记录
        Logger.e(ret);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //处理完成后继续走系统原来的处理流程
        handException(e);
        mDefaultUncaughtExceptionHandler.uncaughtException(t,e);
    }
}
