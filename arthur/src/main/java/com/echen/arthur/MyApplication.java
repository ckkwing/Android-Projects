package com.echen.arthur;

import android.app.Application;
import com.echen.androidcommon.CrashHandler;
import com.echen.arthur.Data.DataManager;

/**
 * Created by echen on 2015/1/14.
 */
public class MyApplication extends Application {
    private static MyApplication singleton;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public static MyApplication getInstance(){
        return singleton;
    }

    @Override
    public final void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        singleton = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
