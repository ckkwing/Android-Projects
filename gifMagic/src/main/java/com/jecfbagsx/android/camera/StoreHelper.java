package com.jecfbagsx.android.camera;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jecfbagsx.android.utils.YUV420SP;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;

public class StoreHelper {
	public static boolean store(byte[] data, int format, int width, int height, Matrix matrix, int quality, OutputStream os) {
		boolean result = false;
		switch (format) {
		case PixelFormat.YCbCr_420_SP:
			result = storeYuv420sp(data, width, height, matrix, quality, os);
			break;
		case PixelFormat.JPEG:
			result = storeJpeg(data, width, height, matrix, quality, os);
			break;
		}

		return result;
	}

	private static boolean storeYuv(byte[] data, int format, int width, int height, int quality, OutputStream os) {
		Constructor yuvImageCtor = null;
		Method compressToJpegMethod = null;

		boolean foundYuvImageClass = false;
		try {
			Class yuvImageClass = Class.forName("android.graphics.YuvImage");
			yuvImageCtor = yuvImageClass.getDeclaredConstructor(byte[].class, int.class, int.class, int.class, int[].class);
			compressToJpegMethod = yuvImageClass.getDeclaredMethod("compressToJpeg", Rect.class, int.class, OutputStream.class);
			foundYuvImageClass = true;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		if (foundYuvImageClass) {
			Object yuvImage = null;
			try {
				yuvImage = yuvImageCtor.newInstance(data, format, width, height, null);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			if (yuvImage != null) {
				try {
					return (Boolean) compressToJpegMethod.invoke(yuvImage, new Rect(0, 0, width, height), quality, os);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}
	
	private static boolean storeYuv420sp(byte[] data, int width, int height, Matrix matrix, int quality, OutputStream os) {
		int[] rgbData = new int[height * width];
		decodeYuv420spNative(rgbData, data, width, height);
		Bitmap bitmap = Bitmap.createBitmap(rgbData, width, height, Bitmap.Config.ARGB_8888);
		
		Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return bitmap2.compress(CompressFormat.JPEG, quality, os);
	}
	
	private static boolean storeJpeg(byte[] data, int width, int height, Matrix matrix, int quality, OutputStream os) {
		boolean result = false;
		try {
			os.write(data);
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private static void decodeYuv420spNative(int[] rgb, byte[] yuv420sp, int width, int height) {
		YUV420SP YUV420SP = new YUV420SP();
		YUV420SP.decode(rgb, yuv420sp, width, height);
	}
	
	private static void decodeYuv420sp(int[] rgb, byte[] yuv420sp, int width, int height) {
		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				
				if (y < 0)
					y = 0;
				
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}
				
				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}
	
	private static boolean storeBitmap(Bitmap image, int quality, String filename) {
		boolean result = false;
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			result = image.compress(CompressFormat.JPEG, quality, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = false;
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}
}
