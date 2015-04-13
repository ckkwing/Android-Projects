package com.jecfbagsx.android.gifmagic.service;

import java.util.Map;

import android.app.Activity;

public interface IGifOperation {
	void create(Activity activity, String path, int duration,
			Map<String, Float> fileMap);

	void cancel();
}
