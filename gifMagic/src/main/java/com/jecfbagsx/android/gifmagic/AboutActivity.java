package com.jecfbagsx.android.gifmagic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.utils.TitlebarHelper;

public class AboutActivity extends Activity {
	private static final String TAG = "AboutActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setTitle(R.string.about_us);

		TitlebarHelper.setbackground(this);
		Log.i(TAG, "About activity create...");
	}
}
