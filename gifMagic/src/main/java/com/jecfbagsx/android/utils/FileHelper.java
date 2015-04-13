package com.jecfbagsx.android.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.jecfbagsx.android.data.IImageConverter;
import com.jecfbagsx.android.data.ImageSolutionFactory;
import com.jecfbagsx.android.data.PhotoAlbum;
import com.jecfbagsx.android.data.Quality;
import com.jecfbagsx.android.data.ResolutionInfo;
import com.jecfbagsx.android.video.FrameCapture;

public class FileHelper {
	public static final String GIF_MAGIC_ROOT = "GifMagic";
	public static final String GIF_MAGIC_HISTORY = "GifMagic" + File.separator
			+ "GifMagic_Gif";
	public static final String GIF_MAGIC_IMAGE_EDIT = "GifMagic"
			+ File.separator + "GifMagic_Edit";
	public static final String GIF_MAGIC_CAMERA = "GifMagic" + File.separator
			+ "GifMagic_Camera";
	public static final String GIF_MAGIC_VIDEO = "GifMagic" + File.separator
			+ "GifMagic_Video";
	public static final String DISPLAY_GIF_MAGIC_HISTORY = "/SDCard/GifMagic"
			+ File.separator + "GifMagic_Gif";
	public static final String GIF_MAFIC_TEMP = "GifMagic" + File.separator
			+ "Temp";
	private static SimpleDateFormat timeFormat = new SimpleDateFormat(
			"yyyy_MM_dd_HH_mm_ss_SSS");
	private static String[] hiddenFolders = new String[] { "Cache", ".nomedia",
			".thumbnails", "thumbnail", "topic", "head", "TempData", "Data" };
	public static int MINSIDELENGTH = -1;
	public static int MAXNUMOFPIXELS = 64 * 64;
	private static String[] supportedPhotoFileSuffix = { "jpg", "png", "jpeg" };
	private static String[] supportedVideoFileSuffix = { "3gp", "mp4", "mpg",
			"mpeg", "avi", "wmv", "flv", "mkv", "divx", "3g2", "rm", "rmvb",
			"ra", "asf", "mov" };
	public static final String VIDEO_FOLDER_END_FLAG = "__v";

	public static boolean isPhotoSuffixSupported(String fileSuffix) {
		if (fileSuffix == null) {
			return false;
		}
		boolean support = false;
		for (String s : supportedPhotoFileSuffix) {
			if (s.equalsIgnoreCase(fileSuffix)) {
				support = true;
				break;
			}
		}
		return support;
	}

	public static boolean isVideoSuffixSupported(String fileSuffix) {
		if (fileSuffix == null) {
			return false;
		}
		boolean support = false;
		for (String s : supportedVideoFileSuffix) {
			if (s.equalsIgnoreCase(fileSuffix)) {
				support = true;
				break;
			}
		}
		return support;
	}

