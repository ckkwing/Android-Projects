package com.echen.wisereminder.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.echen.wisereminder.ConsistentString;
import com.echen.wisereminder.Service.MainService;

/**
 * Created by echen on 2015/9/22.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive " + action);
        switch (action) {
            case ConsistentString.ACTION_BROADCAST_MAINSERVICE: {
                startUploadService(context);
            }
            break;
        }
    }

    protected void startUploadService(Context context) {
        Intent serviceIntent = new Intent(context, MainService.class);
        context.startService(serviceIntent);
    }
}
