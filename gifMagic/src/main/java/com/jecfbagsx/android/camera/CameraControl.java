package com.jecfbagsx.android.camera;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.graphics.Matrix;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;

public class CameraControl {
	private static final String TAG = CameraControl.class.getSimpleName();
	private static final String SHARED_PREFERENCES_NAME = CameraControl.class.getSimpleName();
	private static final String PREF_CAMERA_ID = "CAMERA_ID";
	private static final String PREF_MODE = "MODE";
	private static final String PREF_ORIENTATION_COMPENSATION = "ORIENTATION_COMPENSATION";
	private static final int DEFAULT_CAMERA_ID = 0;
	private static final Mode DEFAULT_MODE = Mode.PREVIEW;
	
	public enum SizeType {
		SCREEN, MAX_PREVIEW_DISPLAY, PREVIEW_DISPLAY,
	};
	
	public enum State {
		CLOSED, OPENED, PREVIEWING, CAPTURING
	}
	
	public enum Capability {
		ZOOM,
	}
	
	public enum Mode {
		PREVIEW, PICTURE, VIDEO,
	};
	
	public interface ControlCallback {
		public Context getContext();
		public SizeType getPreviewSizeType();
		public float getPreviewSizeScale();	
		public int[] getSize(SizeType sizeType);
		public void setSize(SizeType sizeType, int size[]);
		public SurfaceHolder getPreviewSurfaceHolder();
		public boolean receivePreviewData(CaptureData data);
		public boolean receivePictureData(CaptureData data);
		public void onStateChanged(State previousState, State currentState);
		public void onCapability(Capability capability, Object value);
	};
	
	private ControlCallback mControlCallback;
	
	public CameraControl(ControlCallback callback) {
		mControlCallback = callback;
	}
	
	private State mState = State.CLOSED;
	private Camera mCamera;
	private int mDeviceOrientation;
	private int mScreenOrientation;
	private int mDisplayOrientation;
	private Matrix mDisplayMatrix = new Matrix();
	private Matrix mDeviceMatrix = new Matrix();
	private int[] mPreviewSize = new int[2];
	private int mPreviewFormat;
	private int[] mPictureSize = new int[2];
	private int mPictureFormat;
	
	public void setOrientation(boolean screen, int orientation) {
		if (screen)
			mScreenOrientation = orientation;
		else
			mDeviceOrientation = orientation;
		
		Log.i(TAG, "setOrientation " + (screen ? "screen" : "device") + " " + orientation);

		if (!screen)
			updateDeviceMatrix();
	}

	public void setCaptureVideoFile(String path) {
		mCaptureVideoFile = path;
	}
	
	public String getLastCaptureVideoFile() {
		return mLastCaptureVideoFile;
	}
	
	public Mode getMode() {
		return readMode();
	}
	
	public void openCamera() {		
		if (mState != State.CLOSED)
			return;
		
		int cameraId = readCameraId();

		Log.i(TAG, "open camera, id " + cameraId);

		mCamera = CameraHelper.openCamera(cameraId);
		
		if (mCamera == null)
			return;
		
		reportCapabilities();
		
		mDisplayOrientation = CameraHelper.getCameraDisplayOrientation(cameraId, mScreenOrientation);
		mDisplayMatrix.set(CameraHelper.getCameraDisplayMatrix(cameraId, mScreenOrientation));

		Log.i(TAG, "display orientation " + mDisplayOrientation);
		Log.i(TAG, "display matrix " + mDisplayMatrix);

		prepareCamera();
		onCameraPrepared();
		
		Log.i(TAG, "camera opened");

		mState = State.OPENED;
		
		reportStateChange(State.CLOSED, State.OPENED);
	}

	public void closeCamera() {	
		if (mState != State.OPENED)
			return;
		
		Log.i(TAG, "close camera");

		if (mCamera == null)
			return;
		
		mCamera.release();
		mCamera = null;
		
		Log.i(TAG, "camera closed");

		mState = State.CLOSED;
		
		reportStateChange(State.OPENED, State.CLOSED);
	}
	
	public void changeMode(Mode mode) {
		if (mState != State.CLOSED)
			return;
		
		if (readMode() != mode)
			writeMode(mode);
	}
	
