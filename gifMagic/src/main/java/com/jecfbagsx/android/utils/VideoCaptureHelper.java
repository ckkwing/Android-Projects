package com.jecfbagsx.android.utils;

import com.jecfbagsx.android.data.CameraSpeed;

public class VideoCaptureHelper {
	
	public static int getCaptureCount()
	{
		return SettingsHelper.getVideoDuration();
	}
	
	public static int getCaptureSkipFrame() {
		CameraSpeed speed = SettingsHelper.getCameraSpeedType();
		int interval = 1;
		switch (speed) {
		case fast:
			interval = 1;
			break;
		case slow:
			interval = 3;
			break;
		case normal:
			interval = 5;
			break;
		}
		return interval;
	}
}
