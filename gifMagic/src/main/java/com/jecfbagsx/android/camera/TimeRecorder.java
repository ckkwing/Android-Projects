package com.jecfbagsx.android.camera;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public class TimeRecorder {
	private static final int UPDATE_MESSAGE = 219;
	
	public enum Event {
		START, STOP, RECORD,
	}
	
	public interface Notification {
		public void reportEvent(Event event, String text);
	};
	
	Notification mNotification;
	public TimeRecorder(Notification notification) {
		mNotification = notification;
	}
	
	long mRecordingStartTime;
	boolean mRecordingTime;
	private Handler mUpdateRecordTimeHandler = new Handler(new Handler.Callback() {
		public boolean handleMessage(Message msg) {
			if (msg.what != UPDATE_MESSAGE)
				return false;
			
			update();
			
			return true;
		}
	});

	public void start() {
		if (!mRecordingTime) {
			mRecordingTime = true;
	        mRecordingStartTime = SystemClock.uptimeMillis();
			
			reportEvent(Event.START, null);
			
	        update();
		}
	}
	
	public void stop() {
		if (mRecordingTime) {
			mRecordingTime = false;
			
			reportEvent(Event.STOP, null);
		}
	}
	
	private void update() {
		if (!mRecordingTime)
			return;
		
		long now = SystemClock.uptimeMillis();
		long delta = now - mRecordingStartTime;

		long next_update_delay = 1000 - (delta % 1000);
		long seconds = delta / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long remainderMinutes = minutes - (hours * 60);
		long remainderSeconds = seconds - (minutes * 60);

		String secondsString = Long.toString(remainderSeconds);
		if (secondsString.length() < 2) {
			secondsString = "0" + secondsString;
		}
		
		String minutesString = Long.toString(remainderMinutes);
		if (minutesString.length() < 2) {
			minutesString = "0" + minutesString;
		}
		
		String text = minutesString + ":" + secondsString;
		if (hours > 0) {
			String hoursString = Long.toString(hours);
			
			if (hoursString.length() < 2) {
				hoursString = "0" + hoursString;
			}
			
			text = hoursString + ":" + text;
		}
		
		reportEvent(Event.RECORD, text);
		
		mUpdateRecordTimeHandler.sendEmptyMessageDelayed(UPDATE_MESSAGE, next_update_delay);
	}
	
	private void reportEvent(Event event, String text) {
		if (mNotification != null)
			mNotification.reportEvent(event, text);
	}
}
