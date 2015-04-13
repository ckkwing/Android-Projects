package com.jecfbagsx.android.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.jecfbagsx.android.data.CameraSpeed;
import com.jecfbagsx.android.data.Duration;
import com.jecfbagsx.android.data.Quality;
import com.jecfbagsx.android.data.ResolutionInfo;

public class SettingsHelper {
	public static final String PREF_LANGUAGE = "PREF_LANGUAGE";
	private static final String PREF_RESOLUTION = "PREF_RESOLUTION";
	private static final String PREF_TOTAL_CAMERA_CAPTURE = "PREF_TOTAL_CAMERA_CAPTURE";
	private static final String PREF_TOTAL_VIDEO_CAPTURE = "PREF_TOTAL_VIDEO_CAPTURE";
	private static final String PREF_CAMERA_SPEED = "PREF_CAMERA_SPEED";
	private static final String PREF_KEEP_TEMP_IMAGE = "PREF_KEEP_TEMP_IMAGE";

	private static ResolutionInfo resolutionInfo = new ResolutionInfo(
			Quality.High);
	// private static Duration duration = Duration.Short;
	public static final String VIDEO_FILE_PATH = "VIDEO_FILE_PATH";
	public static final String IMAGE_FILES_FOLDER = "IMAGE_FILES_FOLDER";
	public static final String GIF_FILE_LOCATION = "GIF_FILE_LOCATION";
	public static final String GIF_FILE_ISCREATED = "GIF_FILE_ISCREATED";
	public static final String WEIBO_MESSAGE = "WEIBO_MESSAGE";
	private static final String ACTIVITY_CODE = "ACTIVITY_CODE";
	private static final String HIDE_GIFMAGIC_TAG = "HIDE_GIFMAGIC_TAG";
	private static final String VIDEO_CAPTURE_ENABLE = "video_capture_enable";
	private static String vipCode = "";
	private static CameraSpeed cameraSpeed = CameraSpeed.normal;
	private static int languageIndex;
	private static boolean hideGifMagicTag = false;
	private static boolean isCheckedVideoCapture = false;
	private static boolean isVideoCaptureEnable = false;
	private static int totalCameraCapture = 20;
	private static int totalVideoCapture = 20;
	private static boolean isKeepTempImage = false;

	public static boolean isKeepTempImage() {
		return isKeepTempImage;
	}

	public static void setKeepTempImage(Context context, boolean isKeepImage) {
		// SettingsHelper.hideGifMagicTag = hideGifMagicTag;
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		prefs.edit()
				.putString(PREF_KEEP_TEMP_IMAGE, Boolean.toString(isKeepImage))
				.commit();
		refreshSettings(context);
	}

	public static boolean isHideGifMagicTag() {
		return hideGifMagicTag;
	}

	public static void setHideGifMagicTag(Context context,
			boolean hideGifMagicTag) {
		// SettingsHelper.hideGifMagicTag = hideGifMagicTag;
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		prefs.edit()
				.putString(HIDE_GIFMAGIC_TAG, Boolean.toString(hideGifMagicTag))
				.commit();
		refreshSettings(context);

	}

	public static int getLanguageIndex() {
		return languageIndex;
	}

	public static int getCameraSpeed() {
		int speed = 3;
		switch (cameraSpeed) {
		case fast:
			speed = 1;
			break;
		case normal:
			speed = 3;
			break;
		case slow:
			speed = 5;
			break;
		}
		return speed;
	}

	public static CameraSpeed getCameraSpeedType() {
		return cameraSpeed;
	}

