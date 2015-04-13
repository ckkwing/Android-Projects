package com.jecfbagsx.android.customercontroler;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimationImageView extends ImageView {
	
	private Map<String, Float> m_fileMap = new LinkedHashMap<String, Float>();
	private List<String> m_filePathList = new ArrayList<String>();
	private int m_duration = 1000;
	private boolean m_isStop = false;
	private int m_currentIndex = 0;
	private ChangeImageTask m_task = null;

	public AnimationImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public AnimationImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public AnimationImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void startAnimation() {
		m_isStop = false;
		m_currentIndex = 0;
		if (null != m_task)
		{
			if (m_task.isCancelled())
			{
				m_task = null;
			}
		}
		if (null == m_task)
		{
			m_task = new ChangeImageTask();
			m_task.execute(null);
		}
	}
	
	public void stopAnimation() {
		if (null != m_task)
		{
			m_isStop = true;
			m_task.cancel(m_isStop);
		}
	}
	
	public void setImageSource(Map<String, Float> fileMap)
	{
		m_fileMap = fileMap;
		m_filePathList.clear();
		for (Iterator<String> iterator = m_fileMap.keySet().iterator(); iterator
		.hasNext();) {
			String path = iterator.next();
			m_filePathList.add(path);
		}
	}
	
	
	public void setDuration(int millisecond)
	{
		this.m_duration = millisecond;
	}
	
	private int cleanValue(int val, int limit)  
    {  
        if (val > limit)
        	return 0;
        else {
			return val;
		}
    }
	
	class ChangeImageTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			publishProgress(null);

			long millisecond = SystemClock.elapsedRealtime();
			while (!m_isStop) {
				try {
					Thread.sleep(100);
					long currentMillisecond = SystemClock.elapsedRealtime();
					if ((currentMillisecond - millisecond) >= m_duration)
					{
						publishProgress(null);
						millisecond = currentMillisecond;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
//			BitmapDrawable bitmapDrawable = (BitmapDrawable)getDrawable();
//			if (bitmapDrawable != null)
//			{
//				Bitmap bitmap = bitmapDrawable.getBitmap();
//				if ( !bitmap.isRecycled())
//					bitmap.recycle();
//			}
			m_currentIndex = cleanValue(m_currentIndex + 1, m_fileMap.size()-1);
			String path = m_filePathList.get(m_currentIndex);
			try {
				setImageURI(Uri.fromFile(new File(path)));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				
			}
			
			Matrix mat = new Matrix();
			mat.setRotate(m_fileMap.get(path));
			setImageMatrix(mat);
			invalidate();
			
			super.onProgressUpdate(values);
		}
		
	}

}
