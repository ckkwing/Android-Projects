package com.jecfbagsx.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {
	private HashMap<String, SoftReference<Drawable>> m_imageCache;
	
	
	public AsyncImageLoader()
	{
		m_imageCache = new HashMap<String, SoftReference<Drawable>>();
	}
	
	public Drawable loadDrawable(final String imageUrl, final IImageCallback callback)
	{
		if (m_imageCache.containsKey(imageUrl))
		{
			SoftReference<Drawable> softReference = m_imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if ( drawable != null )
				return drawable;
		}
		final Handler handler = new Handler()
		{
			public void handleMessage(Message message)
			{
				callback.imageLoaded((Drawable)message.obj, imageUrl);
			}
		};
		new Thread()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Drawable drawable = loadimageFromUrl(imageUrl);
				m_imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message msg = new Message();
				msg.obj = drawable;
				handler.sendMessage(msg);
				
			}
		}.start();
		
		return null;
	}
	
	public static Drawable loadimageFromUrl(String imageUrl)
	{
		InputStream is = null;
		try {
			is = new FileInputStream(new File(imageUrl));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		Drawable drawable = Drawable.createFromStream(is, "src name: " + imageUrl);
		return drawable;
	}
}
