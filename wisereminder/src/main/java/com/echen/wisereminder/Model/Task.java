package com.echen.wisereminder.Model;

import android.util.Log;

/**
 * Created by echen on 2015/9/22.
 */
public class Task {

    private static final String TAG = "Task";

    public void doWork() throws InterruptedException {
        Thread.sleep(1000);
        Log.d(TAG, "##########################################################################doWork ");
    }
}
