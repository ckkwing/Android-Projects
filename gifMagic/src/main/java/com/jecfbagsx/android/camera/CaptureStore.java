package com.jecfbagsx.android.camera;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class CaptureStore extends Thread {
	private static final String TAG = CaptureStore.class.getSimpleName();
	private static final int PREQUEST_MESSAGE = 1231;
	
	public interface Notification {
		public void reportFinish(CaptureJob job);
	};
	
	private Notification mNotification;
	
	public CaptureStore(Notification notification) {
		mNotification = notification;
	}

	private Handler mHandler;

	public void run() {
		Log.i(TAG, "thread started");

		Looper.prepare();

		mHandler = new Handler(new Handler.Callback() {
			public boolean handleMessage(Message msg) {
				if (msg.what != PREQUEST_MESSAGE || msg.obj == null)
					return false;

				handleStore(msg.obj);

				return true;
			}
		});

		Looper.loop();

		Log.i(TAG, "thread stopped");
	}

	public void startup() {
		Log.i(TAG, "start thread");
		
		start();
	}

	public void shutdown() {
		Log.i(TAG, "stop thread");

		if (mHandler == null)
			return;

		mHandler.removeMessages(PREQUEST_MESSAGE);
		
		mHandler.getLooper().quit();
	}

	public void request(CaptureData data, CaptureJob job) {
		requestStore(data, job);
	}
	
	private final class Store {
		CaptureData data;
		CaptureJob job;
	};
	
	private void requestStore(CaptureData data, CaptureJob job) {
		Store store = new Store();
		store.data = data;
		store.job = job;

		Message msg = Message.obtain();
		msg.what = PREQUEST_MESSAGE;
		msg.obj = store;

		if (mHandler != null) {
			mHandler.sendMessage(msg);
			Log.i(TAG, "store request sent");
		}		
	}
	
	private void handleStore(Object obj) {
		Store store = (Store)obj;
		store(store.job, store.data);
		Log.i(TAG, "store request handled");

		if (mNotification != null)
			mNotification.reportFinish(store.job);
	}
	
	private static void store(CaptureJob job, CaptureData data) {
		OutputStream os = null;

		boolean result = false;

		try {
			os = new BufferedOutputStream(new FileOutputStream(job.filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (os != null) {
			result = StoreHelper.store(
					data.data, data.format,
					data.width, data.height, 
					data.matrix, job.quality,
					os);

			if (result) {
				try {
					os.flush();
					os.close();
					result = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		job.result = result;
	}
}
