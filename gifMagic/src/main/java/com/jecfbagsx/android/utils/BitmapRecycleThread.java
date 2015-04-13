package com.jecfbagsx.android.utils;

import android.graphics.Bitmap;
import android.util.Log;

public class BitmapRecycleThread extends Thread {


	private String TAG = "BitmapRecycleThread";
	private Bitmap m_bitmap = null;
	
	public BitmapRecycleThread()
	{
		super();
	}
	
	public BitmapRecycleThread(Bitmap bitmap) {
		this();
		m_bitmap = bitmap;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (m_bitmap != null && !m_bitmap.isRecycled())
			m_bitmap.recycle();
		super.run();
	}
	
	public void waitInfinite()
	{
		if (null == m_bitmap)
			return;
		while (!m_bitmap.isRecycled())
		{
			Log.i(TAG, "BitmapRecycleThread has not finished");
		}
	}

}
