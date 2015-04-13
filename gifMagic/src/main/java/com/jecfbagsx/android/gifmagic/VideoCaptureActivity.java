package com.jecfbagsx.android.gifmagic;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jecfbagsx.android.data.ResolutionInfo;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.TimeUtils;
import com.jecfbagsx.android.utils.VideoCaptureHelper;
import com.jecfbagsx.android.video.FrameCapture;

public class VideoCaptureActivity extends Activity {
	private FrameCapture frameCapture;
	private ResolutionInfo videoRes;
	private static final String TAG = "VideoCaptureActivity";
	private ImageView videoFrameView;
	protected static final int CAPTURE_COMPLETED = 0;
	protected static final int CAPTURE_STEP = 1;
	private static final String TOTAL = "TOTAL";
	private static final String CURRENT = "CURRENT";
	private static final int MAX_VIDEO_BOUND = 480;
	private static final int DLG_WARNING_VERSION = 0;
	private SeekBar mvideoSeekbar;
	String strTempPath;
	private Bitmap mCurrentImage;
	private String mStoragePath;
	private String mCaptureFile;

	private ImageView mCaptureImageView;
	private ImageView mBrowseImageView;
	private ImageView mSettingImageView;
	private int targetWidth = 320;
	private int targetHeight = 240;
	private boolean mIsCapturing = false;
	private boolean mIsExit = false;
	private Thread runningThread;
	private ProgressBar mCaptureprogress;
	private long mFileDuration;
	private int mFileIndex = 1;
	private TextView mVideoInfo;
	private String videoInfoFormatStr;
	private boolean isJniFailed = false;
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case CAPTURE_COMPLETED:
				mIsCapturing = false;
				mCaptureprogress.setProgress((int) mFileDuration);
				gotoImageView();
				break;
			case CAPTURE_STEP:
				Bundle bundle = message.getData();
				int current = bundle.getInt(CURRENT, 0);
				mCaptureprogress.setProgress(current);
				showImage(mCurrentImage);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vcapture);

		mCaptureImageView = (ImageView) findViewById(R.id.vcapture_preview);
		mCaptureImageView.setOnClickListener(captureButtonListener);
		mBrowseImageView = (ImageView) findViewById(R.id.browse);
		mBrowseImageView.setOnClickListener(browseButtonListener);
		mSettingImageView = (ImageView) findViewById(R.id.setup);
		mSettingImageView.setOnClickListener(settingButtonListener);
		mVideoInfo = (TextView) findViewById(R.id.vcapture_video_info);

		mvideoSeekbar = (SeekBar) findViewById(R.id.video_seekbar);
		videoFrameView = (ImageView) findViewById(R.id.video_capture_preview);
		mCaptureprogress = (ProgressBar) findViewById(R.id.capture_progress);

		try {
			frameCapture = new FrameCapture();
		} catch (Error ex) {
			Log.e(TAG, "Error:FrameCapture instucture");
			showDialog(DLG_WARNING_VERSION);
			isJniFailed = true;
			return;
		}
		mCaptureFile = getIntent().getStringExtra(
				ActivityActions.EXTRA_VIDEO_CAPTURE_FILE);
		mStoragePath = getIntent().getStringExtra(
				ActivityActions.EXTRA_PREVIEW_CAPTURE_STORAGE_PATH);

		Log.i(TAG, "mCaptureFile:" + mCaptureFile);

		frameCapture.setDataSource(mCaptureFile);
		videoRes = frameCapture.getResolution();
		mFileDuration = frameCapture.getDuration();
		mvideoSeekbar.setMax((int) mFileDuration);
		mvideoSeekbar.setOnSeekBarChangeListener(seekbarListener);
		ResolutionInfo adjustRes = null;
		if (videoRes.getWidth() > 854 || videoRes.getHeight() > 854) {
			adjustRes = FileHelper.getRefinedResolutionInfo(videoRes,
					MAX_VIDEO_BOUND);
		} else {
			adjustRes = videoRes;
		}
		targetWidth = adjustRes.getWidth();
		targetHeight = adjustRes.getHeight();
		mCurrentImage = frameCapture.getNextFrame(targetWidth, targetHeight, 0);
		showImage(mCurrentImage);
		Log.i(TAG, videoRes.toString() + " Dua:" + mFileDuration);
		Log.i(TAG, "Target Resolution: " + targetWidth + "x" + targetHeight);
		videoInfoFormatStr = getResources().getString(R.string.video_info);
		setVideoInfo();
	}

	SeekBar.OnSeekBarChangeListener seekbarListener = new SeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (VideoCaptureActivity.this.mIsCapturing) {
				return;
			}
			Log.i(TAG, "Progress Changed(ms):" + progress);
			mCurrentImage = frameCapture.seekTo(targetWidth, targetHeight,
					progress);
			showImage(mCurrentImage);
			setVideoInfo();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}

	};

	private void setVideoInfo() {
		String totalTime = TimeUtils.getFormatTime(mFileDuration);
		String curTime = TimeUtils.getFormatTime(frameCapture.getCurrentPos());
		mVideoInfo.setText(String
				.format(videoInfoFormatStr, curTime, totalTime));
	}

	private void showImage(Bitmap bitmap) {
		if (bitmap == null) {
			return;
		}
		Bitmap newBmp = bitmap.copy(Bitmap.Config.RGB_565, false);
		if (newBmp != null) {
			videoFrameView.setImageBitmap(newBmp);
		}
	}

	@Override
	protected void onDestroy() {
		if (!isJniFailed) {
			stopCaptureThread();
			frameCapture.release();
		}
		super.onDestroy();
	}

	private Button.OnClickListener captureButtonListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			if (mIsCapturing) {
				return;
			}

			mvideoSeekbar.setEnabled(false);
			mIsCapturing = true;
			mCaptureprogress.setMax(VideoCaptureHelper.getCaptureCount());
			// mStartpos = mvideoSeekbar.getProgress();
			runningThread = new Thread() {
				public void run() {
					try {
						for (int i = 0; i < VideoCaptureHelper
								.getCaptureCount() && !mIsExit; i++) {
							int nSkipFrame = VideoCaptureHelper
									.getCaptureSkipFrame();
							String newFile = mStoragePath + File.separator
									+ FileHelper.getNextFileorFolderName()
									+ "_gifmagic_" + mFileIndex + ".png";
							mFileIndex++;
							Bitmap bmp = frameCapture.getNextFrame(targetWidth,
									targetHeight, nSkipFrame);
							if (bmp == null) {
								Log.i(TAG, "Capture to the end or Error." + i);
								break;
							}
							mCurrentImage = bmp;
							FileHelper.saveBitmapAsPng(bmp, newFile);

							Message msg = new Message();
							msg.what = CAPTURE_STEP;
							Bundle bunlde = new Bundle();
							bunlde.putInt(TOTAL,
									VideoCaptureHelper.getCaptureCount());
							bunlde.putInt(CURRENT, i);
							// bunlde.putInt(SEEK_POS, pos);
							msg.setData(bunlde);
							handler.sendMessage(msg);
						}
					} catch (Exception e) {
					}

					Message msg = new Message();
					msg.what = CAPTURE_COMPLETED;
					handler.sendMessage(msg);
				}
			};
			runningThread.start();
		}

	};

	private Button.OnClickListener browseButtonListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			Intent intent = new Intent(ActivityActions.ACTION_HISTORY);
			intent.putExtra(ActivityActions.EXTRA_HISTORY_SOUCEPATH,
					FileHelper.getAppHistoryPath());
			startActivity(intent);
		}
	};

	private Button.OnClickListener settingButtonListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			startActivity(new Intent(ActivityActions.ACTION_SETTING));
		}
	};

	private void gotoImageView() {
		Intent intent = new Intent(ActivityActions.ACTION_MULTIPLE_SELECTION);
		intent.putExtra(ActivityActions.EXTRA_MULTIPLE_SELECTION_SOUCEPATH,
				mStoragePath);
		intent.putExtra(ActivityActions.EXTRA_PREVIEW_CAPTURE_STORAGE_PATH,
				FileHelper.getNextAppTempPath());
		intent.putExtra(ActivityActions.EXTRA_IS_FROM_VIDEO_CAPTURE, true);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		stopCaptureThread();
		super.onBackPressed();
	}

	private void stopCaptureThread() {
		mIsExit = true;
		try {
			if (runningThread != null) {
				runningThread.join(1000);
			}
		} catch (InterruptedException e) {
		}
		Log.i(TAG, "video capture Thread end....");
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dlg = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case DLG_WARNING_VERSION:
			builder.setTitle(R.string.capture_error);
			builder.setMessage(R.string.capture_error_message);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							VideoCaptureActivity.this.finish();
						}
					});
			dlg = builder.create();
			break;
		}
		return dlg;
	}

}
