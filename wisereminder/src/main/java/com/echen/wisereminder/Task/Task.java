package com.echen.wisereminder.Task;

import android.content.Context;
import android.util.Log;

/**
 * Created by echen on 2015/9/22.
 */
public class Task {

    protected static final String TAG = "Task";
    protected Context m_context;
    protected TaskType m_type = TaskType.Blank;

    public TaskType getTaskType() {
        return this.m_type;
    }

    public Task(Context context) {
        m_context = context;
    }

    public void doWork() throws InterruptedException {
        Thread.sleep(1000);
        Log.d(TAG, "##########################################################################doWork ");
    }
}
