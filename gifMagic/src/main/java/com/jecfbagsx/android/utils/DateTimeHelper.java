package com.jecfbagsx.android.utils;

public class DateTimeHelper {

	public static String formatMiliSecondToTimeStr(int lms) {
		Integer hour = 0;
		Integer minute = 0;
		Integer second = 0;
		Integer mili = 0;
		mili = lms % 1000;
		second = lms / 1000;

		if (second > 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute > 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		return (hour.toString() + ":" + minute.toString() + ":"
				+ second.toString() + "." + mili.toString());
	}

}
