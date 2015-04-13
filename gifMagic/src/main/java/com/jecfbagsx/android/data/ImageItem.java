package com.jecfbagsx.android.data;

import java.io.File;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageItem {
	private File file = null;
	private boolean isChecked = false;
	private Bitmap thumbnail = null;
	private float rotateDegree = 0;
	private float currentRotateDegree = 0;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isChecked() {
		return isChecked;
	}
	
	public float getRotateDegree()
	{
		return currentRotateDegree;
	}

	public Bitmap getThumbnail() {
		postRotate();
		return thumbnail;
	}
	
	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	private void postRotate() {
		postRotate(rotateDegree);
	}
	
	public void postRotate(float degrees)
	{
		rotateDegree = degrees;
		if (thumbnail == null || 0 == rotateDegree)
			return;
		try {
			Matrix mat = new Matrix();
	        mat.setRotate(rotateDegree);
	        Bitmap newBitmap = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), mat, true);
	        thumbnail.recycle();
	        thumbnail = newBitmap;
	        currentRotateDegree += degrees;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		rotateDegree = 0;
	}

	public void setChecked(boolean isChecked) {
		if (this.isChecked != isChecked) {
			this.isChecked = isChecked;
			// setChanged();
			// notifyObservers(this.isChecked);
		}
	}

	public ImageItem(File file) {
		// TODO Auto-generated constructor stub
		this.file = file;
//		try {
//			thumbnail = FileHelper.getRefinedBitmap(file.getAbsolutePath(),
//					FileHelper.MINSIDELENGTH, FileHelper.MAXNUMOFPIXELS);
//		} catch (Exception e) {
//			// TODO: handle exception
//			Log.i(this.getClass().getName(),
//					"get thumbnail exception: " + e.getMessage());
//		}
	}
}
