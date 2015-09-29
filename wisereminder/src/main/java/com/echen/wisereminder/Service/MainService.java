package com.echen.wisereminder.Service;


import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.echen.androidcommon.DateTime;
import com.echen.androidcommon.Threading.ManualResetEvent;
import com.echen.wisereminder.ConsistentString;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.Model.ReminderTask;
import com.echen.wisereminder.Model.Task;
import com.echen.wisereminder.Receiver.ScreenBroadcastReceiver;
import com.echen.wisereminder.Receiver.TimeTickBroadcastReceiver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by echen on 2015/9/22.
 */
public class MainService extends Service {

    private static final String TAG = "MainService";
//    private static final int NOTIFICATION_ID = 1;
    private Thread m_thread = null;
    private boolean m_isWorking = true;
    private Lock m_lock = new ReentrantLock(true);
    private ManualResetEvent m_manualResetEvent = new ManualResetEvent(false);
    private List<Task> m_taskList = new ArrayList<>();
//    private NotificationManager m_NotificationManager;
//    private NotificationCompat.Builder m_builder;

    public class ServiceBinder extends Binder {
        public MainService getService()
        {
            return MainService.this;
        }
    }

    private IBinder m_binder = new ServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        registerSystemReceiver();
//        m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        m_builder = new NotificationCompat.Builder(this);
        startBackgroundTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        for(int i =0; i<1; i++)
//        {
//            m_taskList.add(new Task());
//        }
        //Temp task adding logic
        for (Reminder reminder : DataManager.getInstance().getReminders(false))
        {
            if (reminder.getIsCompleted())
                continue;
            long currentUTCTimeLong = DateTime.getNowUTCTimeLong();
            if (reminder.getDueTime_UTC() <= currentUTCTimeLong)
            {
                ReminderTask reminderTask = new ReminderTask(this);
                m_taskList.add(reminderTask);
            }
        }
        m_manualResetEvent.set();
        flags = START_STICKY;
        String strFormat = "onStartCommand flags: %d, startId: %d";
        Log.d(TAG, String.format(strFormat, flags, startId));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(ConsistentString.ACTION_BROADCAST_MAINSERVICE);
        sendBroadcast(intent);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return m_binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void registerSystemReceiver()
    {
        IntentFilter filterScreen = new IntentFilter();
        filterScreen.addAction(Intent.ACTION_SCREEN_ON);
        filterScreen.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new ScreenBroadcastReceiver(), filterScreen);

        IntentFilter filterTimeTick=new IntentFilter();
        filterTimeTick.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(new TimeTickBroadcastReceiver(), filterTimeTick);
    }

//    private void sendNotification()
//    {
//        m_builder.setContentTitle("测试标题")//设置通知栏标题
//                .setContentText("测试内容") //设置通知栏显示内容
//                .setContentIntent(getDefaultIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
//            //  .setNumber(number) //设置通知集合的数量
//                .setTicker("测试通知来啦") //通知首次出现在通知栏，带上升动画效果的
//                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
//                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//            //  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
//                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//           //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
//                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
//        m_NotificationManager.notify(NOTIFICATION_ID, m_builder.build());
//    }

//    private PendingIntent getDefaultIntent(int flags){
//        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
//        return pendingIntent;
//    }

    private void startBackgroundTask()
    {
        m_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Thread run start");
                while (m_isWorking)
                {
                    while (true)
                    {
                        m_lock.lock();
                        try
                        {
                            if (m_taskList.size() > 0 || !m_isWorking)
                                break;
                        }
                        catch (Exception e) {}
                        finally {
                            m_lock.unlock();
                        }

                        try {
                            m_manualResetEvent.waitOne();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(m_isWorking)
                            m_manualResetEvent.reset();
                    }

                    if(m_isWorking)
                    {
                        Iterator<Task> iterator = m_taskList.iterator();
                        if (null != iterator)
                        {
                            if (iterator.hasNext())
                            {
                                Task task = iterator.next();
                                try {
                                    task.doWork();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                iterator.remove();
                            }
                        }
                    }
                }


                Log.d(TAG, "Thread run end");
            }
        }, "EncryptionTransmissionServiceThread");
        m_thread.start();
    }
}
