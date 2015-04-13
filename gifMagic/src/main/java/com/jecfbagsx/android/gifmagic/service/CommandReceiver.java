package com.jecfbagsx.android.gifmagic.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CommandReceiver extends BroadcastReceiver {

	public static String ACTION_COMMANDRECEIVER = "android.intent.action.COMMANDRECEIVER";
	public static String MESSAGE_CREATEGIFPATH_STRING = "MESSAGE_CREATEGIFPATH";
	public static String MESSAGE_CREATEGIFPROCESSRATE_DOUBLE = "MESSAGE_CREATEGIFPROCESSRATE";
	public static String MESSAGE_FINISHPROCESS_BOOL = "MESSAGE_FINISHPROCESS_BOOL";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		double index = intent.getDoubleExtra(
				MESSAGE_CREATEGIFPROCESSRATE_DOUBLE, 0);// 获取Extra信息
		Log.i("test receiver", String.valueOf(index));
	}

}
