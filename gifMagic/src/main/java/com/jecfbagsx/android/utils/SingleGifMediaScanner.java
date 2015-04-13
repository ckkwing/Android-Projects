package com.jecfbagsx.android.utils;

import java.io.File;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

public class SingleGifMediaScanner implements MediaScannerConnectionClient {
	private static final String TAG = "SingleGifMediaScanner";
	private MediaScannerConnection mMs;
	private String mFile;
	private boolean isScanCompleted;
	private Uri mediaUri;

	public boolean isScanCompleted() {
		return isScanCompleted;
	}

	public Uri getMediaUri() {
		return mediaUri;
	}

	public SingleGifMediaScanner(Context context, String f) {
		mFile = f;
		mMs = new MediaScannerConnection(context, this);
		mMs.connect();
	}

	@Override
	public void onMediaScannerConnected() {
		mMs.scanFile(mFile, null);
		Log.i(TAG, "Scanning File:" + mFile);
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		Log.i(TAG, "onScanCompleted");
		if (path.equals(mFile)) {
			mediaUri = uri;
			Log.i(TAG, "uri:" + uri.toString());
		}
		isScanCompleted = true;
		mMs.disconnect();
	}
}
