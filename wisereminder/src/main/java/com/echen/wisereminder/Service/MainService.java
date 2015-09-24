package com.echen.wisereminder.Service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.echen.androidcommon.Threading.ManualResetEvent;
import com.echen.wisereminder.ConsistentString;
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
    private Thread m_thread = null;
    private boolean m_isWorking = true;
    private Lock m_lock = new ReentrantLock(true);
    private ManualResetEvent m_manualResetEvent = new ManualResetEvent(false);
    private List<Task> m_taskList = new ArrayList<>();

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
        startBackgroundTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        for(int i =0; i<100; i++)
        {
            m_taskList.add(new Task());
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