	public static void setCameraSpeed(Context context, CameraSpeed cameraSpeed) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		prefs.edit().putString(PREF_CAMERA_SPEED, cameraSpeed.toString())
				.commit();
		refreshSettings(context);
	}

	public static String getVipCode() {
		return vipCode;
	}

	public static boolean setVipCode(Context context, String vipCode) {
		if (!virifyActivityCode(vipCode)) {
			return false;
		}
		SettingsHelper.vipCode = vipCode;
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		prefs.edit().putString(ACTIVITY_CODE, vipCode).commit();
		refreshSettings(context);
		return true;
	}

	private static boolean virifyActivityCode(String vipCode) {
		if (vipCode == null || "".equals(vipCode)) {
			return false;
		}
		if ("GifMagic2011#jecfbags".equals(vipCode.trim())) {
			return true;
		}
		return true;
	}

	public static boolean IsActivitied() {
		if (vipCode == null || "".equals(vipCode)) {
			return false;
		}

		return true;
	}

	public static ResolutionInfo getResolutionInfo() {
		return resolutionInfo;
	}

	public static Locale getTargetLan(Context context, String lan) {
		Locale loc = Locale.ENGLISH;
		if (lan == null || "".equals(lan)) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			lan = prefs.getString("PREF_LANGUAGE", "en");
		}

		if ("zh_CN".equalsIgnoreCase(lan)) {
			loc = Locale.SIMPLIFIED_CHINESE;
			languageIndex = 1;
		} else {
			loc = Locale.ENGLISH;
			languageIndex = 0;
		}

		return loc;
	}

	public static int getCameraDuration() {
		return totalCameraCapture;
	}

	public static int getVideoDuration() {
		return totalVideoCapture;
	}

	public static void setTotalCameraCapture(Context context, int num) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		prefs.edit().putInt(PREF_TOTAL_CAMERA_CAPTURE, num).commit();
		refreshSettings(context);
	}

	public static void setTotalVideoCapture(Context context, int num) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		prefs.edit().putInt(PREF_TOTAL_VIDEO_CAPTURE, num).commit();
		refreshSettings(context);
	}

	public static void setResolutionType(Context context, Quality qualityType) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		prefs.edit().putString(PREF_RESOLUTION, qualityType.toString())
				.commit();
		refreshSettings(context);
	}

	public static boolean refreshSettings(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		// update resolution
		String res = prefs.getString(PREF_RESOLUTION, "");
		if (res != null && !"".equals(res)) {
			if (Quality.Low.toString().equals(res)) {
				resolutionInfo = new ResolutionInfo(Quality.Low);
			} else if (Quality.Middle.toString().equals(res)) {
				resolutionInfo = new ResolutionInfo(Quality.Middle);
			} else if (Quality.High.toString().equals(res)) {
				resolutionInfo = new ResolutionInfo(Quality.High);
			} else {
				resolutionInfo = new ResolutionInfo(Quality.Low);
			}
		} else {
			resolutionInfo = new ResolutionInfo(Quality.High);
		}

		getTargetLan(context, null);
		totalCameraCapture = prefs.getInt(PREF_TOTAL_CAMERA_CAPTURE, 20);
		totalVideoCapture = prefs.getInt(PREF_TOTAL_VIDEO_CAPTURE, 100);

		String sp = prefs.getString(PREF_CAMERA_SPEED,
				CameraSpeed.normal.toString());
		cameraSpeed = CameraSpeed.valueOf(sp);

		vipCode = prefs.getString("ACTIVITY_CODE", "");
		String hideTag = prefs.getString(HIDE_GIFMAGIC_TAG, "false");
		hideGifMagicTag = Boolean.valueOf(hideTag);

		String keep = prefs.getString("PREF_KEEP_TEMP_IMAGE", "false");
		isKeepTempImage = Boolean.valueOf(keep);
		return true;
	}

	public static boolean isVideoCaptureEnable(Context context) {
		if (!isCheckedVideoCapture) {
			isCheckedVideoCapture = true;
			try {
				ApplicationInfo appi = context.getPackageManager()
						.getApplicationInfo(context.getPackageName(),
								PackageManager.GET_META_DATA);
				Bundle meta = appi.metaData;
				isVideoCaptureEnable = meta.getBoolean(VIDEO_CAPTURE_ENABLE);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return isVideoCaptureEnable;
	}

	public static void exitApp(Activity activity) {
		Intent startMain = new Intent(Intent.ACTION_MAIN); 
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(startMain); System.exit(0);
	}
}
