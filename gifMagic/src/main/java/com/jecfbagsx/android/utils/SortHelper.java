package com.jecfbagsx.android.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jecfbagsx.android.data.ImageItem;

public class SortHelper {

	private static String splite = "_gifmagic_";
	public static void cameraDecreasingSort(List<ImageItem> list)
	{
		Collections.sort(list, new Comparator<ImageItem>() {

			@Override
			public int compare(ImageItem object1, ImageItem object2) {
				// TODO Auto-generated method stub
				try {
					String strF1 = object1.getFile().getName();
					String strF2 = object2.getFile().getName();

					int iStartF1 = strF1.indexOf(splite);
					int iEndF1 = strF1.lastIndexOf(".");
					String count1 = strF1.substring(iStartF1 + 1,
							iEndF1);

					int iStartF2 = strF2.indexOf(splite);
					int iEndF2 = strF2.lastIndexOf(".");
					String count2 = strF2.substring(iStartF2 + 1,
							iEndF2);
					return Long.valueOf(count1).compareTo(Long.valueOf(count2));
				} catch (Exception e) {
					// TODO: handle exception
					return -1;
				}
			}
		});
	}
	
	public static void cameraAscendingSort(List<ImageItem> list)
	{
		Collections.sort(list, new Comparator<ImageItem>() {

			@Override
			public int compare(ImageItem object1, ImageItem object2) {
				// TODO Auto-generated method stub
				try {
					String strF1 = object1.getFile().getName();
					String strF2 = object2.getFile().getName();

					int iStartF1 = strF1.indexOf(splite);
					int iEndF1 = strF1.lastIndexOf(".");
					String count1 = strF1.substring(iStartF1 + 1,
							iEndF1);

					int iStartF2 = strF2.indexOf(splite);
					int iEndF2 = strF2.lastIndexOf(".");
					String count2 = strF2.substring(iStartF2 + 1,
							iEndF2);
					return Long.valueOf(count2).compareTo(Long.valueOf(count1));
				} catch (Exception e) {
					// TODO: handle exception
					return -1;
				}
			}
		});
	}
	
	public static void decreasingSort(List<ImageItem> list)
	{
		Collections.sort(list, new Comparator<ImageItem>() {

			@Override
			public int compare(ImageItem object1, ImageItem object2) {
				// TODO Auto-generated method stub
				try {
					return Long.valueOf(object1.getFile().lastModified()).compareTo(Long.valueOf(object2.getFile().lastModified()));
				} catch (Exception e) {
					// TODO: handle exception
					return -1;
				}
			}
		});
	}
	
	public static void ascendingSort(List<ImageItem> list)
	{
		Collections.sort(list, new Comparator<ImageItem>() {

			@Override
			public int compare(ImageItem object1, ImageItem object2) {
				// TODO Auto-generated method stub
				try {
					return Long.valueOf(object2.getFile().lastModified()).compareTo(Long.valueOf(object1.getFile().lastModified()));
				} catch (Exception e) {
					// TODO: handle exception
					return -1;
				}
			}
		});
	}
}
