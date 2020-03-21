package demo.com.ledvediocontroller;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.addLogAdapter(new DiskLogAdapter());
        MyUncaughtExceptionHandler.getInstance().init(this);
    }
}
