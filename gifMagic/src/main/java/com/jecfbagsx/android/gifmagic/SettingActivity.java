package com.jecfbagsx.android.gifmagic;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.jecfbagsx.android.data.CameraSpeed;
import com.jecfbagsx.android.data.Duration;
import com.jecfbagsx.android.data.Quality;
import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.gifmanage.OAuthManager;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.SettingsHelper;
import com.jecfbagsx.android.utils.TimeUtils;
import com.jecfbagsx.android.utils.TitlebarHelper;

public class SettingActivity extends Activity {
	private static final String TAG = "AboutActivity";
	private static final int ACTIVITY_SUCCESS = 0;
	private static final int ACTIVITY_FAIL = 1;
	private static final int NO_ACTIVITY = 2;
	protected static final int ACCOUNT_SELECTION_DLG = 3;
	protected static final int KEEP_TEMP_IMAGE_DLG = 4;
	protected static final int QUIT_APP_DLG = 5;
	private ToggleButton mQualityLow;
	private ToggleButton mQualityMiddle;
	private ToggleButton mQualityHigh;

	private ToggleButton mCameraFast;
	private ToggleButton mCameraNormal;
	private ToggleButton mCameraSlow;

	private SeekBar mTotalCameraCaptureSeekbar;
	private SeekBar mTotalVideoCaptureSeekbar;
	private TextView mCurrentCameraCaptureValue;
	private TextView mCurrentVideoCaptureValue;
	private int defCameraCaputeNum;
	private int defVideoCaputeNum;
	private Quality defQuality;
	private CameraSpeed defSpeed;
	private RadioGroup mLanGroup;
	private RadioButton mLan_En;
	private RadioButton mLan_zh_CN;

	private Button mbtnCleanAccount;
	private Button mbtnKeppTempImage;
	private Button mbtnQuit;
	private boolean[] mcheckedStates = { true, true, true};
	private int defLanIndex;
	private int mLan_index;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		setTitle(R.string.menu_setting);

		TitlebarHelper.setbackground(this);

		Log.i(TAG, "Setting activity create...");
		defLanIndex = SettingsHelper.getLanguageIndex();
		mLan_index = defLanIndex;
		defCameraCaputeNum = SettingsHelper.getCameraDuration();
		defVideoCaputeNum = SettingsHelper.getVideoDuration();
		defQuality = SettingsHelper.getResolutionInfo().getType();
		defSpeed = SettingsHelper.getCameraSpeedType();

		map_view();

