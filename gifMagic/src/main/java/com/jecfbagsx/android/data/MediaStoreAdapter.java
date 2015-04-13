package com.jecfbagsx.android.data;

import java.util.ArrayList;
import java.util.List;

import com.jecfbagsx.android.utils.VideoThumbnailUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

public class MediaStoreAdapter {
	private boolean isInited;
	private List<VideoThumbInfo> videoThumbnailList = new ArrayList<VideoThumbInfo>();
	private VideoThumbnailUtils videoUtils;

	private static final String[] mediaColumns = new String[] {
			MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID,
			MediaStore.Video.Media.SIZE, MediaStore.Video.Media.MIME_TYPE,
			MediaStore.Video.Media.DURATION };

	public MediaStoreAdapter() {
	}

	public boolean init(Activity context) {
		isInited = true;
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null,
					null, null);
		} catch (Exception e) {
			
		}
		
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				{
					do {
						try {
							VideoThumbInfo info = new VideoThumbInfo();
							int id = cursor
									.getInt(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
							info.setId(id);

							String mimeType = cursor
									.getString(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
							info.setMimeType(mimeType);
							String path = cursor
									.getString(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
							info.setPath(path);
							long duration = cursor
									.getInt(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
							info.setDuration(duration);
							long size = cursor
									.getLong(cursor
											.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
							info.setSize(size);
							ContentResolver crThumb = context
									.getContentResolver();
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inSampleSize = 1;
							Bitmap curThumb = MediaStore.Video.Thumbnails
									.getThumbnail(
											crThumb,
											id,
											MediaStore.Video.Thumbnails.MICRO_KIND,
											options);
							info.setThumbnail(curThumb);
							videoThumbnailList.add(info);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					} while (cursor.moveToNext());
					cursor.close();
				}
			}
		}
		videoUtils = new VideoThumbnailUtils();
		if (!videoUtils.init()) {
			videoUtils = null;
		}
		return true;
	}

	public Bitmap getVideoThumbnail(String vPath) {
		if (!isInited || vPath == null || "".equals(vPath)) {
			return null;
		}
		Bitmap bmp = null;
		for (VideoThumbInfo info : videoThumbnailList) {
			if (vPath.equalsIgnoreCase(info.getPath())) {
				bmp = info.getThumbnail();
				break;
			}
		}

		if (bmp == null && videoUtils != null) {
			bmp = videoUtils.getVideoThumbnail(vPath,
					MediaStore.Video.Thumbnails.MICRO_KIND);
		}

		return bmp;
	}

}
