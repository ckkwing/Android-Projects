package com.jecfbagsx.android.gifmagic.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.jecfbagsx.android.data.IImageConverter;
import com.jecfbagsx.android.data.ImageSolutionFactory;
import com.jecfbagsx.android.data.ResolutionInfo;
import com.jecfbagsx.android.gifmanage.GifEncoder;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.SettingsHelper;

public class GifService extends Service implements IGifOperation {

	private static final String TAG = "GifCreationService";
	private CustomerBinder m_cuCustomerBinder = new CustomerBinder();

	public GifService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return m_cuCustomerBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		// m_receiver = new CommandReceiver();
		// IntentFilter filter = new IntentFilter();//创建IntentFilter对象
		// filter.addAction(m_receiver.ACTION_COMMANDRECEIVER);
		// registerReceiver(m_receiver, filter);//注册Broadcast Receiver
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// this.unregisterReceiver(m_receiver);//取消注册的CommandReceiver
		super.onDestroy();
	}

	public class CustomerBinder extends Binder implements IGifOperation {
		GifService getService() {
			return GifService.this;
		}

		@Override
		public void create(Activity activity, String path, int duration,
				Map<String, Float> fileMap) {
			// TODO Auto-generated method stub
			final Activity activityTemp = activity;
			final String pathTemp = path;
			final int durationTemp = duration;
			final Map<String, Float> fileMapTemp = fileMap;
//			final String[] fileStringsTemp = fileStrings;

			Thread createThread = new Thread() {
				public void run() {

					Log.i("CustomerBinder",
							"Creation thread, id: "
									+ String.valueOf(this.getId()));
					try {
						GifService.this.create(activityTemp, pathTemp,
								durationTemp, fileMapTemp);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Intent intent = new Intent();// 创建Intent对象
					intent.setAction(CommandReceiver.ACTION_COMMANDRECEIVER);
					intent.putExtra(
							CommandReceiver.MESSAGE_CREATEGIFPATH_STRING,
							pathTemp);
					intent.putExtra(
							CommandReceiver.MESSAGE_CREATEGIFPROCESSRATE_DOUBLE,
							1.0);
					intent.putExtra(CommandReceiver.MESSAGE_FINISHPROCESS_BOOL,
							true);
					sendBroadcast(intent);// 发送广播
					
					Log.i("CustomerBinder", "Creation thread end");
				}

			};
			try {
				createThread.setDaemon(true);
				createThread.start();
			} catch (Exception e) {
				// TODO: handle exception
				Log.i(TAG, "create thread exception: " + e.getMessage());
			}

		}

		@Override
		public void cancel() {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void create(Activity activity, String path, int duration,
			Map<String, Float> fileMap) {
		if (fileMap.isEmpty())
			return;
		// TODO Auto-generated method stub
		GifEncoder encoder = new GifEncoder(activity);
		encoder.start(path);
		encoder.setDelay(duration);
		encoder.setRepeat(0);
		List<String> fileList = new ArrayList<String>();
		for (Iterator<String> iterator = fileMap.keySet().iterator(); iterator.hasNext();) {
			String str = iterator.next();
			fileList.add(str);
		}
		IImageConverter iImageConverter = ImageSolutionFactory.getImageSolution(fileList);
		ResolutionInfo imageInfo = iImageConverter.getTargetResolution();
		encoder.setBitmapSize(imageInfo.getWidth(), imageInfo.getHeight());
		
		Intent intent = new Intent();// 创建Intent对象
		intent.setAction(CommandReceiver.ACTION_COMMANDRECEIVER);
		intent.putExtra(CommandReceiver.MESSAGE_CREATEGIFPATH_STRING, path);
		intent.putExtra(CommandReceiver.MESSAGE_FINISHPROCESS_BOOL, false);
		sendBroadcast(intent);// 发送广播
		int count = fileMap.size();
		int i =0;
		for (Iterator<String> iterator = fileMap.keySet().iterator(); iterator.hasNext();) {
			String str = iterator.next();
			encoder.addFile(str, (float)fileMap.get(str),iImageConverter);

			ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			List<ActivityManager.RunningServiceInfo> list = mActivityManager
					.getRunningServices(100);
			final String className = "android.intent.action.GIFSERVICE";
			boolean isExist = false;
			for (int j = 0; j < list.size(); j++) {
				if (className.equals(list.get(j).service.getClassName())) {
					isExist = true;
					break;
				}
			}

			intent.putExtra(
					CommandReceiver.MESSAGE_CREATEGIFPROCESSRATE_DOUBLE,
					(double) (i + 1) / (double) count);
			sendBroadcast(intent);
			i++;
		}

		encoder.finish();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
