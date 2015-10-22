package com.echen.wisereminder.Receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by echen on 2015/10/22.
 */
public class HomeKeyEventBroadCastReceiver extends BootReceiver {
    private static final String TAG = "HomeKeyEventReceiver";
    private static final String SYSTEM_REASON = "reason";
    private static final String SYSTEM_HOME_KEY = "homekey";//home key
    private static final String SYSTEM_RECENT_APPS = "recentapps";//long home key

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_REASON);
            if (reason != null) {
                if (reason.equals(SYSTEM_HOME_KEY)) {
                    // home key处理点

                } else if (reason.equals(SYSTEM_RECENT_APPS)) {
                    // long home key处理点
                }
            }
        }
    }
}