	public static boolean isHiddenFolder(String path,
			List<String> userHiddenList) {
		String pathName = path.substring(path.lastIndexOf("/") + 1,
				path.length());
		if (pathName.startsWith(".")) {
			return true;
		}

		for (String s : hiddenFolders) {
			if (s.equalsIgnoreCase(pathName)) {
				return true;
			}
		}

		if (userHiddenList != null && userHiddenList.size() > 0) {
			for (String s : userHiddenList) {
				if (s.equalsIgnoreCase(path)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean IsSDCardValid() {
		String sDStateString = android.os.Environment.getExternalStorageState();
		if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}

		return false;
	}

	public static String getInternalStorage() {
		File SDRoot = android.os.Environment.getExternalStorageDirectory();
		return SDRoot.getAbsolutePath();
	}

	public static String getExternalStorage() {
		File SDRoot = android.os.Environment.getExternalStorageDirectory();
		String external = SDRoot.getAbsolutePath() + "-ext";
		return external;
	}

	public static String getAppRootPath() {
		String mPath = "";
		if (IsSDCardValid()) {
			File SDFile = android.os.Environment.getExternalStorageDirectory();
			mPath = SDFile.getAbsolutePath() + File.separator + GIF_MAGIC_ROOT;
			File newPath = new File(mPath);
			if (!newPath.exists()) {
				newPath.mkdirs();
			}
		}
		return mPath;
	}

	public static String getAppEditPath() {
		String mPath = "";
		if (IsSDCardValid()) {
			File SDFile = android.os.Environment.getExternalStorageDirectory();
			mPath = SDFile.getAbsolutePath() + File.separator
					+ GIF_MAGIC_IMAGE_EDIT;
			File newPath = new File(mPath);
			if (!newPath.exists()) {
				newPath.mkdirs();
			}
		}
		return mPath;
	}

	public static String getAppCameraPath() {
		String mPath = "";
		if (IsSDCardValid()) {
			File SDFile = android.os.Environment.getExternalStorageDirectory();
			mPath = SDFile.getAbsolutePath() + File.separator
					+ GIF_MAGIC_CAMERA;
			File newPath = new File(mPath);
			if (!newPath.exists()) {
				newPath.mkdirs();
			}
		}
		return mPath;
	}

	public static String getAppVideoPath() {
		String mPath = "";
		if (IsSDCardValid()) {
			File SDFile = android.os.Environment.getExternalStorageDirectory();
			mPath = SDFile.getAbsolutePath() + File.separator + GIF_MAGIC_VIDEO;
			File newPath = new File(mPath);
			if (!newPath.exists()) {
				newPath.mkdirs();
			}
		}
		return mPath;
	}

	public static String getAppHistoryPath() {
		String mPath = "";
		if (IsSDCardValid()) {
			File SDFile = android.os.Environment.getExternalStorageDirectory();
			mPath = SDFile.getAbsolutePath() + File.separator
					+ GIF_MAGIC_HISTORY;
			File newPath = new File(mPath);
			if (!newPath.exists()) {
				newPath.mkdirs();
			}
		}
		return mPath;
	}

	public static String getAppTempRootPath() {
		String mPath = "";
		if (IsSDCardValid()) {
			File SDFile = android.os.Environment.getExternalStorageDirectory();
			mPath = SDFile.getAbsolutePath() + File.separator + GIF_MAFIC_TEMP;
			File newPath = new File(mPath);
			if (!newPath.exists()) {
				newPath.mkdirs();
			}
		}
		return mPath;
	}

	public static String getNextAppTempPath() {
		String mPath = "";
		if (IsSDCardValid()) {
			File SDFile = android.os.Environment.getExternalStorageDirectory();
			mPath = SDFile.getAbsolutePath() + File.separator + GIF_MAFIC_TEMP
					+ File.separator + getNextFileorFolderName();
			File newPath = new File(mPath);
			if (!newPath.exists()) {
				newPath.mkdirs();
			}

		}
		return mPath;
	}

	public static String getNextAppVideoTempPath() {
		String mPath = "";
		if (IsSDCardValid()) {
			File SDFile = android.os.Environment.getExternalStorageDirectory();
			mPath = SDFile.getAbsolutePath() + File.separator + GIF_MAFIC_TEMP
					+ File.separator + getNextFileorFolderName()
					+ VIDEO_FOLDER_END_FLAG;
			File newPath = new File(mPath);
			if (!newPath.exists()) {
				newPath.mkdirs();
			}

		}
		return mPath;
	}

	public static String getNextFileorFolderName() {
		return timeFormat.format(System.currentTimeMillis());
	}

	private static void DeleteRecursive(File dir) {

		Log.i("FileHelper", "CleanTempFolder:" + dir.toString());

		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				File temp = new File(dir, children[i]);
				if (temp.isDirectory()) {
					DeleteRecursive(temp);
				} else {
					boolean b = temp.delete();
					if (b == false) {
					}
				}
			}
		}
		dir.delete();
	}

	public static void cleanTempPath() {
		File tempPath = new File(getAppTempRootPath());
		DeleteRecursive(tempPath);
	}

	public static String getFileSize(long sizeofByte) {
		if (sizeofByte < 0)
			return "0B";
		DecimalFormat dfFormat = new DecimalFormat("###.00");
		if ((sizeofByte / 1024.0 / 1024.0 / 1024.0) > 1.0) {
			return dfFormat.format((sizeofByte / 1024.0 / 1024.0 / 1024.0))
					+ " GB";
		} else if ((sizeofByte / 1024.0 / 1024.0) > 1.0) {
			return dfFormat.format((sizeofByte / 1024.0 / 1024.0)) + " MB";
		} else if ((sizeofByte / 1024.0) > 1.0) {
			return dfFormat.format((sizeofByte / 1024.0)) + "KB";
		} else {
			return sizeofByte + " B";
		}
	}

	public static Bitmap getRefinedBitmap(String pathName, int MINSIDELENGTH,
			int MAXNUMOFPIXELS) {
		Bitmap bitmap = null;
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, option);
		option.inSampleSize = computeSampleSize(option, MINSIDELENGTH,
				MAXNUMOFPIXELS);
		option.inJustDecodeBounds = false;
		try {
			bitmap = BitmapFactory.decodeFile(pathName, option);
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("FileHelper",
					"set image: " + pathName + ", exception: " + e.getMessage());
		}
		return bitmap;
	}

