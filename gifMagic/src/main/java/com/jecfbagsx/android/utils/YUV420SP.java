package com.jecfbagsx.android.utils;

public class YUV420SP {
	public native void decode(int[] rgb, byte[] yuv420sp, int width, int height);

	static {
		System.loadLibrary("YUV420SP");
	}
}