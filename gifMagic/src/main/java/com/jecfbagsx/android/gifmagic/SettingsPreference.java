package com.jecfbagsx.android.gifmagic;

import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.SettingsHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

public class SettingsPreference extends PreferenceActivity {
	private int defLanIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		defLanIndex = SettingsHelper.getLanguageIndex();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Context context = getApplicationContext();
			SettingsHelper.refreshSettings(context);
			int changedLanIndex = SettingsHelper.getLanguageIndex();
			Bundle bundle = new Bundle();
			if (defLanIndex != changedLanIndex) {
				bundle.putBoolean(ActivityActions.EXTRA_SETTING_LAN_CHANGED,
						true);
			}
			setResult(RESULT_OK, this.getIntent().putExtras(bundle));
		}
		return super.onKeyDown(keyCode, event);
		
	}
}
