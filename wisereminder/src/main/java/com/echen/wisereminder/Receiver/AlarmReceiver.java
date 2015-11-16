package com.echen.wisereminder.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.echen.wisereminder.ConsistentString;
import com.echen.wisereminder.Service.MainService;

/**
 * Created by echen on 2015/10/23.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive " + action);
        switch (action) {
            case ConsistentString.ACTION_BROADCAST_PERIODICALARM: {
                Intent serviceIntent = new Intent(context, MainService.class);
                serviceIntent.putExtra(ConsistentString.SERVICE_KEY_CALLER, this.getClass().getSimpleName());
                context.startService(serviceIntent);
            }
            break;
        }
    }
}
