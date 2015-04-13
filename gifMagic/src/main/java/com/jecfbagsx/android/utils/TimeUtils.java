package com.jecfbagsx.android.utils;

public class TimeUtils {
	public static String getFormatTime(long totalMs) {
		long ms = totalMs % 1000;
		int ts = (int) (totalMs/1000);
		int s = 0;
		int m = 0;
		int h = 0;
		if (ts/60 >0)
		{
			int tm = (int) (ts/60);
			if (tm/60 >0)
			{
				h = tm/60;
				m = tm%60;
				s = ts - m*60;
			}else
			{
				m = tm;
				s = ts%60;
			}
		}else
		{
			s = ts;
		}
		
		return String.format("%02d:%02d:%02d", h,m,s);
	}
}
