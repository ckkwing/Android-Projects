package com.echen.wisereminder.Receiver;

import android.content.Context;
import android.content.Intent;

/**
 * Created by echen on 2015/9/23.
 */
public class TimeTickBroadcastReceiver extends BootReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        startUploadService(context);
    }
}
