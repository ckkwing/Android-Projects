package com.jecfbagsx.android.gifmagic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.utils.SettingsHelper;
import com.jecfbagsx.android.utils.TitlebarHelper;

public class HelpActivity extends Activity {
	private static final String TAG = "AboutActivity";
	private CheckBox mHideTag;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		TitlebarHelper.setbackground(this);
		setTitle(R.string.help);
		Log.i(TAG, "About activity create...");
		mHideTag = (CheckBox) findViewById(R.id.setting_hide_tag);
		mHideTag.setChecked(SettingsHelper.isHideGifMagicTag());
		mHideTag.setOnCheckedChangeListener(checkListener);
	}

	private CheckBox.OnCheckedChangeListener checkListener = new CheckBox.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			SettingsHelper.setHideGifMagicTag(HelpActivity.this, isChecked);
		}

	};
}
