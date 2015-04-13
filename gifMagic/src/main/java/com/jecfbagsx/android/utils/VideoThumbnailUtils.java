package com.jecfbagsx.android.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.graphics.Bitmap;
import android.util.Log;

public class VideoThumbnailUtils {
	private Class<?> clzThumbnailUtils;
	private Method mtcreateVideoThumbnail;
	boolean mbInitedSuccess;
	public VideoThumbnailUtils(){
		
	}
	
	public boolean init()
	{
		try {
			clzThumbnailUtils = Class
			.forName("android.media.ThumbnailUtils");
			mtcreateVideoThumbnail = clzThumbnailUtils.getDeclaredMethod(
					"createVideoThumbnail", new Class[] { String.class,int.class });
			mbInitedSuccess = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return mbInitedSuccess;
	}
	
	public Bitmap getVideoThumbnail(String path, int kind)
	{
		if (!mbInitedSuccess)
		{
			return null;
		}
		Bitmap bmp = null;
		Object[] args = new Object[] { path, kind };
		try {
			bmp = (Bitmap) mtcreateVideoThumbnail.invoke(null, args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return bmp;
	}
}
