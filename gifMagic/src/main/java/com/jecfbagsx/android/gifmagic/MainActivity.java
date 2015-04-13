package com.jecfbagsx.android.gifmagic;

import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.DataManger;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.SettingsHelper;
import com.mobclick.android.MobclickAgent;
import com.mobclick.android.UmengConstants;
import com.mobclick.android.UmengUpdateListener;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private static final int PREFERENCE_REQUEST_CODE = 1;
	private static final int CAPTURE_REQUEST_CODE = 2;
	private static final int ANDROID_2_2_DIALOG = 0;
	private static final int DLG_SDCARD_INVALID = 1;
	private static final int DLG_LANGUAGE_SELECT = 2;
	private static final int DLG_QUIT = 3;
	private static final String FIRST_USED_FLAG = "FIRST_USED_FLAG";
	private static final String FIRST_USED_FLAG_LAN = "FIRST_USED_FLAG_LAN";
	protected static final int SETTING_ACTIVITY_RESULT = 3;
	private Button mCameraBtn;
	private Button mBrowseBtn;
	private Button mSettingAlbumBtn;
	private Button mGalleryAlbumBtn;
	private int mLan_index;
	private int defLanIndex;
	private boolean mbDestroyforLan = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		changeUILanguage(null);
		setContentView(R.layout.main);
		
		MobclickAgent.onError(this);
		MobclickAgent.update(MainActivity.this);
		MobclickAgent.setUpdateOnlyWifi(false);// [true(default) update only
												// wifi, false we'll try to
												// update during 2G or 3G ,if
												// you have no better idea,just
												// gore this params]
		// MobclickAgent.enableCacheInUpdate [true(default) enable cached apk to
		// be installed which has been downloaded before]
		// [false we'll always download the latest version from the server. if
		// you have no better idea,just igore this params.]
		// MobclickAgent.updateAutoPopup= false;
		MobclickAgent.updateOnlineConfig(MainActivity.this);

		Log.i(TAG, "Main activity create...");
		map_view();

		defLanIndex = SettingsHelper.getLanguageIndex();
		Context context = getApplicationContext();
		SettingsHelper.refreshSettings(context);
		checkShorcut();
		showLanguageSelect();
		DataManger.getInstance().init();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dlg = null;
		AlertDialog.Builder builder = null;
		switch (id) {
		case ANDROID_2_2_DIALOG:
			builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle(R.string.app_name);
			builder.setMessage(R.string.android_2_2_supported);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			dlg = builder.create();
			break;
		case DLG_SDCARD_INVALID:
			builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle(R.string.app_name);
			builder.setMessage(R.string.sdcard_busy);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			dlg = builder.create();
			break;
		case DLG_LANGUAGE_SELECT:
			builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle(R.string.app_name);
			builder.setSingleChoiceItems(R.array.language_entries_list,
					defLanIndex, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mLan_index = which;
						}

					});
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (mLan_index != defLanIndex) {
								String strLan = "";
								if (mLan_index == 0) {
									strLan = "en";
								} else {
									strLan = "zh_CN";
								}
								SharedPreferences prefs = PreferenceManager
										.getDefaultSharedPreferences(MainActivity.this);
								prefs.edit()
										.putString(
												SettingsHelper.PREF_LANGUAGE,
												strLan).commit();
								MainActivity.this.finish();
								Intent intent = new Intent(
										ActivityActions.ACTION_MAIN);
								startActivity(intent);
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
		case DLG_QUIT:
			builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.quit_title);
			builder.setMessage(R.string.quit_message);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
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

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private int changeUILanguage(String lan) {
		Log.i(TAG, "changeLanguage: target language:" + lan);
		Context context = getApplicationContext();
		Locale locale = SettingsHelper.getTargetLan(context, lan);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
		return 0;
	}

	private void map_view() {
		mCameraBtn = (Button) findViewById(R.id.main_camera_button);
		mBrowseBtn = (Button) findViewById(R.id.main_browse_button);
		mSettingAlbumBtn = (Button) findViewById(R.id.main_setting_button);
		mGalleryAlbumBtn = (Button) findViewById(R.id.main_gallery_button);
		mCameraBtn.requestFocus();
		mCameraBtn.setOnClickListener(cameraButtonListener);
		mBrowseBtn.setOnClickListener(browseButtonListener);
		mSettingAlbumBtn.setOnClickListener(settingButtonListener);
		mGalleryAlbumBtn.setOnClickListener(galleryButtonListener);
	}

	private Button.OnClickListener cameraButtonListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			Log.i(TAG, "Camera button clicked");
			if (!FileHelper.IsSDCardValid()) {
				showDialog(DLG_SDCARD_INVALID);
			} else {
				Intent intent = new Intent();
				int picCount = SettingsHelper.getCameraDuration();

				intent.putExtra(CameraView.CAPTURE_COUNT, picCount);
				intent.putExtra(CameraView.STORAGE_ROOT_PATH,
						FileHelper.getNextAppTempPath());
				intent.setAction(ActivityActions.ACTION_PREVIEW_CAPTURE);
				MainActivity.this.startActivity(intent);
			}
		}

	};

	private Button.OnClickListener browseButtonListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			Log.i(TAG, "weibo4sina button clicked...");
			if (!FileHelper.IsSDCardValid()) {
				showDialog(DLG_SDCARD_INVALID);
			} else {
				Intent intent = new Intent();
				intent.putExtra(ActivityActions.EXTRA_HISTORY_SOUCEPATH,
						FileHelper.getAppHistoryPath());
				intent.setAction(ActivityActions.ACTION_HISTORY);
				MainActivity.this.startActivity(intent);
			}
		}

	};

	private Button.OnClickListener settingButtonListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			Log.i(TAG, "setting button clicked");
			Intent intent = new Intent(ActivityActions.ACTION_SETTING);
			MainActivity.this.startActivityForResult(intent,
					SETTING_ACTIVITY_RESULT);
		}

	};

	private Button.OnClickListener galleryButtonListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			Log.i(TAG, "gallery button clicked");
			if (!FileHelper.IsSDCardValid()) {
				showDialog(DLG_SDCARD_INVALID);
			} else {
				Intent intent = new Intent(ActivityActions.ACTION_GALLERY);
				MainActivity.this.startActivity(intent);
			}
		}

	};

	private void showSettings() {
		Log.i(TAG, "settings menu clicked");
		Intent intent = new Intent(ActivityActions.ACTION_APPSETTING);
		startActivityForResult(intent, PREFERENCE_REQUEST_CODE);
	}

	private void showAbout() {
		Log.i(TAG, "about button clicked");
		Intent intent = new Intent(ActivityActions.ACTION_ABOUT);
		startActivity(intent);
	}

	private void showHelp() {
		Log.i(TAG, "help button clicked");
		Intent intent = new Intent(ActivityActions.ACTION_HELP);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_about:
			showAbout();
			break;
		case R.id.menu_settings:
			showSettings();
			break;
		case R.id.menu_help:
			showHelp();
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PREFERENCE_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Context context = getApplicationContext();
				SettingsHelper.refreshSettings(context);
				if (data != null) {
					checkLanguage(data);
				}
			}
			break;
		case CAPTURE_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					String storagePath = data
							.getStringExtra(CameraView.STORAGE_PATH);
					Intent intent = new Intent();
					intent.putExtra(
							ActivityActions.EXTRA_MULTIPLE_SELECTION_SOUCEPATH,
							storagePath);
					intent.setClass(MainActivity.this,
							MultipleImageSelectionActivity.class);
					MainActivity.this.startActivity(intent);
				}
			}
			break;
		case SETTING_ACTIVITY_RESULT:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					checkLanguage(data);
				}
			}
			break;
		}
	}

	private void checkLanguage(Intent data) {
		boolean lanChanged = data.getBooleanExtra(
				ActivityActions.EXTRA_SETTING_LAN_CHANGED, false);
		if (lanChanged) {
			mbDestroyforLan = true;
			Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
			startActivity(myIntent);
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		DataManger.getInstance().uninit();
		if (!SettingsHelper.isKeepTempImage()) {
			FileHelper.cleanTempPath();
		}
		super.onDestroy();

		if (!mbDestroyforLan) {
			final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			am.restartPackage(getPackageName());
		}
	}

	private void checkShorcut() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean firstFlag = prefs.getBoolean(FIRST_USED_FLAG, false);
		if (!firstFlag) {
			prefs.edit().putBoolean(FIRST_USED_FLAG, true).commit();
			if (!hasShortcut()) {
				InstallShortcut();
			}
		}
	}

	private void showLanguageSelect() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean firstFlag = prefs.getBoolean(FIRST_USED_FLAG_LAN, false);
		if (!firstFlag) {
			prefs.edit().putBoolean(FIRST_USED_FLAG_LAN, true).commit();
			showDialog(DLG_LANGUAGE_SELECT);
		}
	}

	private void InstallShortcut() {
		if (!hasShortcut()) {
			Intent thisIntent = new Intent();
			thisIntent.setClass(this, Splash.class);
			String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
			Intent addShortcut = new Intent(ACTION_ADD_SHORTCUT);
			Parcelable icon = Intent.ShortcutIconResource.fromContext(this,
					R.drawable.icon);
			String appName = getResources().getString(R.string.app_name);
			addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
			addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
			addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, thisIntent);
			sendBroadcast(addShortcut);
		}
	}

	private boolean hasShortcut() {
		boolean isInstallShortcut = false;
		final ContentResolver cr = getContentResolver();
		final String AUTHORITY = "com.android.launcher.settings";
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/favorites?notify=true");
		Cursor c = cr.query(CONTENT_URI,
				new String[] { "title", "iconResource" }, "title=?",
				new String[] { getString(R.string.app_name).trim() }, null);
		if (c != null && c.getCount() > 0) {
			isInstallShortcut = true;
		}
		if (c != null) {
			c.close();
		}
		return isInstallShortcut;

	}

	@Override
	public void onBackPressed() {
		showDialog(DLG_QUIT);
		
	}

}
