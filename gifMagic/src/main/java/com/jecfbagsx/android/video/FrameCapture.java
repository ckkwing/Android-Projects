package com.jecfbagsx.android.video;

import com.jecfbagsx.android.data.ResolutionInfo;

import android.graphics.Bitmap;
import android.util.Log;

public class FrameCapture {
	private static final String TAG = "FrameCapture";

	private native int openFile(String filename);

	private native long getNextFrame(int videoIndex, int width, int height,
			int nSkipFrame, Bitmap bitmap);

	private native long seekTo(int videoIndex, int width, int height,
			long timeMs, Bitmap bitmap);

	private native long getFileDuration(int videoIndex);

	private native int getFileResolutionWidth(int videoIndex);

	private native int getFileResolutionHeight(int videoIndex);

	public native void release();

	private native void native_setup();

	private static native void native_init();

	private native void native_finalize();

	private static native long scaleImage(String srcFile, Bitmap targetBmp);

	private int videoIndex = -1;
	private long currentPosMs = 0;

	static {
		System.loadLibrary("ffmpegutils");
		native_init();
	}

	public FrameCapture() {
		native_setup();
	}

	// The field below is accessed by native methods
	@SuppressWarnings("unused")
	private int mNativeContext;

	public long getCurrentPos() {
		return currentPosMs;
	}

	public boolean setDataSource(String file) {
		Log.i(TAG, "Open file " + file);
		try {
			videoIndex = openFile(file);
		} catch (Exception ex) {
			Log.i(TAG, "Exception:" + ex.getMessage());
		}

		return (videoIndex >= 0);
	}

	public Bitmap seekTo(int width, int height, long timeMs) {
		Log.i(TAG, "get Frame At Time(ms):" + timeMs);
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		boolean bSuccess = false;
		try {
			long ret = seekTo(videoIndex, width, height, timeMs, bitmap);
			bSuccess = (ret >= 0);
			if (bSuccess) {
				currentPosMs = ret;
				Log.i(TAG, "Current Position(ms):" + currentPosMs);
			}
		} catch (Exception ex) {
			Log.i(TAG, "Exception:" + ex.getMessage());
		}

		return bSuccess ? bitmap : null;
	}

	public Bitmap getNextFrame(int width, int height, int nSkipFrame) {
		Log.i(TAG, "get Next Frame");
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		boolean bSuccess = false;
		try {
			long ret = getNextFrame(videoIndex, width, height, nSkipFrame,
					bitmap);
			bSuccess = (ret >= 0);
			if (bSuccess) {
				currentPosMs = ret;
				Log.i(TAG, "Current Position(ms):" + currentPosMs);
			}
		} catch (Exception ex) {
			Log.i(TAG, "Exception:" + ex.getMessage());
		}
		return bSuccess ? bitmap : null;
	}

	public long getDuration() {
		long dur = 0;
		Log.i(TAG, "get Duration");
		try {
			dur = getFileDuration(videoIndex);
		} catch (Exception ex) {
			Log.i(TAG, "Exception:" + ex.getMessage());
		}
		Log.i(TAG, "Duration(ms):" + dur);
		return dur;
	}

	public ResolutionInfo getResolution() {
		ResolutionInfo res = new ResolutionInfo();
		res.setWidth(getFileResolutionWidth(videoIndex));
		res.setHeight(getFileResolutionHeight(videoIndex));
		Log.i(TAG, "Resolution:" + res);
		return res;
	}

	public static Bitmap scaleBitmap(String src, int targetWidth,
			int targetHeight) {
		Log.i(TAG, "scale Bitmap");
		Bitmap bitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Bitmap.Config.ARGB_8888);
		try {
			scaleImage(src, bitmap);
		} catch (Exception ex) {
			Log.i(TAG, "Exception:" + ex.getMessage());
		}
		return bitmap.copy(Bitmap.Config.RGB_565, true);
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			native_finalize();
		} finally {
			super.finalize();
		}
	}
}
