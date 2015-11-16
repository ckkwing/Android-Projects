package com.echen.wisereminder.Receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.echen.wisereminder.ConsistentString;
import com.echen.wisereminder.Service.MainService;

import java.util.Calendar;

/**
 * Created by echen on 2015/9/23.
 */
public class TimeTickBroadcastReceiver extends BootReceiver {

    private static final String TAG = "TimeTickBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        startUploadService(context);
        String str = this.getClass().getSimpleName();
        str = "";
    }

    @Override
    protected void startUploadService(Context context) {
        Intent serviceIntent = new Intent(context, MainService.class);
        serviceIntent.putExtra(ConsistentString.SERVICE_KEY_CALLER, this.getClass().getSimpleName());
        context.startService(serviceIntent);
    }
}
