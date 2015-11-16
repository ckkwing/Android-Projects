package com.echen.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.echen.androidcommon.Crypto.AESUtility;
import com.echen.androidcommon.FileHelper;
import com.echen.androidcommon.Threading.ManualResetEvent;
import com.echen.arthur.Model.TransferInfo;
import com.echen.arthur.Utility.StringConstant;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by echen on 2015/3/11.
 */
public class EncryptionTransmissionService extends Service {
    private final String TAG = "TransferService";
    private final IBinder binder = new EncryptionTransmissionBinder();
    private Thread thread = null;
    private List<TransferInfo> filesToTransfer = new ArrayList<>();
    private boolean isWorking = true;
    private Lock lock = new ReentrantLock(true);
    private ManualResetEvent manualResetEvent = new ManualResetEvent(false);
    private String PATH_TEMP_ENCRYPTED = null;
    private String PATH_TEMP_DECRYPTED = null;
    private String PATH_TARGETDIR = null;
    private Notification notification;


    public class EncryptionTransmissionBinder extends Binder
    {
        public EncryptionTransmissionService getService()
        {
            return EncryptionTransmissionService.this;
        }
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
        startBackgroundTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getConfiguration();
        Bundle bundleObject = intent.getExtras();
        // Get ArrayList Bundle
        List<TransferInfo> files = (ArrayList<TransferInfo>) bundleObject.getSerializable(StringConstant.IMAGES);

        lock.lock();
        try
        {
            for (TransferInfo transferInfo : files)
            {
                filesToTransfer.add(transferInfo);
            }
        }
        catch (Exception e) {}
        finally {
            lock.unlock();
        }

        manualResetEvent.set();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        isWorking = false;
        if (null != manualResetEvent)
            manualResetEvent.set();
        if (null != thread)
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void getConfiguration()
    {
        PATH_TEMP_ENCRYPTED = null;
        PATH_TEMP_DECRYPTED = null;
        PATH_TARGETDIR = null;

        SharedPreferences sharedPreferences = getSharedPreferences(StringConstant.PREFERENCES_NAME, MODE_PRIVATE);
        PATH_TEMP_ENCRYPTED = sharedPreferences.getString(StringConstant.PREFERENCES_KEY_TEMPENCRYPTEDPATH, null);
        if(null == PATH_TEMP_ENCRYPTED)
            throw new NullPointerException("PATH_TEMP_ENCRYPTED has not initialized");
        PATH_TEMP_DECRYPTED = sharedPreferences.getString(StringConstant.PREFERENCES_KEY_TEMPDECRYPTEDPATH, null);
        if(null == PATH_TEMP_DECRYPTED)
            throw new NullPointerException("PATH_TEMP_DECRYPTED has not initialized");
        PATH_TARGETDIR = sharedPreferences.getString(StringConstant.PREFERENCES_KEY_TARGETPATH, null);
        if(null == PATH_TARGETDIR)
            throw new NullPointerException("PATH_TARGETDIR has not initialized");
    }

    private void createNotification()
    {
        int icon = android.R.drawable.ic_dialog_alert;
        String tickerText = "Notification";
        long when = System.currentTimeMillis();
        notification = new Notification();
        notification.defaults = Notification.DEFAULT_SOUND |
                Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
//        notification.defaults = Notification.DEFAULT_ALL; // Same as above

        //Sound
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.sound = uri;

        //Vibrate
        long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
        notification.vibrate = vibrate;

        //Light
        notification.ledARGB = Color.RED;
        notification.ledOffMS = 0;
        notification.ledOnMS = 1;
        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
    }

    private void startBackgroundTask()
    {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Thread run start");
                while (isWorking)
                {
                    while (true)
                    {
                        lock.lock();
                        try
                        {
                            if (filesToTransfer.size() > 0 || !isWorking)
                                break;
                        }
                        catch (Exception e) {}
                        finally {
                            lock.unlock();
                        }

                        try {
                            manualResetEvent.waitOne();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(isWorking)
                            manualResetEvent.reset();
                    }

                    if(isWorking)
                    {
                        Iterator<TransferInfo> iterator = filesToTransfer.iterator();
                        if (null != iterator)
                        {
                            if (iterator.hasNext())
                            {
                                TransferInfo transferInfo = iterator.next();
                                File encryptedFile = encrypt(transferInfo.getSourceFile());
//                                decrypt(transferInfo.getSourceFile());
                                if (null != encryptedFile) {
                                    transfer(encryptedFile, PATH_TARGETDIR, encryptedFile.getName());
                                    FileHelper.deleteFile(encryptedFile);
                                }
                                iterator.remove();
                            }
                        }
                    }
                }


                Clear();
                Log.d(TAG, "Thread run end");
            }
        }, "EncryptionTransmissionServiceThread");
        thread.start();
    }

    private void Clear()
    {
        File temp_encrypted = new File(PATH_TEMP_ENCRYPTED);
        if (null != temp_encrypted && temp_encrypted.exists() && !temp_encrypted.isFile())
            FileHelper.deleteFile(temp_encrypted);
        File temp_decrypted = new File(PATH_TEMP_DECRYPTED);
        if (null != temp_decrypted && temp_decrypted.exists() && !temp_decrypted.isFile())
            FileHelper.deleteFile(temp_decrypted);
//        File target = new File(PATH_TARGETDIR);
//        if (null != target && target.exists() && !target.isFile())
//            FileHelper.deleteFile(target);
    }

    private File encrypt(File file)
    {
        File encryptedFile = null;
        if (null == file || !file.exists() || !file.isFile())
            return encryptedFile;
        encryptedFile = AESUtility.encryptFile(file, PATH_TEMP_ENCRYPTED, file.getName(), StringConstant.CRYPTO_AES_KEY);
        return encryptedFile;
    }

    private File decrypt(File file)
    {
        File decryptedFile = null;
        if (null == file || !file.exists() || !file.isFile())
            return decryptedFile;
        decryptedFile = AESUtility.decryptFile(file, PATH_TEMP_DECRYPTED, file.getName(), StringConstant.CRYPTO_AES_KEY);
        return decryptedFile;
    }

    private boolean transfer(File sourceFile, String targetPath, String fileName)
    {
        if(null == sourceFile || !sourceFile.exists())
            return false;
        if (null == targetPath || targetPath.equals(""))
            return false;
        if (null == fileName || fileName.equals(""))
            return false;

        File toFile = FileHelper.createNewFile(targetPath, fileName);
        if (null == toFile)
            return false;

        FileHelper.copyFile(sourceFile, toFile);
        return true;
    }
}