	public static Bitmap getRefinedBitmap(String pathName, int MINSIDELENGTH,
			int MAXNUMOFPIXELS, ResolutionInfo info) {
		Bitmap bitmap = null;
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, option);
		if (option.outWidth <= 854 && option.outHeight <= 854) {
			Bitmap src = BitmapFactory.decodeFile(pathName);
			ResolutionInfo resInfo = new ResolutionInfo();
			resInfo.setWidth(option.outWidth);
			resInfo.setHeight(option.outHeight);
			// bitmap = FrameCapture.scaleBitmap(pathName,
			// refinedRes.getWidth(), refinedRes.getHeight());
			// bitmap = Bitmap.createScaledBitmap(src, refinedRes.getWidth(),
			// refinedRes.getHeight(), true);
			bitmap = zoomBitmap(src, info);
			src.recycle();
		} else {
			option.inSampleSize = computeSampleSize(option, MINSIDELENGTH,
					MAXNUMOFPIXELS);
			option.inJustDecodeBounds = false;
			try {
				bitmap = BitmapFactory.decodeFile(pathName, option);
			} catch (Exception e) {
				Log.i("FileHelper", "set image: " + pathName + ", exception: "
						+ e.getMessage());
			}
		}
		return bitmap;
	}

	public static Bitmap zoomBitmap(Bitmap target, ResolutionInfo refinedRes) {
		int width = target.getWidth();
		int height = target.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) refinedRes.getWidth()) / width;
		float scaleHeight = ((float) refinedRes.getHeight()) / height;
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap result = Bitmap.createBitmap(target, 0, 0, width, height,
				matrix, true);
		return result;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static String convertFileSize(long fileSize) {
		String filename = "";
		float size = 0f;
		String unit = "";
		float attchSize = fileSize;
		if (attchSize < 1024) {
			size = (float) (attchSize);
			unit = " B";
		} else {
			float k = (float) (attchSize / 1024);
			if (k < 1024) {
				size = (float) (k);
				unit = " KB";
			} else {
				float m = (float) (k / 1024);
				if (m < 1024) {
					size = (float) (m);
					unit = " MB";
				} else {
					size = (float) (m / 1024);
					unit = " GB";
				}
			}
		}
		DecimalFormat formater = new DecimalFormat("#0.##");
		filename = formater.format(size).toString();
		if ((filename.indexOf(".00")) != -1) {
			filename = filename.substring(0, filename.indexOf(".00"));
		}
		filename += unit;
		return filename;
	}

	public static ResolutionInfo getImageResolution(String filePath) {
		ResolutionInfo res = new ResolutionInfo(0, 0);
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, option);
		res.setWidth(option.outWidth);
		res.setHeight(option.outHeight);
		return res;
	}

	public static ResolutionInfo getRefinedResolutionInfo(
			ResolutionInfo resource, int matchLength) {
		ResolutionInfo res = new ResolutionInfo(0, 0);
		try {
			int width = resource.getWidth();
			int height = resource.getHeight();

			if (width <= matchLength && height <= matchLength) {
				res.setWidth(width);
				res.setHeight(height);
				return res;
			}

			int max = width >= height ? width : height;

			double scale = (double) max * 1.0 / matchLength;

			if (width >= height) {
				int t = (int) (height / scale);
				if (t % 2 != 0) {
					t = t + 1;
				}
				res.setWidth(matchLength);
				res.setHeight(t);
			} else {
				int t = (int) (width / scale);
				if (t % 2 != 0) {
					t = t + 1;
				}
				res.setWidth(t);
				res.setHeight(matchLength);
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.i("FileHelper", e.getMessage());
		}

		return res;
	}

	public static boolean saveBitmapAsPng(Bitmap bitmap, String path) {
		boolean bstate = false;
		File file = new File(path);
		try {
			BufferedOutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(file));
			bstate = bitmap
					.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i("Hub", "FileNotFoundException: " + e.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bstate;
	}

	public static String getFileNameFromFullPath(String path) {
		if (path == null) {
			return "";
		}

		return path.substring(path.lastIndexOf("/") + 1, path.length());
	}

	public static byte[] readFileImage(String filename) throws IOException {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(filename));
		int len = bufferedInputStream.available();
		byte[] bytes = new byte[len];
		int r = bufferedInputStream.read(bytes);
		if (len != r) {
			bytes = null;
			throw new IOException("read file is incorrectly...");
		}
		bufferedInputStream.close();
		return bytes;
	}

	public static boolean isNeedRefineBitmap(Set<String> pathSet) {
		ResolutionInfo info = SettingsHelper.getResolutionInfo();
		long totalSize = 0;
		long maxSize = 1 * 1024 * 1024;
		int maxBorderWidth = 440;
		boolean isNeedRefine = true;
		isNeedRefine = (pathSet.size() <= 10 && info.getType() == Quality.High) ? false
				: true;
		if (!isNeedRefine) {
			for (Iterator iterator = pathSet.iterator(); iterator.hasNext();) {
				Object key = iterator.next();
				String url = (String) key;
				File file = new File(url);
				if (file.exists() && file.isFile() && file.canRead()) {
					totalSize += file.length();
					if (totalSize > maxSize) {
						isNeedRefine = true;
						break;
					}
				}

				ResolutionInfo imageInfo = FileHelper.getImageResolution(url);
				if (imageInfo.getWidth() > maxBorderWidth
						|| imageInfo.getHeight() > maxBorderWidth) {
					isNeedRefine = true;
					break;
				}

			}
		}
		return isNeedRefine;
	}

	public static Bitmap getBitmapAutomatically(String path) {
		Bitmap bitmap = null;
		File file = new File(path);
		InputStream is = null;
		Drawable drawable = null;
		try {
			is = new BufferedInputStream(file.toURL().openStream(), 8000);
			drawable = Drawable.createFromStream(is, path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (drawable != null) {
			bitmap = ((BitmapDrawable) drawable).getBitmap();
		} else {
			List<String> list = new ArrayList<String>();
			list.add(path);
			IImageConverter iImageConverter = ImageSolutionFactory
					.getImageSolution(list);
			ResolutionInfo info = iImageConverter.getTargetResolution();
			bitmap = FileHelper.getRefinedBitmap(path,
					FileHelper.MINSIDELENGTH,
					info.getWidth() * info.getHeight());
		}
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public static Uri getMediaStoreUri(Context context, String photoPath) {
		Cursor photoCursor = null;
		Uri uri = null;
		try {
			String[] projection = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			photoCursor = context.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
					null, null, null);

			if (photoCursor != null && photoCursor.moveToFirst()) {
				{
					do {
						String photoFilePath = photoCursor
								.getString(photoCursor
										.getColumnIndex(MediaStore.Images.Media.DATA));
						if (photoFilePath.equalsIgnoreCase(photoPath)) {
							int id = photoCursor
									.getInt(photoCursor
											.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
							uri = ContentUris
									.withAppendedId(
											android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
											id);
							break;
						}
					} while (photoCursor.moveToNext());
				}
			}
		} finally {
			if (photoCursor != null) {
				photoCursor.close();
			}
		}

		return uri;
	}

}
