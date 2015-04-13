package com.jecfbagsx.android.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.jecfbagsx.android.data.ImageItem;
import com.jecfbagsx.android.gifmagic.R;

public class ImageAdapter extends BaseAdapter {
	

	private Context m_context = null;
	protected LayoutInflater m_layoutInflater = null;
	protected static final int PROCESS_REFRESH = 1;
	private String[] m_supportedFileTypeStrings = { ".gif", ".jpg", ".png" };
	protected List<ImageItem> m_items = new ArrayList<ImageItem>();
	protected AsyncImageLoader m_asyncLoader = new AsyncImageLoader();
	protected boolean m_isFromCamera;
	protected int m_selectedPosition = -1;
	public int getSelectedPosition() {
		return m_selectedPosition;
	}
	
	public void setSelectedPosition(int position) {
		m_selectedPosition = position;
	}

	public List<ImageItem> getItems() {
		synchronized (m_context) {
			return m_items;
		}
	}

	protected boolean m_checkable = true;

	public boolean getCheckable() {
		return this.m_checkable;
	}

	public void setCheckable(boolean checkable) {
		this.m_checkable = checkable;
	}

	public ImageAdapter(Context context) {
		this.m_context = context;
		this.m_layoutInflater = (LayoutInflater) m_context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Intent intent = ((Activity) m_context).getIntent();
		Bundle bundle = intent.getExtras();
		String pathString = bundle
				.getString(ActivityActions.EXTRA_MULTIPLE_SELECTION_SOUCEPATH);
		File dirFile = new File(pathString);

		if (dirFile.exists() && dirFile.isDirectory() && dirFile.canRead()) {
			File[] imgFiles = dirFile.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					// TODO Auto-generated method stub
					for (String extension : m_supportedFileTypeStrings) {
						if (filename.toLowerCase().endsWith(extension))
							return true;
					}
					return false;
				}
			});

			if (null != imgFiles) {
				for (File file : imgFiles) {
					ImageItem item = new ImageItem(file);
					m_items.add(item);
				}
			}

		}

	}

	public ImageAdapter(Context context, List<ImageItem> imageList, boolean isFromCamera) {
		this.m_context = context;
		this.m_layoutInflater = (LayoutInflater) m_context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.m_items = imageList;
		this.m_isFromCamera = isFromCamera;
	}
	
	public void sortAscending()
	{
		if (m_isFromCamera) {
			SortHelper.cameraAscendingSort(m_items);
		} else {
			SortHelper.ascendingSort(m_items);
		}
		setSelectedPosition(-1);
		notifyDataSetChanged();
	}
	
	public void sortDecreasing() {
		if (m_isFromCamera) {
			SortHelper.cameraDecreasingSort(m_items);
		} else {
			SortHelper.decreasingSort(m_items);
		}
		setSelectedPosition(-1);
		notifyDataSetChanged();
	}

	public boolean removeItem(int position) {
		boolean bRel = false;
		try {
			final ImageItem item = (ImageItem) getItem(position);
			if (null != item) {
				bRel = item.getFile().delete();
			}
			if (bRel) {
				m_items.remove(position);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.i(this.getClass().getName(),
					"removeItem exception: " + e.getMessage());
		}
		return bRel;
	}

	public boolean removeItem(ImageItem item) {
		boolean bRel = false;
		try {
			if (null != item) {
				bRel = item.getFile().delete();
			}
			if (bRel) {
				m_items.remove(item);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.i(this.getClass().getName(),
					"removeItem exception: " + e.getMessage());
		}

		return bRel;
	}

	public void removeAllItem() {
		try {
			for (ImageItem imageItem : m_items) {
				imageItem.getFile().delete();
			}
			m_items.clear();
		} catch (Exception e) {
			// TODO: handle exception
			Log.i(this.getClass().getName(),
					"removeAllItem exception: " + e.getMessage());
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int mPosition = position;
		final View mConvertView = convertView;
		final ViewGroup mParent = parent;
		ViewHolder viewHolder = null;
		if (m_items.isEmpty())
			return convertView;
		if (null == convertView) {
			convertView = m_layoutInflater.inflate(R.layout.gallery_item_view,
					null);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.thumbImage);
			viewHolder.checkBox = (CheckBox) convertView
					.findViewById(R.id.itemCheckBox);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.id = position;
		viewHolder.imageView.setId(position);
		final String imageUrl = m_items.get(position).getFile().getAbsolutePath();
		viewHolder.imageView.setTag(imageUrl);
//		Drawable cacheImage = m_asyncLoader.loadDrawable(imageUrl, new IImageCallback() {
//			
//			@Override
//			public void imageLoaded(Drawable drawable, String imageUrl) {
//				// TODO Auto-generated method stub
//				
//				ImageView imageView = (ImageView)mParent.findViewWithTag(imageUrl);
//				if ( imageView != null && drawable != null)
//				{
//					imageView.setImageDrawable(drawable);
//				}
//
//			}
//		});
//		if ( cacheImage == null )
//		{
//			viewHolder.imageView.setImageResource(android.R.drawable.ic_menu_search);
//		}
//		else {
//			viewHolder.imageView.setImageDrawable(cacheImage);
//		}
		Bitmap thumbnail = m_items.get(position).getThumbnail();
		if (thumbnail == null)
		{
			viewHolder.imageView.setImageResource(android.R.drawable.ic_menu_search);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ImageItem item = m_items.get(mPosition);
					try {
						item.setThumbnail(FileHelper.getRefinedBitmap(item.getFile().getAbsolutePath(),
								FileHelper.MINSIDELENGTH, FileHelper.MAXNUMOFPIXELS));
					} catch (Exception e) {
						// TODO: handle exception
						Log.i(this.getClass().getName(),
								"get thumbnail exception: " + e.getMessage());
					}
					Message message = new Message();
					message.what = PROCESS_REFRESH;
					message.arg1 = mPosition;
					Bundle bundle = new Bundle();
					bundle.putString("imageUrl", item.getFile().getAbsolutePath());
					message.setData(bundle);
					message.obj = mParent;
					handler.sendMessage(message);
				}
			}).start();
		}
		else
			viewHolder.imageView.setImageBitmap(thumbnail);
		viewHolder.checkBox.setId(position);
		viewHolder.checkBox.setVisibility(this.m_checkable ? View.VISIBLE
				: View.GONE);
		viewHolder.checkBox.setChecked(m_items.get(position).isChecked());
		
		convertView
				.setBackgroundResource(position == getSelectedPosition() ? R.drawable.selected_rounded_retangle
						: R.drawable.rounded_rectangle);
		
		return convertView;
	}
	
	protected Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case PROCESS_REFRESH:
				//notifyDataSetChanged();
				Bundle bundle = message.getData();
				ViewGroup parent = (ViewGroup)message.obj;
				ImageView imageView = (ImageView)parent.findViewWithTag(bundle.getString("imageUrl"));
				
				if ( imageView != null)
				{
					imageView.setImageBitmap(m_items.get(message.arg1).getThumbnail());
				}
				break;
			}
		}
	};

	public class ViewHolder {
		ImageView imageView;
		public CheckBox checkBox;
		int id;
	}

}