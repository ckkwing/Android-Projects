package com.echen.wisereminder.Model;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by echen on 2015/9/29.
 */
public class ReminderTask extends Task {
    private int NOTIFICATION_ID = 1;

    public ReminderTask(Context context)
    {
        super(context);
    }

    @Override
    public void doWork() throws InterruptedException {
//    NotificationManager notificationManager = (NotificationManager)m_context.getSystemService(Context.NOTIFICATION_SERVICE);
//    NotificationCompat.Builder builder = new NotificationCompat.Builder(m_context);
//        builder.setContentTitle("测试标题")//设置通知栏标题
//                    .setContentText("测试内容") //设置通知栏显示内容
//                    .setContentIntent(getDefaultIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
//                            //  .setNumber(number) //设置通知集合的数量
//                    .setTicker("测试通知来啦") //通知首次出现在通知栏，带上升动画效果的
//                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
//                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//                            //  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
//                    .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//                            //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
//                    .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
//        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private PendingIntent getDefaultIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(m_context, 1, new Intent(), flags);
        return pendingIntent;
    }
}
