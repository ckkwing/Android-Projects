package com.jecfbagsx.android.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R;
import android.graphics.Bitmap;

import com.jecfbagsx.android.utils.FileHelper;

public class PhotoAlbum {
	private String path;
	private String name;
	// private String desc;
	private List<String> photosList = new ArrayList<String>();
	private List<String> videosList = new ArrayList<String>();
	private List<Bitmap> thumbnailList = new ArrayList<Bitmap>();
	private MediaStoreAdapter adapter;

	public PhotoAlbum(String path, String name, MediaStoreAdapter adapter) {
		super();
		this.path = path;
		this.name = name;
		this.adapter = adapter;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<String> getPhotosList() {
		return photosList;
	}

	public void setPhotosList(List<String> photosList) {
		this.photosList = photosList;
	}

	public List<String> getVideosList() {
		return videosList;
	}

	public String[] getPhotoNamesArray() {
		int size = photosList.size();
		String[] arr = new String[size];
		for (int i = 0; i < size; i++) {
			arr[i] = FileHelper.getFileNameFromFullPath(photosList.get(i));
		}
		return arr;
	}

	public void setVideosList(List<String> videosList) {
		this.videosList = videosList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return name + "(" + (photosList.size() + videosList.size()) + ")";
	}

	public void genarateThumbnail() {
		int iCount = 0;
		for (String f : photosList) {
			try {
				Bitmap bitmap = FileHelper.getRefinedBitmap(f,
						FileHelper.MINSIDELENGTH, FileHelper.MAXNUMOFPIXELS);
				Bitmap newBit = Bitmap.createScaledBitmap(bitmap, 64, 64, true);
				thumbnailList.add(newBit);
				iCount++;
			} catch (Exception ex) {

			}
			if (iCount >= 4) {
				break;
			}
		}

		if (videosList.size() > 0 && iCount < 4) {
			if (adapter != null) {
				for (String v : videosList) {
					Bitmap bmp = adapter.getVideoThumbnail(v);
					if (bmp != null) {
						thumbnailList.add(bmp);
						iCount++;
					}

					if (iCount >= 4) {
						break;
					}
				}
			}
		}
	}

	public List<Bitmap> getThumbnailList() {
		return thumbnailList;
	}

	public boolean isPhotoAlbum() {
		return (photosList.size() > 0);
	}

	public boolean isVideoAlbum() {
		return (videosList.size() > 0);
	}
}
