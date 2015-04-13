package com.echen.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.echen.arthur.R;

/**
 * Created by echen on 2015/3/16.
 */
public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";
    public static final String RECEIVER_ACTION = "android.intent.action.ARTHUR_BROADCAST";

    @Override
    public void onReceive(Context context, Intent intent) {
//        String msg = intent.getStringExtra("msg");
//        Log.i(TAG, msg);

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(ns);
//定义Notification的各种属性
        int icon = android.R.drawable.ic_menu_search; //通知图标
        CharSequence tickerText = "Hello"; //状态栏显示的通知文本提示
        long when = System.currentTimeMillis(); //通知产生的时间，会在通知信息里显示
//用上面的属性初始化Nofification
        Notification notification = new Notification(icon,tickerText,when);
        Toast.makeText(context, "时间到了！！！！", Toast.LENGTH_LONG).show();
    }
}
