package com.jecfbagsx.android.gifmagic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jecfbagsx.android.data.MediaStoreAdapter;
import com.jecfbagsx.android.data.PhotoAlbum;
import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.ActivityJump;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.HiddenFolderHelper;
import com.jecfbagsx.android.utils.SettingsHelper;
import com.jecfbagsx.android.utils.TitlebarHelper;

public class PhotoAlbumActivity extends Activity implements
		OnItemClickListener, OnItemLongClickListener {
	private static final String TAG = "PhotoAlbumActivity";
	private static final String FIRST_USED_FLAG_SCAN = "FIRST_USED_FLAG_SCAN";
	protected static final int NEW_FOLDER_FOUND = 0;
	protected static final int LOAD_COMPLETED = 1;
	private static final int DLG_SDCARD_INVALID = 0;
	private static final int DLG_NO_FOLDER = 1;
	private static final int DLG_ALBUM_TYPES_SELECT = 3;
	protected static final int DLG_VIDEO_ITEMS_SELECT = 4;
	protected static final int DLG_HIDDEN_FOLDER = 5;
	protected static final int DLG_FIRST_USE_SCAN = 6;
	private static final int RESULT_VIDEO_CAPTURE = 20;
	private static final int RESULT_IMAGE_LIST = 21;
	private GridView mPhotoAlbumGallery;
	private ImageAdapter photoAlbumListAdapter;
	private List<PhotoAlbum> photoAlbumList = new ArrayList<PhotoAlbum>();
	private Thread runningThread;
	private ProgressDialog progressDlg;
	private LayoutInflater mLayoutInflater;
	private boolean isProgressDlgDismiss = false;
	private boolean isExit = false;
	private PhotoAlbum mSelectedAlbum;
	private int mIndex;
	private int mVideoIndex;
	private boolean mLoadCompleted = false;
	private MediaStoreAdapter mediaStoreAdapter = new MediaStoreAdapter();
	private VideoListAdapter mVideoListAdapter = new VideoListAdapter(this);
	private List<String> mUserHiddenList;
	private int mLongPressed = -1;

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case NEW_FOLDER_FOUND:
				if (photoAlbumList.size() > 4) {
					isProgressDlgDismiss = true;
					progressDlg.dismiss();
				}
				photoAlbumListAdapter.notifyDataSetChanged();
				break;
			case LOAD_COMPLETED:
				if (!isProgressDlgDismiss) {
					progressDlg.dismiss();
				}
				photoAlbumListAdapter.notifyDataSetChanged();
				setProgressBarIndeterminateVisibility(false);
				loadCompleted();
				break;
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.photo_album_view);
		setProgressBarIndeterminateVisibility(true);

		TitlebarHelper.setbackground(this);

		Log.i(TAG, "PhotoAlbum activity create...");
		photoAlbumListAdapter = new ImageAdapter(this);
		mPhotoAlbumGallery = (GridView) findViewById(R.id.photo_album_gallery);
		mPhotoAlbumGallery.setAdapter(photoAlbumListAdapter);
		mPhotoAlbumGallery.setOnItemClickListener(this);
		mPhotoAlbumGallery.setOnItemLongClickListener(this);
		registerForContextMenu(mPhotoAlbumGallery);
		// Cannot move it the background thread. it must in UI thread
		mediaStoreAdapter.init(PhotoAlbumActivity.this);
		mUserHiddenList = HiddenFolderHelper.getUserHiddenFolder(this);

		String title = getResources().getString(
				R.string.video_select_loading_title);
		String content = getResources().getString(R.string.scan_photo);
		mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		progressDlg = ProgressDialog.show(this, title, content, true);
		runningThread = new Thread() {
			public void run() {
				try {
					scanPhotoAlbum();
				} catch (Exception e) {
				}

				Message msg = new Message();
				msg.what = LOAD_COMPLETED;
				handler.sendMessage(msg);
			}
		};
		runningThread.start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		isExit = true;
		try {
			runningThread.join(1000);
		} catch (InterruptedException e) {
		}
		Log.i(TAG, "Wait Thread end....");
		mIndex = 0;
		mVideoIndex = 0;
		mSelectedAlbum = photoAlbumList.get(position);

		if (mSelectedAlbum.isPhotoAlbum() && mSelectedAlbum.isVideoAlbum()) {
			showDialog(DLG_ALBUM_TYPES_SELECT);
		} else if (mSelectedAlbum.isPhotoAlbum()
				&& !mSelectedAlbum.isVideoAlbum()) {
			showPhotoSelect();
		} else if (!mSelectedAlbum.isPhotoAlbum()
				&& mSelectedAlbum.isVideoAlbum()) {
			if (mSelectedAlbum.getVideosList().size() <= 1) {
				gotoVideoCaptureView(PhotoAlbumActivity.this.mSelectedAlbum
						.getVideosList().get(0));
			} else {
				showDialog(DLG_VIDEO_ITEMS_SELECT);
			}
		} else {
			finish();
		}
	}

	private void showPhotoSelect() {
		Intent intent = new Intent(ActivityActions.ACTION_MULTIPLE_SELECTION);
		intent.putExtra(ActivityActions.EXTRA_MULTIPLE_SELECTION_SOUCEPATH,
				mSelectedAlbum.getPath());
		intent.putExtra(ActivityActions.EXTRA_PREVIEW_CAPTURE_STORAGE_PATH,
				FileHelper.getNextAppTempPath());
		if (mSelectedAlbum.getPath().endsWith(FileHelper.VIDEO_FOLDER_END_FLAG));
		{
			intent.putExtra(ActivityActions.EXTRA_IS_FROM_VIDEO_CAPTURE, true);
		}
		startActivityForResult(intent, RESULT_IMAGE_LIST);
	}

	private void scanPhotoAlbum() {
		scanPhotos();
		Log.i(TAG, "Scan completed");
	}

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			if (photoAlbumList == null) {
				return 0;
			}
			return photoAlbumList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView = mLayoutInflater.inflate(
					R.layout.photo_album_item_view, null);
			ImageView[] ims = new ImageView[4];
			ims[0] = (ImageView) v.findViewById(R.id.one);
			ims[1] = (ImageView) v.findViewById(R.id.two);
			ims[2] = (ImageView) v.findViewById(R.id.three);
			ims[3] = (ImageView) v.findViewById(R.id.four);
			TextView text = (TextView) v.findViewById(R.id.desc);
			ImageView photoLogo = (ImageView) v.findViewById(R.id.photo_logo);
			ImageView videoLogo = (ImageView) v.findViewById(R.id.video_logo);

			PhotoAlbum p = photoAlbumList.get(position);
			text.setText(p.getDesc());
			List<Bitmap> lst = p.getThumbnailList();
			if (lst.size() > 0) {
				int i = 0;
				for (Bitmap b : lst) {
					ims[i].setImageBitmap(b);
					i++;
				}
			}
			if (p.isPhotoAlbum()) {
				photoLogo.setVisibility(View.VISIBLE);
			}
			if (p.isVideoAlbum()) {
				videoLogo.setVisibility(View.VISIBLE);
			}
			return v;
		}

	}

	private void gotoVideoCaptureView(String videoFile) {
		Intent intent = new Intent(ActivityActions.ACTION_VIDEO_CAPTURE);
		intent.putExtra(ActivityActions.EXTRA_VIDEO_CAPTURE_FILE, videoFile);
		intent.putExtra(ActivityActions.EXTRA_PREVIEW_CAPTURE_STORAGE_PATH,
				FileHelper.getNextAppVideoTempPath());
		startActivityForResult(intent, RESULT_VIDEO_CAPTURE);
	}

	@Override
	public void onBackPressed() {
		mVideoListAdapter.notifyDataSetChanged();
		removeDialog(DLG_VIDEO_ITEMS_SELECT);
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		try {
			super.onResume();
		} catch (Exception ex) {

		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dlg = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				PhotoAlbumActivity.this);
		switch (id) {
		case DLG_SDCARD_INVALID:
			builder.setTitle(R.string.app_name);
			builder.setMessage(R.string.sdcard_busy);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PhotoAlbumActivity.this.finish();
						}
					});
			dlg = builder.create();
			break;
		case DLG_NO_FOLDER:
			builder.setTitle(R.string.app_name);
			builder.setMessage(R.string.no_folder);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PhotoAlbumActivity.this.finish();
						}
					});
			dlg = builder.create();
			break;
		case DLG_ALBUM_TYPES_SELECT:
			mIndex = 0;
			builder.setTitle(R.string.select_mode);
			builder.setSingleChoiceItems(R.array.photo_album_types, 0,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mIndex = which;
						}

					});
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (mIndex == 0) {
								showPhotoSelect();
							} else {
								if (PhotoAlbumActivity.this.mSelectedAlbum
										.getVideosList().size() > 1) {
									PhotoAlbumActivity.this
											.showDialog(DLG_VIDEO_ITEMS_SELECT);
								} else {
									gotoVideoCaptureView(PhotoAlbumActivity.this.mSelectedAlbum
											.getVideosList().get(0));
								}
							}
							removeDialog(DLG_ALBUM_TYPES_SELECT);
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mIndex = 0;
							removeDialog(DLG_ALBUM_TYPES_SELECT);
						}

					});
			dlg = builder.create();
			break;
		case DLG_VIDEO_ITEMS_SELECT:
			mVideoIndex = 0;
			builder.setTitle(R.string.select_video);
			builder.setAdapter(mVideoListAdapter,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mVideoIndex = which;
							gotoVideoCaptureView(PhotoAlbumActivity.this.mSelectedAlbum
									.getVideosList().get(mVideoIndex));
							removeDialog(DLG_VIDEO_ITEMS_SELECT);
						}

					});
			dlg = builder.create();
			mVideoListAdapter.notifyDataSetChanged();
			break;
		case DLG_HIDDEN_FOLDER:
			builder.setTitle(R.string.hide_folder);
			builder.setMessage(R.string.hide_folder_message);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PhotoAlbum p = photoAlbumList.get(mLongPressed);
							String str = p.getPath();
							HiddenFolderHelper.addUserHiddenFolder(
									PhotoAlbumActivity.this, str);
							photoAlbumList.remove(p);
							mLongPressed = -1;
							photoAlbumListAdapter.notifyDataSetChanged();
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
		case DLG_FIRST_USE_SCAN:
			builder.setTitle(R.string.info);
			builder.setMessage(R.string.info_hide_folder);
			builder.setPositiveButton(R.string.ok,
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
	protected void onPrepareDialog(int id, Dialog dialog) {
		mVideoListAdapter.notifyDataSetChanged();
		super.onPrepareDialog(id, dialog);
	}

	/*
	 * @Override public boolean onContextItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case ID_HIDE_FOLDER: if(mLongPressed != -1 ) {
	 * PhotoAlbum p = photoAlbumList.get(mLongPressed); String str =
	 * p.getPath(); HiddenFolderHelper.addUserHiddenFolder(this, str);
	 * photoAlbumList.remove(p); mLongPressed = -1;
	 * photoAlbumListAdapter.notifyDataSetChanged(); } break; case
	 * ID_SHOW_ALL_FOLDER: HiddenFolderHelper.removeAllUserHiddenFolder(this);
	 * break;
	 * 
	 * } return super.onContextItemSelected(item); }
	 * 
	 * @Override public void onCreateContextMenu(ContextMenu menu, View v,
	 * ContextMenuInfo menuInfo) { menu.add(Menu.NONE, ID_HIDE_FOLDER,
	 * Menu.NONE, R.string.hide_folder); menu.add(Menu.NONE, ID_SHOW_ALL_FOLDER,
	 * Menu.NONE, R.string.show_folder); }
	 */

	private void loadCompleted() {
		mLoadCompleted = true;
		if (!FileHelper.IsSDCardValid()) {
			showDialog(DLG_SDCARD_INVALID);
		}else {
			showFirstScanWarning();
		}
	}

	public void scanPhotos() {

		if (!FileHelper.IsSDCardValid()) {
			return;
		}

		String iternalS = FileHelper.getInternalStorage();
		ScanPath(iternalS);
		String externalS = FileHelper.getExternalStorage();

		if (externalS != null) {
			ScanPath(externalS);
		}
		return;
	}

	private void ScanPath(String path) {
		if (path == null) {
			return;
		}
		
		File file = new File(path);
		File[] files = file.listFiles();
		String folderPath = file.getAbsolutePath();
		if (FileHelper.isHiddenFolder(folderPath, mUserHiddenList)) {
			return;
		}
		if (isExit) {
			return;
		}
		String folderName = folderPath.substring(
				folderPath.lastIndexOf("/") + 1, folderPath.length());
		PhotoAlbum p = new PhotoAlbum(folderPath, folderName, mediaStoreAdapter);
		if (files != null && files.length > 0) {
			for (File f : files) {
				if (isExit) {
					return;
				}
				
				if (f.isDirectory() && !f.isHidden()) {
					ScanPath(f.getAbsolutePath());
				} else if (f.isFile() && !f.isHidden()) {
					String temp = f.getPath();
					String suffix = temp.substring(temp.lastIndexOf(".") + 1,
							temp.length());
					if (FileHelper.isPhotoSuffixSupported(suffix)) {
						p.getPhotosList().add(temp);
					} else if (FileHelper.isVideoSuffixSupported(suffix)
							&& SettingsHelper.isVideoCaptureEnable(this)) {
						p.getVideosList().add(temp);
					}
				}
			}
		}

		if (p.getPhotosList().size() > 0 || p.getVideosList().size() > 0) {
			photoAlbumList.add(p);
			p.genarateThumbnail();
			Message msg = new Message();
			msg.what = NEW_FOLDER_FOUND;
			handler.sendMessage(msg);
		}
	}

	private void showFirstScanWarning() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean firstFlag = prefs.getBoolean(FIRST_USED_FLAG_SCAN, false);
		if (!firstFlag) {
			prefs.edit().putBoolean(FIRST_USED_FLAG_SCAN, true).commit();
			showDialog(DLG_FIRST_USE_SCAN);
		}
	}

	private class VideoListAdapter extends BaseAdapter {
		public VideoListAdapter(Context mContext) {

		}

		public void refresh() {

		}

		@Override
		public int getCount() {
			return mSelectedAlbum.getVideosList().size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String videoPath = mSelectedAlbum.getVideosList().get(position);
			View v = convertView = mLayoutInflater.inflate(
					R.layout.video_list_item, null);
			TextView txt = (TextView) v.findViewById(R.id.video_item_name);
			txt.setText(FileHelper.getFileNameFromFullPath(videoPath));
			ImageView img = (ImageView) v.findViewById(R.id.video_item_thumb);
			Bitmap bmp = mediaStoreAdapter.getVideoThumbnail(videoPath);
			if (bmp != null) {
				img.setImageBitmap(bmp);
			}
			return v;
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view,
			int position, long id) {
		mLongPressed = position;
		showDialog(DLG_HIDDEN_FOLDER);
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.photo_album_show, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_photo_show_folder:
			HiddenFolderHelper.removeAllUserHiddenFolder(this);
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_VIDEO_CAPTURE:
		case RESULT_IMAGE_LIST:
			if (resultCode == RESULT_OK) {
				if (data.getBooleanExtra(ActivityJump.CLOSE_YOURSELF, false)) {
					setResult(RESULT_OK, data);
					finish();
				}
			}
			break;
		}
	}

}
