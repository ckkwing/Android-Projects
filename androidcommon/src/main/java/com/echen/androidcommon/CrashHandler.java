package com.echen.androidcommon;

import android.app.Application;
import android.util.Log;

/**
 * Created by echen on 2015/2/9.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private volatile static CrashHandler instance;
    private Application app;

    private CrashHandler(){}

    public static CrashHandler getInstance()
    {
        if (null == instance)
        {
            synchronized (CrashHandler.class)
            {
                if (null == instance)
                {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    public void init(Application app) {
        Log.i("CrashHandler", "init()");
        this.app = app;
        // 设置该类为线程默认UncatchException的处理器。
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * The thread is being terminated by an uncaught exception. Further
     * exceptions thrown in this method are prevent the remainder of the
     * method from executing, but are otherwise ignored.
     *
     * @param thread the thread that has an uncaught exception
     * @param ex     the exception that was thrown
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        System.out.println("This is:" + thread.getName() + ",Message:" + ex.getMessage());
        ex.printStackTrace();
    }
}
