package com.echen.wisereminder.Model;

import android.content.Context;
import android.util.Log;

/**
 * Created by echen on 2015/9/22.
 */
public class Task {

    protected static final String TAG = "Task";
    protected Context m_context;

    public Task(Context context)
    {
        m_context = context;
    }

    public void doWork() throws InterruptedException {
        Thread.sleep(1000);
        Log.d(TAG, "##########################################################################doWork ");
    }
}
