package com.example.echen.myapplication.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.echen.androidcommon.Media.Image;
import com.example.echen.myapplication.R;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/2/6.
 */
public class ImageAdapter extends BaseAdapter {
    private final String TAG = "ImageAdapter";

    protected Context m_context = null;
    protected List<Image> m_images = new ArrayList<>();
    protected LayoutInflater m_layoutInflater = null;

    public class ViewHolder {
        int id;
        ImageView imageView;
        public CheckBox checkBox;
        public TextView txtName;
        public TextView txtSize;
        public TextView txtDate;
    }

    public ImageAdapter(Context context, List<Image> images)
    {
        this.m_context = context;
        if (null == m_context)
            throw new NullPointerException("ImageAdapter: Passed Context is NULL!");
        this.m_layoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.m_images = images;
    }

    @Override
    public int getCount() {
        return m_images.size();
    }

    @Override
    public Object getItem(int position) {
        return m_images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder viewHolder;
            if (m_images.isEmpty())
                return convertView;
            if (null == convertView) {
                convertView = m_layoutInflater.inflate(R.layout.list_item_view,
                        null);
                viewHolder = new ViewHolder();
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
                viewHolder = (ViewHolder) convertView.getTag();
            }

//        File file = m_items.get(position).getFile();

            Image image = m_images.get(position);
            viewHolder.id = position;
            viewHolder.imageView.setId(position);
            viewHolder.txtName.setText(image.getDisplayName());
            viewHolder.txtSize.setText(String.valueOf(image.getSize()));
//        Date date = new Date(file.lastModified());
//        viewHolder.txtDate.setText(date.toLocaleString());
            viewHolder.checkBox.setId(position);
            viewHolder.imageView.setImageDrawable(null);

//            Bitmap thumb = null;
//            if (null != viewHolder.imageView.getDrawable()) {
//                thumb = ((BitmapDrawable) viewHolder.imageView.getDrawable()).getBitmap();
//            }
//            if (null != thumb)
//            {
//                thumb.recycle();
//                thumb = null;
//            }
//            thumb = BitmapFactory.decodeFile(image.getPath(), null);
//            if (null != thumb)
//                viewHolder.imageView.setImageBitmap(thumb);

            String[] projection = {
                    "_data"    ,
                    //"image_id"
            };
            Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                    m_context.getContentResolver(), image.getId(),
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null );
            if( cursor != null && cursor.getCount() > 0 ) {
                cursor.moveToFirst();//**EDIT**
                String uri = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Thumbnails.DATA ) );
                viewHolder.imageView.setImageURI(Uri.parse(uri));
            }
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return convertView;
    }
}
