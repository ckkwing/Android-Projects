package com.jecfbagsx.android.gifmagic;

import java.util.Timer;
import java.util.TimerTask;

import com.jecfbagsx.android.gifmagic.R;

import dalvik.system.VMRuntime;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

public class Splash extends Activity {
	private static final int STOPSPLASH = 0;
	private boolean mFinished = false;
	private boolean mTouched = false;
	private final int SPLASH_DISPLAY_LENGHT = 3000;
	private TextView mVersionNum;
	private Timer timer;
	private Handler splashHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STOPSPLASH:
				timer.cancel();
				if (!mFinished) {
					mFinished = true;
					Intent mainIntent = new Intent(Splash.this,
							MainActivity.class);
					Splash.this.startActivity(mainIntent);
					Splash.this.finish();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	TimerTask task = new TimerTask() {
		public void run() {
			Message msg = new Message();
			msg.what = STOPSPLASH;
			splashHandler.sendMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		mVersionNum = (TextView)findViewById(R.id.splash_version);
		
		timer = new Timer(false);
		timer.schedule(task, SPLASH_DISPLAY_LENGHT);
		PackageManager manager = this.getPackageManager();
		String appVersion="";
		try { 
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			appVersion = info.versionName; 
		}
		catch (NameNotFoundException e) {
		}
		mVersionNum.setText("V"+appVersion);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (!mTouched) {
				mTouched = true;
				try {
					timer.cancel();
				} catch (Exception ex) {

				}
				Message msg = new Message();
				msg.what = STOPSPLASH;
				splashHandler.sendMessage(msg);
			}
		}
		return true;

	}

	@Override
	protected void onDestroy() {
		Message msg = new Message();
		msg.what = STOPSPLASH;
		splashHandler.sendMessageDelayed(msg, 0);
		timer.purge();
		super.onDestroy();
	}


}
