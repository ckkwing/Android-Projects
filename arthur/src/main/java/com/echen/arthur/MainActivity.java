package com.echen.arthur;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.echen.androidcommon.Media.Image;
import com.echen.androidcommon.Media.MediaCenter;
import com.echen.androidcommon.Utility.PathUtility;
import com.echen.arthur.Data.DataManager;
import com.echen.arthur.Model.TransferInfo;
import com.echen.arthur.Utility.StringConstant;
import com.echen.service.EncryptionTransmissionService;
import com.echen.service.MyReceiver;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {
    private EncryptionTransmissionService serviceRef;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceRef = ((EncryptionTransmissionService.EncryptionTransmissionBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceRef = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switchLanguage();
        setContentView(R.layout.activity_main);
//        Button btnAlarm = (Button)findViewById(R.id.btnAlarm);
//        registerForContextMenu(btnAlarm);
        setActionBarStyle();
        initialize();
    }

    @Override
    protected void onDestroy() {
        DataManager.getInstance().uninit();
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        setMenuItems(menu);
        setSubMenuItems(menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case (android.R.id.home) :
//                Intent intent = new Intent(this, ActionBarActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                Toast.makeText(this.getBaseContext(), "Home button pressed!", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("ContextMenuItem1");
    }

    private void setActionBarStyle()
    {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Title");
        actionBar.setSubtitle("Sub title");
        actionBar.setIcon(R.drawable.ic_action_cloud);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Resources r = getResources();
        Drawable myDrawable = r.getDrawable(R.drawable.actionbar_background);
        actionBar.setBackgroundDrawable(myDrawable);
//        actionBar.hide();
    }

    private void setMenuItems(Menu menu)
    {
        // Group ID
        int groupId = 0;
// Unique Menu Item identifier. Used for event handling
        int menuItem1Id = Menu.FIRST;
// The order position of the item
        int menuItemOrder = Menu.NONE;
// Text to be displayed for this Menu Item
        int menuItem1Text = R.string.menu_item1;
// Create the Menu Item and keep a reference to it
        MenuItem menuItem1 = menu.add(groupId, menuItem1Id,
                menuItemOrder, menuItem1Text);
        MenuItemCompat.setShowAsAction(menuItem1,MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        int menuItem2Id = Menu.FIRST + 1;
        int menuItem2Text = R.string.menu_item2;
        MenuItem menuItem2 = menu.add(groupId, menuItem2Id,
                menuItemOrder, menuItem2Text);
        MenuItemCompat.setShowAsAction(menuItem2,MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        int menuItem3Id = Menu.FIRST + 1;
        int menuItem3Text = R.string.menu_item3;
        MenuItem menuItem3 = menu.add(groupId, menuItem3Id,
                menuItemOrder, menuItem3Text);
//        menuItem3.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        MenuItemCompat.setShowAsAction(menuItem2,MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        CheckBox checkBox = new CheckBox(getBaseContext());
        menu.add(groupId,menuItem1Id , Menu.NONE, "CheckBox").setChecked(true);
    }

    private void setSubMenuItems(Menu menu)
    {
        SubMenu subMenu = menu.addSubMenu(0,0,Menu.NONE, "Sub Menu");
        subMenu.setHeaderIcon(R.drawable.ic_action_cloud);
        subMenu.setIcon(R.drawable.ic_action_cloud);
        MenuItem submenuItem = subMenu.add(0, 0, Menu.NONE, "Submenu Item");
    }
    private void switchLanguage()
    {
        //Test for multiple language
        Configuration config = getResources().getConfiguration();// 获得设置对象
        Resources resources = getResources();// 获得res资源对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        //Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        config.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
        resources.updateConfiguration(config, dm);
    }

    private void initialize()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(StringConstant.PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        File tempEncryptedDir = PathUtility.createDir(this.getApplicationContext(), "Arthur/temp_encrypted");
        editor.putString(StringConstant.PREFERENCES_KEY_TEMPENCRYPTEDPATH, tempEncryptedDir.getAbsolutePath());

        File tempDecryptedDir = PathUtility.createDir(this.getApplicationContext(), "Arthur/temp_decrypted");
        editor.putString(StringConstant.PREFERENCES_KEY_TEMPDECRYPTEDPATH, tempDecryptedDir.getAbsolutePath());

        File targetDir = PathUtility.createDir(this.getApplicationContext(), "Arthur/target");
        editor.putString(StringConstant.PREFERENCES_KEY_TARGETPATH, targetDir.getAbsolutePath());
        editor.apply();

        DataManager.getInstance().init(getApplicationContext());
//        LoadDataAsyncTask loadDataAsyncTask = new LoadDataAsyncTask(proProgressDialog);
//        loadDataAsyncTask.execute();


    }

    public void onImagesClick(View view)
    {
        Intent intent = new Intent(MainActivity.this, FoldersActivity.class);
        intent.putExtra(StringConstant.CATEGORY_IMAGE, MediaCenter.MediaType.Image.toString());
        startActivity(intent);

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                int j =0;
//                while (true)
//                {
//                    Log.d("Test", "count: " + String.valueOf(j));
//                    try {
//                        Thread.sleep(500);
//                        j++;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
    }

    public void onVideosClick(View view)
    {
        Intent intent = new Intent(MainActivity.this, FoldersActivity.class);
        intent.putExtra(StringConstant.CATEGORY_IMAGE, MediaCenter.MediaType.Video.toString());
        startActivity(intent);
    }

    public void onAudiosClick(View view)
    {
        Intent intent = new Intent(MainActivity.this, FoldersActivity.class);
        intent.putExtra(StringConstant.CATEGORY_IMAGE, MediaCenter.MediaType.Audio.toString());
        startActivity(intent);
    }

    public void onStartService(View view)
    {
        Intent intent = new Intent(this, EncryptionTransmissionService.class);
        List<TransferInfo> list = new ArrayList<>();
        List<Image> images = DataManager.getInstance().getImages();
        //For encrypt
        for (Image image : images)
        {
            list.add(new TransferInfo(new File(image.getPath())));
        }

        //For decrypt
//        list.add(new TransferInfo(new File("/mnt/sdcard/Arthur/temp_encrypted/A Star (15).jpg")));
//        list.add(new TransferInfo(new File("/mnt/sdcard/Arthur/temp_encrypted/Beautiful_Pic_4.jpg")));
        Bundle bundleObject = new Bundle();
        bundleObject.putSerializable(StringConstant.IMAGES, (ArrayList<TransferInfo>)list);
        intent.putExtras(bundleObject);
//        intent.pu
        startService(intent);
//        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void onStopService(View view)
    {
        Intent intent = new Intent(this, EncryptionTransmissionService.class);
        stopService(intent);
    }

    public void onAlarmClick(View view)
    {
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        long timeOrLengthofWait = 1000;
        String ALARM_ACTION = MyReceiver.RECEIVER_ACTION;
        Intent intent = new Intent(ALARM_ACTION);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this,0,intent,0);

        alarmManager.set(alarmType, timeOrLengthofWait, alarmIntent);
    }

    public void onShareClick(View view)
    {
        String imagePath = "/mnt/sdcard-ext/Images/A Star (15).jpg";
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (null == imagePath || imagePath.equals(""))
        {
            intent.setType("text/plain");
        }
        else
        {
            File file = new File(imagePath);
            if (null != file && file.exists() && file.isFile())
            {
                intent.setType("image/jpeg");
                Uri uri = Uri.fromFile(file);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, "A Star (15)");
        intent.putExtra(Intent.EXTRA_TEXT, "This is a star");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));
    }

    public void onNotificationClick(View view)
    {
        int NOTIFICATION_REF = 1;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

//        Notification notification = new Notification();
//        notification.defaults = Notification.DEFAULT_SOUND |
//                Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
////        notification.defaults = Notification.DEFAULT_ALL; // Same as above
//
////        notification.icon = android.R.drawable.ic_dialog_alert;
//        notification.tickerText = "Notification";
//        notification.when = System.currentTimeMillis();
//
//        //Sound
//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        notification.sound = uri;
//
//        //Vibrate
//        long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
//        notification.vibrate = vibrate;
//
//        //Light
//        notification.ledARGB = Color.RED;
//        notification.ledOffMS = 0;
//        notification.ledOnMS = 1;
//        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
//
//        RemoteViews rv = new RemoteViews(getPackageName(),
//                R.layout.my_notification);
//        rv.setTextViewText(R.id.text_content, "This is my notification!");
//        notification.contentView = rv;
//

//        notificationManager.notify(NOTIFICATION_REF, notification);


//        Notification.Builder builder = new Notification.Builder(MainActivity.this);
//        builder.setSmallIcon(R.drawable.warning)
//                .setTicker("Notification")
//                .setWhen(System.currentTimeMillis())
//            .setContentTitle("Progress")
//            .setProgress(100, 50, false)
//            .setContentIntent(pendingIntent);
//        Notification notification = builder.getNotification();


        // 下面需兼容Android 2.x版本是的处理方式
        // Notification notify1 = new Notification(R.drawable.message,
        // "TickerText:" + "您有新短消息，请注意查收！", System.currentTimeMillis());
        Notification notification = new Notification();
        notification.icon = R.drawable.warning;
        notification.tickerText = "TickerText:您有新短消息，请注意查收！";
        notification.when = System.currentTimeMillis();
//        notification.setLatestEventInfo(this, "Notification Title",
//                "This is the notification message", pendingIntent);
        RemoteViews rv = new RemoteViews(getPackageName(),
                R.layout.my_notification);
        rv.setTextViewText(R.id.text_content, "This is my notification!");
        notification.contentView = rv;
        notification.contentIntent = pendingIntent;
        notification.number = 1;
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
        notification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。


        // 通过通知管理器来发起通知。如果id不同，则每click，在statu那里增加一个提示
        manager.notify(NOTIFICATION_REF, notification);
    }
}
