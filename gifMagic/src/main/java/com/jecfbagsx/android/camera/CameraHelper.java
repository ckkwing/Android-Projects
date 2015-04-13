package com.jecfbagsx.android.camera;

import java.lang.reflect.*;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import android.hardware.Camera;
import android.media.ExifInterface;
import android.graphics.Matrix;
import android.content.Context;
import android.view.WindowManager;
import android.view.Display;

public class CameraHelper {
    static class CameraInfo {
        static final int CAMERA_FACING_BACK = 0;
        static final int CAMERA_FACING_FRONT = 1;
        static final int CAMERA_FACING_DEFAULT = CAMERA_FACING_BACK;
        static final int CAMERA_ORIENTATION_NATURE_PORTRAIT = 90;
        static final int CAMERA_ORIENTATION_NATURE_LANDSCAPE = 0;
        static final int CAMERA_ORIENTATION_DEFAULT = CAMERA_ORIENTATION_NATURE_PORTRAIT;
        
        CameraInfo() {
        	mFacing = CAMERA_FACING_DEFAULT;
        	mOrientation = CAMERA_ORIENTATION_DEFAULT;
        	mCalibrated = false;
        }
        
        CameraInfo(int facing, int orientation) {
        	mFacing = facing;
        	mOrientation = orientation;
        	mCalibrated = true;
        }
        
		void calibrate(boolean isNaturePortrait) {
			if (mCalibrated)
				return;

			mOrientation = isNaturePortrait ? CAMERA_ORIENTATION_NATURE_PORTRAIT : CAMERA_ORIENTATION_NATURE_LANDSCAPE;

			mCalibrated = true;
		}
        
        int mFacing;
        int mOrientation;
        boolean mCalibrated;
    };
      
    private static final int DEFAULT_CAMERA_NUMBER = 1;
	private static final CameraInfo[] sCameraInfos = getCameraInfos();
	
