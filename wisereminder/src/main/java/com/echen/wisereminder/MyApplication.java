package com.echen.wisereminder;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.echen.androidcommon.CrashHandler;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Profile.ProfileManager;
import com.echen.wisereminder.Service.MainService;
import com.echen.wisereminder.Utility.SettingUtility;

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
        Context appContext = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(this, MainService.class);
        appContext.startService(intent);
        SettingUtility.getInstance().initiate(appContext);
        DataManager.getInstance().initiate(appContext);
        ProfileManager.getInstance().initiate(appContext);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SettingUtility.getInstance().uninit();
        DataManager.getInstance().uninit();
        ProfileManager.getInstance().uninit();
    }
}