		// setDurationCheck(defDuration);
		mCurrentCameraCaptureValue
				.setText(Integer.toString(defCameraCaputeNum));
		mCurrentVideoCaptureValue.setText(Integer.toString(defVideoCaputeNum));
		setQualityCheck(defQuality);
		setCameraSpeed(defSpeed);
	}

	private void map_view() {
		mQualityLow = (ToggleButton) findViewById(R.id.toggle_quality_l);
		mQualityMiddle = (ToggleButton) findViewById(R.id.toggle_quality_m);
		mQualityHigh = (ToggleButton) findViewById(R.id.toggle_quality_h);
		mbtnCleanAccount = (Button) findViewById(R.id.setting_clean_account);
		mbtnKeppTempImage = (Button) findViewById(R.id.setting_keep_temp_image);
		mbtnQuit = (Button) findViewById(R.id.setting_quit_app);

		mQualityLow.setOnClickListener(qualityLowListener);
		mQualityMiddle.setOnClickListener(qualityMiddleListener);
		mQualityHigh.setOnClickListener(qualityHighListener);

		mCurrentCameraCaptureValue = (TextView) findViewById(R.id.txtv_camera_duration_value);
		mTotalCameraCaptureSeekbar = (SeekBar) findViewById(R.id.setting_camera_duration_seekbar);
		mTotalCameraCaptureSeekbar
				.setOnSeekBarChangeListener(seekbarCameraListener);
		mTotalCameraCaptureSeekbar.setProgress(defCameraCaputeNum);

		mCurrentVideoCaptureValue = (TextView) findViewById(R.id.txtv_video_duration_value);
		mTotalVideoCaptureSeekbar = (SeekBar) findViewById(R.id.setting_video_duration_seekbar);
		mTotalVideoCaptureSeekbar
				.setOnSeekBarChangeListener(seekbarVideoListener);
		mTotalVideoCaptureSeekbar.setProgress(defVideoCaputeNum);

		mCameraFast = (ToggleButton) findViewById(R.id.toggle_camera_f);
		mCameraNormal = (ToggleButton) findViewById(R.id.toggle_camera_n);
		mCameraSlow = (ToggleButton) findViewById(R.id.toggle_camera_s);

		mCameraFast.setOnClickListener(cameraFastListener);
		mCameraNormal.setOnClickListener(cameraNormalListener);
		mCameraSlow.setOnClickListener(cameraSlowListener);

		mLanGroup = (RadioGroup) findViewById(R.id.setting_lans);
		mLan_En = (RadioButton) findViewById(R.id.setting_lan_en);
		mLan_zh_CN = (RadioButton) findViewById(R.id.setting_lan_zh_CN);
		if (defLanIndex == 0) {
			mLan_En.setChecked(true);
		} else {
			mLan_zh_CN.setChecked(true);
		}

		mLanGroup.setOnCheckedChangeListener(lanChangedLinstener);
		mbtnCleanAccount.setOnClickListener(cleanAccountListener);
		mbtnKeppTempImage.setOnClickListener(keepTempImageListener);
		mbtnQuit.setOnClickListener(quitAppListener);
	}

	SeekBar.OnSeekBarChangeListener seekbarCameraListener = new SeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			defCameraCaputeNum = progress;
			if (defCameraCaputeNum<2)
			{
				defCameraCaputeNum = 2;
			}
			mCurrentCameraCaptureValue.setText(Integer.toString(defCameraCaputeNum));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	};

	SeekBar.OnSeekBarChangeListener seekbarVideoListener = new SeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			defVideoCaputeNum = progress;
			if (defVideoCaputeNum<2)
			{
				defVideoCaputeNum = 2;
			}
			mCurrentVideoCaptureValue.setText(Integer.toString(defVideoCaputeNum));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	};

	private OnCheckedChangeListener lanChangedLinstener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == mLan_En.getId()) {
				mLan_index = 0;
			} else if (checkedId == mLan_zh_CN.getId()) {
				mLan_index = 1;
			} else {

			}
		}
	};

	private void setQualityCheck(Quality q) {
		switch (q) {
		case Low:
			mQualityLow.setChecked(true);
			mQualityMiddle.setChecked(false);
			mQualityHigh.setChecked(false);
			break;
		case Middle:
			mQualityLow.setChecked(false);
			mQualityMiddle.setChecked(true);
			mQualityHigh.setChecked(false);
			break;
		case High:
			mQualityLow.setChecked(false);
			mQualityMiddle.setChecked(false);
			mQualityHigh.setChecked(true);
			break;
		}
	}

	private void setCameraSpeed(CameraSpeed speed) {
		switch (speed) {
		case fast:
			mCameraFast.setChecked(true);
			mCameraNormal.setChecked(false);
			mCameraSlow.setChecked(false);
			break;
		case normal:
			mCameraFast.setChecked(false);
			mCameraNormal.setChecked(true);
			mCameraSlow.setChecked(false);
			break;
		case slow:
			mCameraFast.setChecked(false);
			mCameraNormal.setChecked(false);
			mCameraSlow.setChecked(true);
			break;
		}
	}

	private Button.OnClickListener cleanAccountListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(ACCOUNT_SELECTION_DLG);
		}
	};
	private Button.OnClickListener keepTempImageListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(KEEP_TEMP_IMAGE_DLG);
		}
	};

	private Button.OnClickListener quitAppListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(QUIT_APP_DLG);
		}
	};

	private Button.OnClickListener qualityLowListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (defQuality != Quality.Low) {
				defQuality = Quality.Low;
				SettingsHelper.setResolutionType(SettingActivity.this,
						defQuality);

			}
			setQualityCheck(defQuality);
		}

	};

	private Button.OnClickListener qualityMiddleListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {

			if (defQuality != Quality.Middle) {
				defQuality = Quality.Middle;
				SettingsHelper.setResolutionType(SettingActivity.this,
						defQuality);
			}

			setQualityCheck(defQuality);
		}

	};

	private Button.OnClickListener qualityHighListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			/*
			 * if (!SettingsHelper.IsActivitied()) { showDialog(NO_ACTIVITY); }
			 * else {
			 */
			if (defQuality != Quality.High) {
				defQuality = Quality.High;
				SettingsHelper.setResolutionType(SettingActivity.this,
						defQuality);
			}
			setQualityCheck(defQuality);
		}

	};

	private Button.OnClickListener cameraFastListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (defSpeed != CameraSpeed.fast) {
				defSpeed = CameraSpeed.fast;
				SettingsHelper.setCameraSpeed(SettingActivity.this, defSpeed);

			}
			setCameraSpeed(defSpeed);
		}

	};

	private Button.OnClickListener cameraNormalListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (defSpeed != CameraSpeed.normal) {
				defSpeed = CameraSpeed.normal;
				SettingsHelper.setCameraSpeed(SettingActivity.this, defSpeed);

			}
			setCameraSpeed(defSpeed);
		}

	};

	private Button.OnClickListener cameraSlowListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (defSpeed != CameraSpeed.slow) {
				defSpeed = CameraSpeed.slow;
				SettingsHelper.setCameraSpeed(SettingActivity.this, defSpeed);

			}
			setCameraSpeed(defSpeed);
		}

	};

	@Override
	protected Dialog onCreateDialog(int id) {

		Dialog dlg = null;
		AlertDialog.Builder builder = null;
		switch (id) {
		case ACTIVITY_SUCCESS:
			builder = new AlertDialog.Builder(SettingActivity.this);
			builder.setTitle(R.string.activate);
			builder.setMessage(R.string.active_success);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			dlg = builder.create();
			break;
		case ACTIVITY_FAIL:
			builder = new AlertDialog.Builder(SettingActivity.this);
			builder.setTitle(R.string.activate);
			builder.setMessage(R.string.active_failed);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			dlg = builder.create();
			break;
		case NO_ACTIVITY:
			builder = new AlertDialog.Builder(SettingActivity.this);
			builder.setTitle(R.string.activate);
			builder.setMessage(R.string.vip_feature_contact);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			dlg = builder.create();
			break;
		case ACCOUNT_SELECTION_DLG:
			String[] accounts = new String[3];
			accounts[0] = getResources().getString(R.string.account_sina);
			accounts[1] = getResources().getString(R.string.account_qq);
			accounts[2] = getResources().getString(R.string.account_renren);

			builder = new AlertDialog.Builder(SettingActivity.this);
			builder.setTitle(R.string.account_list);
			builder.setMultiChoiceItems(accounts, mcheckedStates,
					new DialogInterface.OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
							mcheckedStates[which] = isChecked;
						}
					});
			builder.setPositiveButton(R.string.general_delete,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (mcheckedStates[0]) {
								OAuthManager.getInstance()
										.Cleanup(OAuthManager.SINA,
												SettingActivity.this);
							}
							if (mcheckedStates[1]) {
								OAuthManager.getInstance().Cleanup(
										OAuthManager.TENCENT,
										SettingActivity.this);
							}
							if (mcheckedStates[2]) {
								OAuthManager.getInstance().Cleanup(
										OAuthManager.RENREN,
										SettingActivity.this);
							}
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			dlg = builder.create();
			break;
		case KEEP_TEMP_IMAGE_DLG:
			builder = new AlertDialog.Builder(SettingActivity.this);
			builder.setTitle(R.string.keep_temp_image_message);
			String[] selects = new String[2];
			selects[0] = getResources().getString(R.string.keep);
			selects[1] = getResources().getString(R.string.delete_after_exit);
			int defsel = SettingsHelper.isKeepTempImage() ? 0 : 1;
			builder.setSingleChoiceItems(selects, defsel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								SettingsHelper.setKeepTempImage(
										SettingActivity.this, true);
							} else {
								SettingsHelper.setKeepTempImage(
										SettingActivity.this, false);
							}
							removeDialog(KEEP_TEMP_IMAGE_DLG);
						}
					});
			dlg = builder.create();
			break;
		case QUIT_APP_DLG:
			builder = new AlertDialog.Builder(SettingActivity.this);
			builder.setTitle(R.string.quit_title);
			builder.setMessage(R.string.quit_message);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Save();
							finish();
							SettingsHelper.exitApp(SettingActivity.this);

						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			dlg = builder.create();
			break;
		}
		return dlg;
	}

	private void Save() {
		SettingsHelper.setTotalCameraCapture(this, defCameraCaputeNum);
		SettingsHelper.setTotalVideoCapture(this, defVideoCaputeNum);
		if (defLanIndex != mLan_index) {
			String strLan = "";
			if (mLan_index == 0) {
				strLan = "en";
			} else {
				strLan = "zh_CN";
			}
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			prefs.edit().putString(SettingsHelper.PREF_LANGUAGE, strLan)
					.commit();
			Bundle bundle = new Bundle();
			bundle.putBoolean(ActivityActions.EXTRA_SETTING_LAN_CHANGED,
					true);
			setResult(RESULT_OK, this.getIntent().putExtras(bundle));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Save();
			this.finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
