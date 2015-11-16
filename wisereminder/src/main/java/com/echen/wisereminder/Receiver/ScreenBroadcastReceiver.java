package com.echen.wisereminder.Receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by echen on 2015/9/23.
 */
public class ScreenBroadcastReceiver extends BootReceiver {

    private static final String TAG = "ScreenBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive ");
//        startUploadService(context);
    }
}
