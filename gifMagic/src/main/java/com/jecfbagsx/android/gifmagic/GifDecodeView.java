package com.jecfbagsx.android.gifmagic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import com.jecfbagsx.android.data.ImageItem;
import com.jecfbagsx.android.gifmanage.GifAction;
import com.jecfbagsx.android.gifmanage.GifDecoder;
import com.jecfbagsx.android.utils.FileHelper;

public class GifDecodeView implements GifAction{
	
	private String LinkImgPath;
	private int LinkImgSize = 0;
	
	public void parseOk(boolean parseStatus, int frameIndex)
	{
		
	}
	
	public void LinkSelectedItems(List<ImageItem> list) {
		
		int size = list.size();
		if(size <= 1)
		{
			return;
		}
		
		if(size > 4)
		{
			size = 4;
		}
		
		LinkImgPath = FileHelper.getNextAppTempPath();
		
		GifDecoder gifDecoder = null;
		
		for(int i = 0; i < size; i++)
		{
			gifDecoder = GetGifBitMaps(list.get(i));
			SaveBitmap(gifDecoder);
			gifDecoder.free();
		}
	}
	
	public void MergeSelectedItems(List<ImageItem> list) {
		
		int size = list.size();
		if(size <= 1)
		{
			return;
		}
		
		if(size > 4)
		{
			size = 4;
		}
		
		List<GifDecoder> gifList = new ArrayList<GifDecoder>();
		
		for(int i = 0; i < size; i++)
		{
			gifList.add(GetGifBitMaps(list.get(i)));
		}
		
	//	List<Bitmap> combinedBitmaps = CombineImages(gifDecoder1, gifDecoder2);
		
	//	SaveBitmap(combinedBitmaps);
	}
	public void SaveBitmap(GifDecoder gifDecoder)
	{
		int size = gifDecoder.getFrameCount();
		
		for(int i = 0; i < size; i++)
		{
			SaveBitmap(gifDecoder.getFrameImage(i), LinkImgPath+"/"+LinkImgSize);
			LinkImgSize ++;
		}
	}
	
	public void SaveBitmap(List<Bitmap> combinedBitmaps)
	{
	
        for(int i = 0; i < combinedBitmaps.size(); i++)
        {
        	SaveBitmap(combinedBitmaps.get(i), LinkImgPath + "/"+i);
        }
	}

	public void SaveBitmap(Bitmap combinedBitmap, String name)
	{
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String tmpImg = name + ".png";

			OutputStream os = null;
			try {
				os = new FileOutputStream(tmpImg);
				combinedBitmap.compress(CompressFormat.PNG, 100, os);

				os.flush();
				os.close();
			} catch (IOException e) {

			}
		}
	}

	public GifDecoder GetGifBitMaps(ImageItem imageItem){
		
		List<Bitmap> bitmaps = new ArrayList<Bitmap>();
		GifDecoder gifDecoder = null;
		try 
		{
			FileInputStream a = new FileInputStream(imageItem.getFile());
			gifDecoder = new GifDecoder(a, this);
			//gifDecoder.start();
			gifDecoder.run();
			
		} catch (IOException e) 
		{

		}
		
		return gifDecoder;
	}
	
	public List<Bitmap> CombineImages(GifDecoder gifDecoder1, GifDecoder gifDecoder2){
		
		if(gifDecoder1.getFrameCount() == 0 || gifDecoder2.getFrameCount() == 0)
		{
			return null;
		}
		
		List<Bitmap> bitmaps = new ArrayList<Bitmap>();
		
//	//	boolean isLeftMore = bitmapLeft.size() > bitmapRight.size()?true:false;
//		
//		int minCount = bitmapLeft.size() > bitmapRight.size()? bitmapRight.size():bitmapLeft.size();
//		
//		for(int i = 0; i < minCount; i++)
//		{
//			bitmaps.add(CombineImages(bitmapLeft.get(i), bitmapRight.get(i)));
//		}
		
		return bitmaps;
	}
	
	public Bitmap CombineImages(Bitmap bitmapLeft, Bitmap bitmapRight){
		
		int width, height;
		width = bitmapLeft.getWidth() + bitmapRight.getWidth();
		height = bitmapLeft.getHeight() >= bitmapRight.getHeight() ? bitmapLeft.getHeight() : bitmapRight.getHeight();

		Bitmap combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas comboImage = new Canvas(combinedBitmap);

		comboImage.drawBitmap(bitmapLeft, 0f, 0f, null);
		comboImage.drawBitmap(bitmapRight, bitmapLeft.getWidth(), 0f, null);

		comboImage.save();
		
		return combinedBitmap;
	}
	
	public int GetLinkImgSize()
	{
		return LinkImgSize;
	}
	
	public String GetLinkImgPath(int index)
	{
		return LinkImgPath+"/"+index+ ".png";
	}
	
	public void Free()
	{
	
	}
}
