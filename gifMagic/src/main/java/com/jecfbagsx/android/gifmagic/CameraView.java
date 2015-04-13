package com.jecfbagsx.android.gifmagic;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.jecfbagsx.android.camera.*;
import com.jecfbagsx.android.camera.CameraControl.Capability;
import com.jecfbagsx.android.camera.CameraControl.Mode;
import com.jecfbagsx.android.camera.CameraControl.SizeType;
import com.jecfbagsx.android.camera.CameraControl.State;

import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.SettingsHelper;
import com.jecfbagsx.android.utils.FileHelper;

public class CameraView extends Activity implements SurfaceHolder.Callback,
		OnClickListener {
	public static final String STORAGE_ROOT_PATH = "STORAGE_ROOT_PATH";
	public static final String STORAGE_PATH = "STORAGE_PATH";
	public static final String PREVIEW_STORAGE_PATH = "PREVIEW_STORAGE_PATH";
	public static final String PICTURE_COUNT = "PICTURE_COUNT";
	public static final String CAPTURE_SERIAL = "CAPTURE_SERIAL";
	public static final String CAPTURE_COUNT = "CAPTURE_COUNT";
	public static final String CAPTURE_SPEED = "CAPTURE_SPEED";
	public static final int DEFAULT_CAPTURE_COUNT = 15;
	public static final int DEFAULT_CAPTURE_SPEED = 3;
	private static final String TAG = CameraView.class.getSimpleName();
	private static final String SHARED_PREFERENCES_NAME = CameraView.class.getSimpleName();
	private static final String PREF_FIRST_LAUNCH = "FIRST_LAUNCH";
	private static final int DIALOG_HELP = 1231;
	
	private enum Action {
		CAPTURE, PAUSE, STOP,
	};
	
	CaptureManager mPreviewCaptureManager = new CaptureManager(
			new CaptureManager.Notification() {
				public void reportState(int capturing, int storing, int finished) {
					reportPreviewJobsState(capturing, storing, finished);
				}

				public void reportFinish(String path, boolean capture) {
					if (path != null)
						return;
					
					if (capture)
						finishCapture();
					else
						doFinish();
				}
			});
	
	CaptureManager mPictureCaptureManager = new CaptureManager(
			new CaptureManager.Notification() {
				public void reportState(int capturing, int storing, int finished) {
				}

				public void reportFinish(String path, boolean capture) {
					if (path == null)
						return;
					
					if (!capture) {
						incPictureCount();
						updatePictureCount();
						updateThumbnail(path);
					}
				}
			});

	TimeRecorder mVideoTimeRecorder = new TimeRecorder(new TimeRecorder.Notification() {
		public void reportEvent(TimeRecorder.Event event, String text) {
			switch (event) {
			case START:
			case STOP:
				reportVideoCaptureState(null);
				break;
				
			case RECORD:
				reportVideoCaptureState(text);
				break;
			}
		}
	});
	
	CameraControl mCameraControl = new CameraControl(
			new CameraControl.ControlCallback() {
				public Context getContext() {
					return getApplicationContext();
				}
				
				public SizeType getPreviewSizeType() {
					return SizeType.SCREEN;
				}

				public float getPreviewSizeScale() {
					return 0.5f;
				}
				
				public int[] getSize(SizeType sizeType) {
					int size[] = new int[2];

					switch (sizeType) {
						case SCREEN: {
							size[0] = getWindowManager().getDefaultDisplay().getWidth();
							size[1] = getWindowManager().getDefaultDisplay().getHeight();
							break;
						}
	
						case MAX_PREVIEW_DISPLAY: {
							View view = findViewById(R.id.camera_preview_holder);
							size[0] = view.getWidth();
							size[1] = view.getHeight();
							break;
						}
						
						case PREVIEW_DISPLAY: {
							View view = findViewById(R.id.camera_preview);
							size[0] = view.getWidth();
							size[1] = view.getHeight();
							break;
						}
							
						default:;
					}

					return size;
				}
				
				public void setSize(SizeType sizeType, int size[]) {
					switch (sizeType) {
						case PREVIEW_DISPLAY : {
							View cameraPreview = findViewById(R.id.camera_preview);
							ViewGroup.LayoutParams layoutParams = cameraPreview.getLayoutParams();
							layoutParams.width = size[0];
							layoutParams.height = size[1];
							cameraPreview.setLayoutParams(layoutParams);
							cameraPreview.requestLayout();
							
							break;
						}
					}
				}

				public SurfaceHolder getPreviewSurfaceHolder() {
					return ((SurfaceView) findViewById(R.id.camera_preview)).getHolder();
				}

				public boolean receivePreviewData(CaptureData previewData) {
					return mPreviewCaptureManager.receiveData(previewData);
				}
				
				public boolean receivePictureData(CaptureData pictureData) {
					return mPictureCaptureManager.receiveData(pictureData);
				}
				
				public void onStateChanged(State previousState, State currentState) {
					if (previousState == CameraControl.State.OPENED && currentState == CameraControl.State.PREVIEWING)
						keepScreenOn(true);
					
					if (previousState == CameraControl.State.PREVIEWING && currentState == CameraControl.State.OPENED)
						keepScreenOn(false);
					
					if (previousState == CameraControl.State.PREVIEWING && currentState == CameraControl.State.CAPTURING) {
						if (getMode() == CameraControl.Mode.VIDEO)
							mVideoTimeRecorder.start();
					}
					
					if (previousState == CameraControl.State.CAPTURING && currentState == CameraControl.State.PREVIEWING) {
						if (getMode() == CameraControl.Mode.VIDEO) {
							mVideoTimeRecorder.stop();
							doFinish();
						}
					}
				}
				
				public void onCapability(Capability capability, Object value) {
					switch (capability) {
					case ZOOM: {
						boolean isZoomSupported = (Boolean)value;
						showView(R.id.zoom_in, isZoomSupported);
						showView(R.id.zoom_out, isZoomSupported);

						break;
					}
						
					default:;
					}
				}
			});
	
	OrientationIndicator mOrientationIndicator;	
	private void initOrientationIndicator() {
		mOrientationIndicator = new OrientationIndicator(CameraView.this,
				new OrientationIndicator.Notification() {
					public void reportOrientationChange(boolean compensation, int previous, int current) {						
						onOrientationChanged(compensation, previous, current);
					}
				}, 
				new OrientationIndicator.Callback() {
					public int getStardardOrientation() {
						return getScreenOrientation();
					}
				});
	}
	
	private void initOnCreate() {
		initOrientationIndicator();
	}
	
	public Dialog onCreateDialog(int id) {
		if (id == DIALOG_HELP) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.camera_view_title);
			builder.setMessage(R.string.camera_view_help);
			builder.setPositiveButton(R.string.ok, null);
			return builder.create();
		}
		
		return null;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initOnCreate();

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFormat(PixelFormat.TRANSPARENT);

		setContentView(R.layout.camera_view);
		View captureView = findViewById(R.id.capture);
		View pauseView = findViewById(R.id.pause);
		View stopView = findViewById(R.id.stop);
		View shiftView = findViewById(R.id.shift);
		View setupView = findViewById(R.id.setup);
		View browseView = findViewById(R.id.browse);
		View rotateView = findViewById(R.id.rotate);
		View zoomInView = findViewById(R.id.zoom_in);
		View zoomOutView = findViewById(R.id.zoom_out);
		View modeMultiView = findViewById(R.id.mode_multi);
		View modeSingleView = findViewById(R.id.mode_single);
		View modeVideoView = findViewById(R.id.mode_video);	
		View thumbnailView = findViewById(R.id.thumbnail);
		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.camera_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();

		try {
			surfaceView.getClass().getDeclaredMethod("setZOrderOnTop", boolean.class).invoke(surfaceView, false);
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
		
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		surfaceHolder.addCallback(this);
		captureView.setOnClickListener(this);
		pauseView.setOnClickListener(this);
		stopView.setOnClickListener(this);
		setupView.setOnClickListener(this);
		browseView.setOnClickListener(this);
		shiftView.setOnClickListener(this);
		rotateView.setOnClickListener(this);
		zoomInView.setOnClickListener(this);
		zoomOutView.setOnClickListener(this);
		modeMultiView.setOnClickListener(this);
		modeSingleView.setOnClickListener(this);
		modeVideoView.setOnClickListener(this);
		thumbnailView.setOnClickListener(this);
		surfaceView.setOnClickListener(this);
		
		showView(R.id.shift, CameraHelper.isCameraChangeSupported());
		showView(R.id.rotate, CameraHelper.isSetDisplayOrientationSupported());
		showView(R.id.zoom_in, CameraHelper.isZoomSupported());
		showView(R.id.zoom_out, CameraHelper.isZoomSupported());
		showView(R.id.camera_view_control, false);
		
		CameraHelper.calibrateCameraInfos(CameraView.this);
		
		mCameraControl.setOrientation(true, getScreenOrientation());
		mCameraControl.setOrientation(false, getScreenOrientation());
		showMode(getMode());
		
		mPreviewCaptureManager.startStore();
		mPictureCaptureManager.startStore();
		
		if (isFirstLaunch())
			doHelp();
	}

	protected void onDestroy() {		
		mPreviewCaptureManager.stopStore();
		mPictureCaptureManager.stopStore();

		super.onDestroy();
	}
	
	protected void onResume() {
		super.onResume();
		mOrientationIndicator.enable();
	
		showAction(Action.CAPTURE);
		mPreviewCaptureManager.init();
		mPictureCaptureManager.init();
		
		mCameraControl.openCamera();
		if (isPreviewSurfaceCreated())
			mCameraControl.startPreview();
	}
	
	protected void onPause() {
		mPreviewCaptureManager.abort();
		mPictureCaptureManager.abort();
		
		mCameraControl.stopCapture();
		mCameraControl.stopPreview();
		mCameraControl.closeCamera();
	
		mOrientationIndicator.disable();
		super.onPause();
	}
	
	private boolean mPreviewSurfaceCreated = false;
	private boolean isPreviewSurfaceCreated() {
		return mPreviewSurfaceCreated;
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		mPreviewSurfaceCreated = true;
		
		mCameraControl.startPreview();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//		mCameraControl.restartPreview();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		mPreviewSurfaceCreated = false;
		
		mCameraControl.stopPreview();
	}
	
	private void shiftCamera() {
		mPreviewCaptureManager.abort();
		mPictureCaptureManager.abort();
		mCameraControl.stopCapture();
		mCameraControl.stopPreview();
		mCameraControl.closeCamera();
		mCameraControl.shiftCamera();
		mCameraControl.openCamera();
		mCameraControl.startPreview();
		mPreviewCaptureManager.init();
		mPictureCaptureManager.init();
		showAction(Action.CAPTURE);
	}
	
	private void rotateCamera(boolean clockwise) {
		mPreviewCaptureManager.abort();
		mPictureCaptureManager.abort();
		mCameraControl.stopCapture();
		mCameraControl.stopPreview();
		mCameraControl.closeCamera();
		mCameraControl.rotateCamera(clockwise);
		mCameraControl.openCamera();
		mCameraControl.startPreview();
		mPreviewCaptureManager.init();
		mPictureCaptureManager.init();
		showAction(Action.CAPTURE);
	}
	
	private void zoomCamera(boolean in) {
		mCameraControl.zoomCamera(in);
	}
	
	public Mode getMode() {
		return mCameraControl.getMode();
	}
	
	public String getLastCaptureVideoFile() {
		return mCameraControl.getLastCaptureVideoFile();
	}
	
	private void changeMode(CameraControl.Mode mode) {
		if (getMode() == mode)
			return;
		
		mPreviewCaptureManager.abort();
		mPictureCaptureManager.abort();
		mCameraControl.stopCapture();
		mCameraControl.stopPreview();
		mCameraControl.closeCamera();
		mCameraControl.changeMode(mode);
		mCameraControl.openCamera();
		mCameraControl.startPreview();
		mPreviewCaptureManager.init();
		mPictureCaptureManager.init();
		showAction(Action.CAPTURE);
	}
	
	private void captureControl(Action action) {
		switch (getMode()) {
		case PREVIEW: {
			if (action == Action.CAPTURE || action == Action.PAUSE) {			
				if (mPreviewCaptureManager.hasCapturingJobs()) {
					if (mCameraControl.isCapturing())
						mCameraControl.stopCapture();
					else
						mCameraControl.startCapture();
				}
				else {
					int captureCount = SettingsHelper.getCameraDuration();
					int captureSpeed = SettingsHelper.getCameraSpeed();
					String storagePath = queryStoragePath(CameraControl.Mode.PREVIEW);
			
					capturePreview(storagePath, captureCount, captureSpeed);
				}
			}
			
			break;
		}
			
		case PICTURE: {
			if (action == Action.CAPTURE) {			
				String storagePath = queryStoragePath(CameraControl.Mode.PICTURE);
	
				capturePicture(storagePath);
			}
			
			break;
		}

		case VIDEO: {
			if (action == Action.CAPTURE || action == Action.STOP) {			
				if (action == Action.CAPTURE) {
					String storagePath = queryStoragePath(CameraControl.Mode.VIDEO);

					captureVideo(storagePath);
				}
				else {
					mCameraControl.stopCapture();
				}
			}	
			
			break;
		}

		default:;
		}
	}
	
	private void capturePreview(String storagePath, int captureCount, int captureSpeed) {		
		new File(storagePath).mkdirs();

		String filename = "Preview" + Long.toString(System.currentTimeMillis());

		for (int i = 1; i <= captureCount; i++) {
			String path = storagePath + File.separator + filename + "_gifmagic_" + Integer.toString(i) + ".jpg";
			mPreviewCaptureManager.addJob(CaptureType.preview, path, captureSpeed, 100);
		}

		mCameraControl.startCapture();
	}
	
	private void capturePicture(String storagePath) {		
		new File(storagePath).mkdirs();

		String filename = "Picture" + Long.toString(System.currentTimeMillis());

		String path = storagePath + File.separator + filename + ".jpg";
		mPictureCaptureManager.addJob(CaptureType.picture, path, 1, 100);
	
		mCameraControl.startCapture();
	}
	
	private void captureVideo(String storagePath) {		
		new File(storagePath).mkdirs();

		String filename = "Video" + Long.toString(System.currentTimeMillis());

		String path = storagePath + File.separator + filename + ".3gp";

		mCameraControl.setCaptureVideoFile(path);
		mCameraControl.startCapture();
	}
	
	private void finishCapture() {
		showAction(Action.CAPTURE);
		mCameraControl.stopCapture();
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		saveStates(outState);
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		restoreStates(savedInstanceState);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.capture:
			doAction(Action.CAPTURE);
			break;
		case R.id.pause:
			doAction(Action.PAUSE);
			break;
		case R.id.stop:
			doAction(Action.STOP);
			break;
		case R.id.shift:
			doShift();
			break;
		case R.id.rotate:
			doRotate();
			break;
		case R.id.zoom_in:
			doZoom(true);
			break;
		case R.id.zoom_out:
			doZoom(false);
			break;
		case R.id.camera_preview:
			doControl();
			break;
		case R.id.mode_single:
			doMode(CameraControl.Mode.PICTURE);
			break;
		case R.id.mode_multi:
			doMode(CameraControl.Mode.PREVIEW);
			break;
		case R.id.mode_video:
			doMode(CameraControl.Mode.VIDEO);
			break;
		case R.id.thumbnail:
			doBrowsePicture();
			break;
		case R.id.setup:
			doSetup();
			break;
		case R.id.browse:
			doFinish();
			break;
		default:;
		}
	}
	
	private void doAction(Action action) {
		controlAction(action);
		
		captureControl(action);
	}
	
	private void doMode(CameraControl.Mode mode) {
		controlMode(mode);
		
		changeMode(mode);
	}
	
	private void doShift() {
		shiftCamera();
	}
	
	private void doRotate() {
		rotateCamera(true);
	}
	
	private void doZoom(boolean in) {
		zoomCamera(in);
	}
	
	private void doControl() {
		toggleView(R.id.camera_view_control);
	}
	
	private void doFinish() {
		finish();

		switch (getMode()) {
		case PICTURE:
			doBrowsePicture();
			break;
			
		case PREVIEW:
			doBrowsePreview();
			break;
			
		case VIDEO:
			doBrowseVideo();
			break;
		}
	}
	
	private void doSetup() {
		startActivity(new Intent(ActivityActions.ACTION_SETTING));
	}

	private void doBrowseHistory() {
		Intent intent = new Intent(ActivityActions.ACTION_HISTORY);
        intent.putExtra(ActivityActions.EXTRA_HISTORY_SOUCEPATH, FileHelper.getAppHistoryPath());
        startActivity(intent);
	}
	
	private void doBrowsePreview() {
		Intent intent = new Intent();
		intent.setAction(ActivityActions.ACTION_MULTIPLE_SELECTION);
		intent.putExtra(ActivityActions.EXTRA_MULTIPLE_SELECTION_SOUCEPATH, queryStoragePath(CameraControl.Mode.PREVIEW));
		intent.putExtra(ActivityActions.EXTRA_MULTIPLE_SELECTION_ISFROMCAMERA, true);
		startActivity(intent);
	}
	
	private void doBrowsePicture() {
		Intent intent = new Intent();
		intent.setAction(ActivityActions.ACTION_MULTIPLE_SELECTION);
		intent.putExtra(ActivityActions.EXTRA_MULTIPLE_SELECTION_SOUCEPATH, queryStoragePath(CameraControl.Mode.PICTURE));
		intent.putExtra(ActivityActions.EXTRA_MULTIPLE_SELECTION_ISFROMCAMERA, true);
		startActivity(intent);
	}
	
	private void doBrowseVideo() {
		String path = getLastCaptureVideoFile();
		if (path == null)
			return;
		
		Intent intent = new Intent(ActivityActions.ACTION_VIDEO_CAPTURE);
		intent.putExtra(ActivityActions.EXTRA_VIDEO_CAPTURE_FILE, path);
		intent.putExtra(ActivityActions.EXTRA_PREVIEW_CAPTURE_STORAGE_PATH, FileHelper.getNextAppTempPath());
		startActivity(intent);
	}
	
	private void doHelp() {
		showDialog(DIALOG_HELP);
	}

	private void reportPreviewJobsState(int capturing, int storing, int finished) {
		int captureProgress = 0;
		int storeProgress = 0;
		int finishProgress = 0;

		int totalStore = storing + finished;
		int total = capturing + totalStore;

		if (total != 0)
			captureProgress = (totalStore) * 100 / total;

		if (totalStore != 0)
			storeProgress = (finished) * 100 / totalStore;

		if (total != 0)
			finishProgress = (finished) * 100 / total;

		String state = "c:" + Integer.toString(capturing) + "/"	+ "s:" + Integer.toString(storing) + "/" + "f:"	+ Integer.toString(finished);
		String progress = "c:" + Integer.toString(captureProgress) + "/" + "s:" + Integer.toString(storeProgress) + "/" + "f:" + Integer.toString(finishProgress);

		Log.i(TAG, "report state " + state);
		Log.i(TAG, "report progress " + progress);

		((ProgressBar)findViewById(R.id.capture_progress)).setProgress(captureProgress);
		findViewById(R.id.store_progress).setVisibility((capturing == 0 && storing != 0) ? View.VISIBLE : View.INVISIBLE);
	}
	
	private void reportVideoCaptureState(String text) {	
		((TextView)findViewById(R.id.video_time)).setText(text);
	}
	
	private void updatePictureCount() {
		((TextView)findViewById(R.id.picture_count)).setText(Integer.toString(queryPictureCount()));
	}
	
	private void updateThumbnail(String path) {
		int degree = CameraHelper.getExifOrientation(path);
		Bitmap bitmap = FileHelper.getRefinedBitmap(path, -1, 64 * 64);
		((ImageView)findViewById(R.id.thumbnail)).setImageBitmap(bitmap);
	}

	private void controlAction(Action action) {
		CameraControl.Mode mode = getMode();
		
		switch (action) {
		case CAPTURE: {
			switch (mode) {
			case PREVIEW: {
				showAction(Action.PAUSE);
				break;
			}

			case VIDEO: {
				showAction(Action.STOP);
				break;
			}

			default:;
			}

			break;
		}

		case PAUSE: {
			switch (mode) {
			case PREVIEW: {
				showAction(Action.CAPTURE);
				break;
			}

			default:;
			}

			break;
		}

		case STOP: {
			switch (mode) {
			case VIDEO: {
				showAction(Action.CAPTURE);
				break;
			}

			default:;
			}
			
			break;
		}

		default:;
		}
	}
	
	private void controlMode(CameraControl.Mode mode) {
		showMode(mode);
	}
	
	private void showAction(Action action) {	
		showView(R.id.capture, false);
		showView(R.id.pause, false);
		showView(R.id.stop, false);

		switch (action) {
		case CAPTURE:
			showView(R.id.capture, true);
			break;
		
		case PAUSE:
			showView(R.id.pause, true);
			break;

		case STOP:
			showView(R.id.stop, true);
			break;

		default:;
		}
	}
	
	private void showMode(CameraControl.Mode mode) {
		ToggleButton modeMulti = (ToggleButton)findViewById(R.id.mode_multi);
		ToggleButton modeSingle = (ToggleButton)findViewById(R.id.mode_single);
		ToggleButton modeVideo = (ToggleButton)findViewById(R.id.mode_video);
		
		modeMulti.setChecked(false);
		modeSingle.setChecked(false);
		modeVideo.setChecked(false);
		
		showView(R.id.camera_view_preview_progress, false);
		showView(R.id.camera_view_video_progress, false);
		showView(R.id.camera_view_picture_progress, false);
		
		switch (mode) {
		case PREVIEW:
			modeMulti.setChecked(true);
			showView(R.id.camera_view_preview_progress, true);
			break;
		
		case PICTURE:
			modeSingle.setChecked(true);
			showView(R.id.camera_view_picture_progress, true);

			break;
		case VIDEO:
			modeVideo.setChecked(true);
			showView(R.id.camera_view_video_progress, true);

			break;
		default:;
		}
		
		setRequestedOrientation(mode == CameraControl.Mode.VIDEO ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_SENSOR);	
		enableRotateViews(mode == CameraControl.Mode.VIDEO);
	}
	
	private boolean mEnableRotateViews = true;
	private void enableRotateViews(boolean enable) {
		mEnableRotateViews = enable;
	}
	
	private void rotateViews(int degree) {
		if (mEnableRotateViews) {
		    ((RotateImageView)findViewById(R.id.capture)).setDegree(degree);
		    ((RotateImageView)findViewById(R.id.pause)).setDegree(degree);
		}
		
	    ((RotateImageView)findViewById(R.id.thumbnail)).setDegree(degree);
	}
	
	private void onOrientationChanged(boolean compensation, int previous, int current) {	
		if (compensation)
			rotateViews(current);
		else
			mCameraControl.setOrientation(false, current);			
	}
	
    private void keepScreenOn(boolean keep) {
    	if (keep)
    		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	else
    		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    private int getScreenOrientation() {
    	return getWindowManager().getDefaultDisplay().getOrientation() * 90;    	
    }
    
    private boolean isOrientationIndicatorNeeded() {
		int rotate = 0;
		
		try {
			rotate = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		
		return (rotate != 0);
    }
      
    private void showView(int id, boolean show) {
		View view = findViewById(id);
		view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    private void toggleView(int id) {
		View view = findViewById(id);
		int visibility = view.getVisibility();
		switch (visibility) {
			case View.VISIBLE:
				visibility = View.GONE;
				break;

			case View.GONE:
			case View.INVISIBLE:
				visibility = View.VISIBLE;
				break;

			default:;
		}
    
		view.setVisibility(visibility);
    }
    
	private String mPreviewStoragePath;
	
	private String queryStoragePath(CameraControl.Mode mode) {	
		switch (mode) {
			case PREVIEW: {
				if (mPreviewStoragePath == null)
					mPreviewStoragePath = FileHelper.getNextAppTempPath();
				
				return mPreviewStoragePath;
			}
			
			case PICTURE: {
				return FileHelper.getAppCameraPath();
			}
			
			case VIDEO: {
				return FileHelper.getAppVideoPath();
			}
		}
		
		return null;
	}
	
	private int mPictureCount;
	
	private int queryPictureCount() {
		return mPictureCount;
	}
	
	private void incPictureCount() {
		mPictureCount++;
	}
	
	private void saveStates(Bundle outState) {
		outState.putString(PREVIEW_STORAGE_PATH, mPreviewStoragePath);
		outState.putInt(PICTURE_COUNT, mPictureCount);
	}

	private void restoreStates(Bundle savedInstanceState) {	
		mPreviewStoragePath = savedInstanceState.getString(PREVIEW_STORAGE_PATH);
		mPictureCount = savedInstanceState.getInt(PICTURE_COUNT);
	}
	
	private String getDefaultStoragePath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	private boolean isFirstLaunch() {
		SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		int firstLaunch = sharedPreferences.getInt(PREF_FIRST_LAUNCH, 1);
		if (firstLaunch != 0)
			sharedPreferences.edit().putInt(PREF_FIRST_LAUNCH, 0).commit();

		return (firstLaunch != 0);
	}
}