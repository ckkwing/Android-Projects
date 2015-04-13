package com.jecfbagsx.android.utils;

import java.io.File;
import java.sql.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.jecfbagsx.android.data.ImageItem;
import com.jecfbagsx.android.gifmagic.R;


public class ListImageAdapter extends ImageAdapter {

	public ListImageAdapter(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		this.m_checkable = true;
	}

	public ListImageAdapter(Context context, List<ImageItem> imageList, boolean isFromCamera) {
		super(context, imageList,isFromCamera);
		this.m_checkable = true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int mPosition = position;
		final ViewGroup mParent = parent;
		ListViewHolder viewHolder = null;
		if (m_items.isEmpty())
			return convertView;
		if (null == convertView) {

			convertView = m_layoutInflater.inflate(R.layout.list_item_view,
					null);

			viewHolder = new ListViewHolder();

			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.listThumb);
			viewHolder.txtName = (TextView) convertView
					.findViewById(R.id.listName);
			viewHolder.txtSize = (TextView) convertView
					.findViewById(R.id.listSize);
			viewHolder.txtDate = (TextView) convertView
					.findViewById(R.id.listDate);
			viewHolder.checkBox = (CheckBox) convertView
					.findViewById(R.id.listCheckBox);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ListViewHolder) convertView.getTag();
		}
		viewHolder.id = position;
		viewHolder.imageView.setId(position);
		File file = m_items.get(position).getFile();
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
		viewHolder.txtName.setText(file.getName());
		viewHolder.txtSize.setText(FileHelper.getFileSize(file.length()));
		Date date = new Date(file.lastModified());
		viewHolder.txtDate.setText(date.toLocaleString());
		viewHolder.checkBox.setId(position);
		viewHolder.checkBox.setVisibility(this.m_checkable ? View.VISIBLE
				: View.GONE);
		viewHolder.checkBox.setChecked(m_items.get(position).isChecked());

		convertView
		.setBackgroundResource(position == getSelectedPosition() ? R.drawable.selected_rounded_retangle
				: R.drawable.rounded_rectangle);
		return convertView;
	}

	public class ListViewHolder extends ViewHolder {
		public TextView txtName;
		public TextView txtSize;
		public TextView txtDate;
	}

}
