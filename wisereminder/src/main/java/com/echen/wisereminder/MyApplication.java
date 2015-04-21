package com.echen.wisereminder;

import android.app.Application;

import com.echen.androidcommon.CrashHandler;

/**
 * Created by echen on 2015/4/21.
 */
public class MyApplication extends Application {
    private volatile static MyApplication instance;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public static MyApplication getInstance()
    {
        if (null == instance)
        {
            synchronized (MyApplication.class)
            {
                if (null == instance)
                {
                    instance = new MyApplication();
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