    public static int getNumberOfCameras() {
    	int number = DEFAULT_CAMERA_NUMBER;
    	
		try {
			number = (Integer)(Camera.class.getDeclaredMethod("getNumberOfCameras").invoke(null));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
    
    	return number;
    }

    public static CameraInfo getCameraInfo(int cameraId) {
    	if (cameraId >= getNumberOfCameras())
    		return null;

    	int facing = CameraInfo.CAMERA_FACING_DEFAULT;
    	int orientation = CameraInfo.CAMERA_ORIENTATION_DEFAULT;
    	boolean got = false;
    	
    	try {
			Class cameraInfoClass = null;
			Class[] classesOfCamera = Camera.class.getDeclaredClasses();
			if (classesOfCamera != null) {
				for (int i = 0; i < classesOfCamera.length; i++) {
					Class classOfCamera = classesOfCamera[i];
					
					if (classOfCamera.getSimpleName().compareTo("CameraInfo") == 0)
						cameraInfoClass = classOfCamera;
				}
			}
			
			if (cameraInfoClass != null) {	
				Object cameraInfoObject = cameraInfoClass.newInstance();
				Camera.class.getDeclaredMethod("getCameraInfo", int.class, cameraInfoClass).invoke(null, cameraId, cameraInfoObject);
				got = true;
				facing = cameraInfoClass.getField("facing").getInt(cameraInfoObject);
				orientation = cameraInfoClass.getField("orientation").getInt(cameraInfoObject);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}	
		
		if (got)
			return new CameraInfo(facing, orientation);
		else
			return new CameraInfo();
    }
    
    public static CameraInfo[] getCameraInfos() {
    	int number = getNumberOfCameras();
    	
    	CameraInfo[] cameraInfos = new CameraInfo[number];

    	for (int index = 0; index < number; index++) {  		
    		cameraInfos[index] = getCameraInfo(index);
    	}
    	
    	return cameraInfos;
    } 	
    
    public static boolean isCameraChangeSupported() {
		boolean supported = sCameraInfos.length > 1;
		
		return supported;
	}
	
    public static int getCameraDisplayOrientation(int cameraId, int rotation) {
		CameraInfo cameraInfo = sCameraInfos[cameraId];
		
    	int orientation = 0;
		if (cameraInfo.mFacing == CameraInfo.CAMERA_FACING_FRONT) {
			orientation = (cameraInfo.mOrientation + rotation) % 360;
			orientation = (360 - orientation) % 360;
		} else {
			orientation = (cameraInfo.mOrientation + 360 - rotation) % 360;
		}
		
		return orientation;
    }
    
    public static int getCameraPictureOrientation(int cameraId, int rotation) {
		CameraInfo cameraInfo = sCameraInfos[cameraId];
		
    	int orientation = 0;
		if (cameraInfo.mFacing == CameraInfo.CAMERA_FACING_FRONT) {
			orientation = (cameraInfo.mOrientation + 360 - rotation) % 360;
		} else {
			orientation = (cameraInfo.mOrientation + rotation) % 360;
		}
		
		return orientation;
    }
    
    public static Matrix getCameraDisplayMatrix(int cameraId, int rotation) {
		CameraInfo cameraInfo = sCameraInfos[cameraId];
		
    	Matrix matrix = new Matrix();
    	
		int orientation = 0;
		if (cameraInfo.mFacing == CameraInfo.CAMERA_FACING_FRONT) {
			orientation = (cameraInfo.mOrientation + rotation) % 360;
		} else {
			orientation = (cameraInfo.mOrientation + 360 - rotation) % 360;
		}
		
		matrix.postRotate(orientation);
		
		if (cameraInfo.mFacing == CameraInfo.CAMERA_FACING_FRONT)
			matrix.postScale(-1, 1);

    	return matrix;
    }
        
    public static Camera openCamera(int cameraId) {
    	if (cameraId >= getNumberOfCameras())
    		return null;
    	
		try {
			return (Camera)(Camera.class.getDeclaredMethod("open", int.class).invoke(null, cameraId));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
    	
    	return Camera.open();
    }
    	
	private static boolean mCalibrated;
	public static void calibrateCameraInfos(Context context) {		
		if (mCalibrated)
			return;
		
		boolean isNaturePortrait = isNaturePortrait(context);
		
		for (int i = 0; i < sCameraInfos.length; i++)
			sCameraInfos[i].calibrate(isNaturePortrait);
		
		mCalibrated = true;
	}
	
	public static int[] chooseBestPreviewSize(Camera camera, int requestSize[], boolean pixelFrist) {	
		Camera.Parameters params = camera.getParameters();
		
		Camera.Size bestSize = params.getPreviewSize();
		
		List<Camera.Size> sizes = null;

		try {
			sizes = (List<Camera.Size>)(params.getClass().getDeclaredMethod("getSupportedPreviewSizes").invoke(params));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		if (sizes != null) {
			class DeviationComparator implements Comparator<Camera.Size> {
				private int mWidth = 0;
				private int mHeight = 0;
				private int mPixelCount = 0;
				private float mAspectRatio = 0f;
				boolean mPixelFrist = true;
				
				public DeviationComparator(int width, int height, boolean pixelFrist) {
					mWidth = width;
					mHeight = height;
					mPixelFrist = pixelFrist;
					
					mPixelCount = mWidth * mHeight;
					mAspectRatio = (float)mWidth / (float)mHeight;
				}
				
				public int compare(Camera.Size first, Camera.Size second) {
					int firstPixelCount = first.width * first.height;
					float firstAspectRatio = (float)first.width / (float)first.height;
	
					int secondPixelCount = second.width * second.height;
					float secondAspectRatio = (float)second.width / (float)second.height;
	
					int firstPixelCountDelta = firstPixelCount - mPixelCount;
					if (firstPixelCountDelta < 0)
						firstPixelCountDelta = -firstPixelCountDelta;
									
					float firstAspectRatioDelta = firstAspectRatio - mAspectRatio;
					if (firstAspectRatioDelta < 0)
						firstAspectRatioDelta = -firstAspectRatioDelta;
					
					int secondPixelCountDelta = secondPixelCount - mPixelCount;
					if (secondPixelCountDelta < 0)
						secondPixelCountDelta = -secondPixelCountDelta;
					
					float secondAspectRatioDelta = secondAspectRatio - mAspectRatio;
					if (secondAspectRatioDelta < 0)
						secondAspectRatioDelta = -secondAspectRatioDelta;
					
					int pixelCountResult = firstPixelCountDelta - secondPixelCountDelta;
					int aspectRatioResult = 0;
	
					float aspectRatioResultF = firstAspectRatioDelta - secondAspectRatioDelta;
					if (aspectRatioResultF != 0f)
						aspectRatioResult = (aspectRatioResultF > 0f ? 1 : -1);
									
					int result = 0;
					if (mPixelFrist)
						result = (pixelCountResult != 0 ? pixelCountResult : aspectRatioResult);
					else
						result = (aspectRatioResult != 0 ? aspectRatioResult : pixelCountResult);
					
					return result;
				}
			};
			
			Collections.sort(sizes, new DeviationComparator(requestSize[0], requestSize[1], pixelFrist));
			
			bestSize = sizes.get(0);
		}
		
		int[] previewSize = new int[2];
		previewSize[0] = bestSize.width;
		previewSize[1] = bestSize.height;
		
		return previewSize;
	}
	
	public static void setDisplayOrientation(Camera camera, int orientation) {
		if (!isSetDisplayOrientationSupported())
			return;
		
		try {
			camera.getClass().getDeclaredMethod("setDisplayOrientation", int.class).invoke(camera, orientation);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}	
	}
	
	public static boolean isSetDisplayOrientationSupported() {
		boolean supported = false;
		try {
			Camera.class.getDeclaredMethod("setDisplayOrientation", int.class);
			supported = true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}	
		
		return supported;
	}
	
	public static boolean isZoomSupported() {
		boolean supported = false;
		try {
			Camera.Parameters.class.getDeclaredMethod("isZoomSupported");
			supported = true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}	
		
		return supported;
	}
	
	public static boolean isZoomSupported(Camera.Parameters param) {
		try {
			return (Boolean)(Camera.Parameters.class.getDeclaredMethod("isZoomSupported").invoke(param));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public static int getZoom(Camera.Parameters params) {
		try {
			return (Integer)(Camera.Parameters.class.getDeclaredMethod("getZoom").invoke(params));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	public static int getMaxZoom(Camera.Parameters params) {
		try {
			return (Integer)(Camera.Parameters.class.getDeclaredMethod("getMaxZoom").invoke(params));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	public static void setZoom(Camera.Parameters params, int value) {
		try {
			Camera.Parameters.class.getDeclaredMethod("setZoom", int.class).invoke(params, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static void setRotation(Camera.Parameters params, int rotation) {
		try {
			Camera.Parameters.class.getDeclaredMethod("setRotation", int.class).invoke(params, rotation);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static void lock(Camera camera) {
		try {
			Camera.class.getDeclaredMethod("lock").invoke(camera);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static void unlock(Camera camera) {
		try {
			Camera.class.getDeclaredMethod("unlock").invoke(camera);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static void reconnect(Camera camera) {
		try {
			Camera.class.getDeclaredMethod("reconnect").invoke(camera);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static int[] adjustPreviewSize(int size[], int previewOrientation) {
		int size2[] = new int[2];
		if (previewOrientation % 180 != 0 && isSetDisplayOrientationSupported()) {
			size2[0] = size[1];
			size2[1] = size[0];
		} else {
			size2[0] = size[0];
			size2[1] = size[1];
		}
		
		return size2;
	}
	
	public static int[] fitInBound(int requestSize[], int boundSize[]) {
		int requestWidth = requestSize[0];
		int requestHeight = requestSize[1];
		int boundWidth = boundSize[0];
		int boundHeight = boundSize[1];
		
		float width = (float)boundWidth;
		float height = (float)boundHeight;
	
		float requestRatio = (float)requestWidth / (float)requestHeight;
		float boundRatio = (float)boundWidth / (float)boundHeight;
		float xScale = (float)boundWidth / (float)requestWidth;
		float yScale = (float)boundHeight / (float)requestHeight;
			
		float Scale = 1;
		if (boundRatio < requestRatio)
			Scale = xScale;
		else
			Scale = yScale;
	
		width = (float)requestWidth * Scale;
		height = (float)requestHeight * Scale;
		
		int[] size = new int[2];
		size[0] = (int)width;
		size[1] = (int)height;
		return size;
	}
	
	public static boolean isNaturePortrait(Context context) {
		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();	
		int orientation = display.getOrientation() * 90;
		int width = display.getWidth();
		int height = display.getHeight();
		boolean portrait = height > width;
		return (orientation % 180 == 0 ? portrait : !portrait);
	}

    public static int getExifOrientation(String path) {
        int degree = 0;
    	int orientation = -1;
    	try {
			Class ExifInterfaceClass = Class.forName("android.media.ExifInterface");
			Object ExifInterfaceObject = ExifInterfaceClass.getDeclaredConstructor(String.class).newInstance(path);
			orientation = (Integer)(ExifInterfaceClass.getDeclaredMethod("getAttributeInt", String.class, int.class).invoke(ExifInterfaceObject, "Orientation", -1));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		if (orientation != -1) {
            switch(orientation) {
            case 6:
                degree = 90;
                break;
            case 3:
                degree = 180;
                break;
            case 8:
                degree = 270;
                break;
            }
		}
		
		return degree;
    }
}