	public void shiftCamera() {
		if (mState != State.CLOSED)
			return;
		
		int cameraId = readCameraId();
		cameraId = (cameraId + 1) % CameraHelper.getNumberOfCameras();
		writeCameraId(cameraId);
	}
	
	public void rotateCamera(boolean clockwise) {
		if (mState != State.CLOSED)
			return;
	
		increaseDisplayCompensation(clockwise ? 90 : 270);
	}
	
	public void zoomCamera(boolean in) {
		if (mState == State.CLOSED)
			return;
		
		Camera.Parameters params = mCamera.getParameters();
	
		int zoom = CameraHelper.getZoom(params);
		int max = CameraHelper.getMaxZoom(params);
		
		if (in && zoom == max || !in && zoom == 0)
			return;
		
		if (in)
			zoom++;
		else
			zoom--;
		
		CameraHelper.setZoom(params, zoom);
		
		try {
			mCamera.setParameters(params);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void startPreview() {			
		if (mState != State.OPENED)
			return;
		
		Log.i(TAG, "start preview");
		
		if (mCamera == null)
			return;

		try {
			mCamera.setPreviewDisplay(mControlCallback.getPreviewSurfaceHolder());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		int compensatedDisplayOrientation = getCompensatedDisplayOrientation();
		int maxPreviewDisplaySize[] = mControlCallback.getSize(SizeType.MAX_PREVIEW_DISPLAY);
		int adjustedPreviewSize[] = CameraHelper.adjustPreviewSize(mPreviewSize, compensatedDisplayOrientation);
		int previewDisplaySize[] = CameraHelper.fitInBound(adjustedPreviewSize, maxPreviewDisplaySize);

		Log.i(TAG, "compensated display orientation " + compensatedDisplayOrientation);
		Log.i(TAG, "max preview display size " + maxPreviewDisplaySize[0] + " " + maxPreviewDisplaySize[1]);
		Log.i(TAG, "adjusted preview size " + adjustedPreviewSize[0] + " " + adjustedPreviewSize[1]);
		Log.i(TAG, "preview display size " + previewDisplaySize[0] + " " + previewDisplaySize[1]);
		
		mControlCallback.setSize(SizeType.PREVIEW_DISPLAY, previewDisplaySize);
		
		mCamera.startPreview();
			
		Log.i(TAG, "preview started");

		mState = State.PREVIEWING;
		
		reportStateChange(State.OPENED, State.PREVIEWING);
	}

	public void stopPreview() {
		if (mState != State.PREVIEWING)
			return;
		
		Log.i(TAG, "stop preview");

		if (mCamera == null)
			return;

		mCamera.stopPreview();

		Log.i(TAG, "preview stopped");

		mState = State.OPENED;		
		
		reportStateChange(State.PREVIEWING, State.OPENED);
	}
	
	public void restartPreview() {
		stopPreview();
		startPreview();
	}
	
	public void startCapture() {
		if (mState != State.PREVIEWING)
			return;
		
		Log.i(TAG, "start capture");
		
		if (mCamera == null)
			return;
		
		switch (readMode()) {
			case PREVIEW:
				capturePreview(true);
				break;
			
			case PICTURE:
				capturePicture(true);
				break;
			
			case VIDEO:
				captureVideo(true);
				break;
		
			default:;
		}
		
		Log.i(TAG, "capture started");
		
		mState = State.CAPTURING;
		
		reportStateChange(State.PREVIEWING, State.CAPTURING);
	}

	public void stopCapture() {
		if (mState != State.CAPTURING)
			return;
		
		Log.i(TAG, "stop capture");
		
		if (mCamera == null)
			return;
		
		switch (readMode()) {
			case PREVIEW:
				capturePreview(false);
				break;
			
			case PICTURE:
				capturePicture(false);
				break;
			
			case VIDEO:
				captureVideo(false);
				break;
	
			default:;
		}
		
		Log.i(TAG, "capture stopped");
		
		mState = State.PREVIEWING;
		
		reportStateChange(State.CAPTURING, State.PREVIEWING);
	}
	
	public boolean isCapturing() {
		return (mState == State.CAPTURING);
	}
	
	private void capturePreview(boolean start) {
		if (start) {
			updateDeviceMatrix();

			mCamera.setPreviewCallback(mPreviewCallback);
		}
		else {
			mCamera.setPreviewCallback(null);
		}
	}
	
	private void capturePicture(boolean start) {
		if (start)
			startFocus();
		else
			mCamera.startPreview();
	}
	
	String mCaptureVideoFile;
	String mLastCaptureVideoFile;
	MediaRecorder mMediaRecorder;
	
	private void captureVideo(boolean start) {
		if (start) {
			prepareForCaptureVideo(true);
			
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setCamera(mCamera);
			
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
			mMediaRecorder.setOutputFile(mCaptureVideoFile);
			mMediaRecorder.setPreviewDisplay(mControlCallback.getPreviewSurfaceHolder().getSurface());
			mMediaRecorder.setMaxDuration(0);
			mMediaRecorder.setMaxFileSize(0);
			
			int rotation = getCompensatedVideoOrientation();
			try {
				mMediaRecorder.getClass().getDeclaredMethod("setOrientationHint", int.class).invoke(mMediaRecorder, rotation);
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			}
			
			try {
				mMediaRecorder.prepare();
				mMediaRecorder.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		else {
			mMediaRecorder.stop();
			mMediaRecorder.reset();
			mMediaRecorder.release();
			mMediaRecorder = null;
			
			prepareForCaptureVideo(false);
			
			updateLastCaptureVideoFile();
		}
	}

	private void startFocus() {
		Log.i(TAG, "start focus");
		
		mCamera.autoFocus(mAutoFocusCallback);
	}
	
	private void onFocusFinished(boolean success) {
		Log.i(TAG, "focus finished");

		startSnapshot();
	}
	
	private void startSnapshot() {
		Log.i(TAG, "start snapshot");
		
		prepareForCapturePicture();
		
		mCamera.takePicture(null, null, mPictureCallback);
	}
	
	private void onSnapshotFinished() {
		Log.i(TAG, "snapshot finished");

		stopCapture();
	}
	
	private void prepareCamera() {
		int compensatedDisplayOrientation = getCompensatedDisplayOrientation();
		int previewStandardSize[] = getPreviewStandardSize();
		int adjustedPreviewStandardSize[] = CameraHelper.adjustPreviewSize(previewStandardSize, compensatedDisplayOrientation);
		int previewSize[] = CameraHelper.chooseBestPreviewSize(mCamera, adjustedPreviewStandardSize, true);

		Log.i(TAG, "compensated display orientation " + compensatedDisplayOrientation);
		Log.i(TAG, "preview standard size " + previewStandardSize[0] + " " + previewStandardSize[1]);
		Log.i(TAG, "adjusted preview standard size " + adjustedPreviewStandardSize[0] + " " + adjustedPreviewStandardSize[1]);
		Log.i(TAG, "preview size " + previewSize[0] + " " + previewSize[1]);

		Camera.Parameters parameters = mCamera.getParameters();

		if (readMode() == Mode.PREVIEW) {		
			parameters.setPreviewSize(previewSize[0], previewSize[1]);
		}
		
		try {
			mCamera.setParameters(parameters);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	
		CameraHelper.setDisplayOrientation(mCamera, compensatedDisplayOrientation);
	}
	
	private void onCameraPrepared() {
		Camera.Parameters parameters = mCamera.getParameters();
		mPreviewSize[0] = parameters.getPreviewSize().width;
		mPreviewSize[1] = parameters.getPreviewSize().height;
		mPreviewFormat = parameters.getPreviewFormat();
		mPictureSize[0] = parameters.getPictureSize().width;
		mPictureSize[1] = parameters.getPictureSize().height;
		mPictureFormat = parameters.getPictureFormat();
	
		Log.i(TAG, "accepted preview size " + mPreviewSize[0] + " " + mPreviewSize[1]);
		Log.i(TAG, "accepted preview format " + mPreviewFormat);
		Log.i(TAG, "accepted picture size " + mPictureSize[0] + " " + mPictureSize[1]);
		Log.i(TAG, "accepted picture format " + mPictureFormat);
	}
	
	private void prepareForCapturePicture() {
		int rotation = getCompensatedPictureOrientation();

		Camera.Parameters params = mCamera.getParameters();
		CameraHelper.setRotation(params, rotation);
	
		try {
			mCamera.setParameters(params);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	private void prepareForCaptureVideo(boolean start) {
		if (start)
			CameraHelper.unlock(mCamera);
		else
			CameraHelper.lock(mCamera);		
	}
	
	private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			if (data == null)
				return;
			
			CaptureData previewData = new CaptureData();
			previewData.data = data;
			previewData.width = mPreviewSize[0];
			previewData.height = mPreviewSize[1];
			previewData.format = mPreviewFormat;
			previewData.matrix = getCompensatedDisplayMatrix();
				
			mControlCallback.receivePreviewData(previewData);
		}
	};
	
	private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			onSnapshotFinished();
			
			if (data == null)
				return;
			
			CaptureData pictureData = new CaptureData();
			pictureData.data = data;
			pictureData.width = mPictureSize[0];
			pictureData.height = mPictureSize[1];
			pictureData.format = mPictureFormat;
			pictureData.matrix = new Matrix();
			
			mControlCallback.receivePictureData(pictureData);
		}
	}; 
	
	private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			onFocusFinished(success);
		}
	};
	
	private int getCompensatedDisplayOrientation() {
		return (mDisplayOrientation + getOrientationCompensation()) % 360;
	}
	
	private int getCompensatedPictureOrientation() {
		return (CameraHelper.getCameraPictureOrientation(readCameraId(), mDeviceOrientation) + getOrientationCompensation()) % 360;
	}
	
	private int getCompensatedVideoOrientation() {
		return (CameraHelper.getCameraPictureOrientation(readCameraId(), mDeviceOrientation) + getOrientationCompensation()) % 360;
	}
	
	private Matrix getCompensatedDisplayMatrix() {
		Matrix matrix = new Matrix(mDisplayMatrix);
		matrix.postRotate(getOrientationCompensation());
		
		return matrix;
	}
	
	private void increaseDisplayCompensation(int degree) {
		int compensation = readOrientationCompensation();
		compensation = (compensation + degree) % 360;
		writeOrientationCompensation(compensation);
	}
	
	private int getOrientationCompensation() {
		return readOrientationCompensation();
	}
	
	private int[] getPreviewStandardSize() {
		SizeType sizeType = mControlCallback.getPreviewSizeType();
		int[] size = mControlCallback.getSize(sizeType);
		float scale = mControlCallback.getPreviewSizeScale();
		size[0] = (int)(size[0] * scale);
		size[1] = (int)(size[1] * scale);
		return size;
	}
	
	private void updateLastCaptureVideoFile() {
		mLastCaptureVideoFile = mCaptureVideoFile;
	}
	
	private void reportCapabilities() {
		reportCapability(Capability.ZOOM, CameraHelper.isZoomSupported(mCamera.getParameters()));
	}
	
	private void updateDeviceMatrix() {
		if (mState != State.CAPTURING)
			return;

		mDeviceMatrix.set(mDisplayMatrix);
		mDeviceMatrix.postRotate(mScreenOrientation + mDeviceOrientation);
	}

	private int readOrientationCompensation() {
		return getSharedPreferences().getInt(getOrientationCompensationKey(), 0);
	}
	
	private void writeOrientationCompensation(int compensation) {
		getSharedPreferences().edit().putInt(getOrientationCompensationKey(), compensation).commit();
	}
	
	private String getOrientationCompensationKey() {
		return PREF_ORIENTATION_COMPENSATION + "#" + Integer.toString(readCameraId());
	}
	
	private Mode readMode() {
		return Mode.values()[getSharedPreferences().getInt(PREF_MODE, DEFAULT_MODE.ordinal())];
	}
	
	private void writeMode(Mode mode) {
		getSharedPreferences().edit().putInt(PREF_MODE, mode.ordinal()).commit();
	}
	
	private int readCameraId() {
		return getSharedPreferences().getInt(PREF_CAMERA_ID, DEFAULT_CAMERA_ID);
	}
	
	private void writeCameraId(int cameraId) {
		getSharedPreferences().edit().putInt(PREF_CAMERA_ID, cameraId).commit();
	}
	
	private SharedPreferences getSharedPreferences() {
		return mControlCallback.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
	}
	
	private void reportStateChange(State previousState, State currentState) {
		mControlCallback.onStateChanged(previousState, currentState);
	}
	
	private void reportCapability(Capability capability, Object value) {
		mControlCallback.onCapability(capability, value);
	}
}
