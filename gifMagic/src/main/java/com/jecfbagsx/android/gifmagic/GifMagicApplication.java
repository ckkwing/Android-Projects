package com.jecfbagsx.android.gifmagic;

import java.util.Locale;

import android.app.Application;
import android.content.res.Configuration;

public class GifMagicApplication extends Application {
	private Locale locale = null;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (locale != null) {
			newConfig.locale = locale;
			Locale.setDefault(locale);
			getBaseContext().getResources().updateConfiguration(newConfig,
					getBaseContext().getResources().getDisplayMetrics());
		}
	}
}
