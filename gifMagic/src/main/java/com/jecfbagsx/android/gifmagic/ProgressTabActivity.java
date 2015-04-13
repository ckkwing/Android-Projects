package com.jecfbagsx.android.gifmagic;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.utils.FileHelper;

public class ProgressTabActivity extends ActivityGroup {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tabhost);

		Intent cameraIntent = new Intent(this, CameraView.class);
		cameraIntent.putExtra(HistoryActivity.SOURCEPATH,
				FileHelper.getAppHistoryPath());

		Intent editIntent = new Intent();
		Intent shareIntent = new Intent();

		TabHost tabs = (TabHost) findViewById(R.id.maintabhost);

		tabs.setup(this.getLocalActivityManager());

		Intent intent = new Intent(this, HistoryActivity.class);
		intent.putExtra(HistoryActivity.SOURCEPATH,
				FileHelper.getAppHistoryPath());

		TabHost.TabSpec spec = tabs.newTabSpec("camera");
		spec.setContent(cameraIntent);
		spec.setIndicator(getResources().getString(R.string.maintab_progress));// 如果需要带icon图标，则使用setIndicator(CharSequence
																				// label,
																				// Drawable
																				// icon)函数
		tabs.addTab(spec);

		// spec = tabs.newTabSpec("edit");
		// spec.setContent(editIntent);
		// spec.setIndicator(getResources().getString(R.string.maintab_edit));
		// tabs.addTab(spec);
		//
		// spec = tabs.newTabSpec("share");
		// spec.setContent(shareIntent);
		// spec.setIndicator(getResources().getString(R.string.maintab_share));
		// tabs.addTab(spec);
		// setTitle("Alert");

		tabs.setCurrentTab(0);// 启动时显示第一个标签页
	}

}
