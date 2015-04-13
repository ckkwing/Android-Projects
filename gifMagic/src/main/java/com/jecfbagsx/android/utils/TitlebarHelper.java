package com.jecfbagsx.android.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewParent;

import com.jecfbagsx.android.gifmagic.R;

public class TitlebarHelper {
	public static void setbackground(Activity activity) {
		View titleView = activity.getWindow().findViewById(android.R.id.title);
		if (titleView != null) {
			ViewParent parent = titleView.getParent();
			if (parent != null && (parent instanceof View)) {
				View parentView = (View) parent;
				parentView.setBackgroundResource(R.drawable.title_gradient);
			}
		}
	}
}
