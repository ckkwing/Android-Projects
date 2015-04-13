package com.jecfbagsx.android.gifmagic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.drawable;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.ColorMatrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jecfbagsx.android.data.GenerateData;
import com.jecfbagsx.android.data.ImageItem;
import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.gifmanage.GifDecoder;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.ActivityJump;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.ImageAdapter;
import com.jecfbagsx.android.utils.SettingsHelper;

public class Showcase extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	enum enumSelectionState {
		enumSelectionState_none, enumSelectionState_selectall, enumSelectionState_unselectall,
	}

	enum enumShowcaseType {
		enumShowcaseType_normal, enumShowcaseType_history,
	}

	public static class ShowCaseConfig {
		boolean isContextMenuEnabled = true;
		boolean isNavigationBarVisible = true;
		boolean isBottomToolBarVisible = true;
		boolean isOperationBtnVisible = true;
		enumShowcaseType showcaseType = enumShowcaseType.enumShowcaseType_normal;

		public ShowCaseConfig() {

		}

		public ShowCaseConfig(boolean isContextMenuEnabled,
				boolean isNavigationBarVisible, boolean isBottomToolBarVisible,
				boolean isOperationBtnVisible) {
			this.isContextMenuEnabled = isContextMenuEnabled;
			this.isNavigationBarVisible = isNavigationBarVisible;
			this.isBottomToolBarVisible = isBottomToolBarVisible;
			this.isOperationBtnVisible = isOperationBtnVisible;
		}

		public ShowCaseConfig(boolean isContextMenuEnabled,
				boolean isNavigationBarVisible, boolean isBottomToolBarVisible,
				boolean isOperationBtnVisible, enumShowcaseType type) {
			this.isContextMenuEnabled = isContextMenuEnabled;
			this.isNavigationBarVisible = isNavigationBarVisible;
			this.isBottomToolBarVisible = isBottomToolBarVisible;
			this.isOperationBtnVisible = isOperationBtnVisible;
			this.showcaseType = type;
		}

	}

	// Properties
	ShowCaseConfig m_config = null;
	ImageAdapter m_adapter = null;
	protected int m_layoutResId;
	protected Button m_operationBtn = null;
	protected CheckBox m_selectionBtn = null;
	protected Context m_context = null;
	protected LayoutInflater m_layoutInflater = null;
	protected String m_titleString = "";
	protected boolean m_isContextMenuEnabled = true;
	protected TextView m_title = null;
	protected ImageView m_leftMenuItem = null;
	protected ImageView m_middleMenuItem = null;
	protected ImageView m_rightMenuItem = null;
	protected ImageView m_navigationBar = null;
	private List<ImageItem> m_selectedImgItems = new ArrayList<ImageItem>();
	protected final int ID_VIEW = Menu.FIRST + 1;
	protected final int ID_POST = Menu.FIRST + 2;
	protected final int ID_DELETE = Menu.FIRST + 3;
	protected final int ID_ASCENDINGSORT = Menu.FIRST + 4;
	protected final int ID_DECREASINGSORT = Menu.FIRST + 5;
	protected final int ID_LEFTROTATE = Menu.FIRST + 6;
	protected final int ID_RIGHTROTATE = Menu.FIRST + 7;
    private GifDecodeView m_decoder = null;
	protected static final int MERAGE_COMPLETE = 0;
	private Thread runningThread;
	private ProgressDialog progressDlg;
	private boolean isProgressDlgDismiss = false;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case MERAGE_COMPLETE:
				if (!isProgressDlgDismiss) {
					progressDlg.dismiss();
				}

				gotoPreviewActivity(0);
				break;
			}
		}
	};

	public Showcase(Context context, ImageAdapter imageAdapter) {
		// TODO Auto-generated constructor stub
		this.m_adapter = imageAdapter;
		this.m_context = context;
		this.m_layoutInflater = (LayoutInflater) this.m_context
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		m_titleString = m_context.getResources().getString(
				R.string.general_selected_photocount);
		m_config = new ShowCaseConfig();
	}

	public Showcase(Context context, ImageAdapter imageAdapter,
			ShowCaseConfig config) {
		this(context, imageAdapter);
		m_isContextMenuEnabled = config.isContextMenuEnabled;
		m_config = config;
	}

	public void display() {
		m_leftMenuItem.setOnClickListener(this);
		if (m_config.showcaseType == enumShowcaseType.enumShowcaseType_normal) {
			View.OnCreateContextMenuListener listener = new View.OnCreateContextMenuListener() {

				@Override
				public void onCreateContextMenu(ContextMenu menu, View v,
						ContextMenuInfo menuInfo) {
					// TODO Auto-generated method stub
					menu.add(Menu.NONE, ID_ASCENDINGSORT, Menu.NONE,
							R.string.img_operation_ascendingorder);

					menu.add(Menu.NONE, ID_DECREASINGSORT, Menu.NONE,
							R.string.img_operation_decreasingsorder);
					menu.add(Menu.NONE, ID_LEFTROTATE, Menu.NONE,
							R.string.img_operation_rotateleft);
					menu.add(Menu.NONE, ID_RIGHTROTATE, Menu.NONE,
							R.string.img_operation_rotateright);

				}
			};
			m_leftMenuItem.setOnCreateContextMenuListener(listener);
		}

		// ((Activity)m_context).registerForContextMenu(m_leftMenuItem);
		m_middleMenuItem.setOnClickListener(this);
		m_rightMenuItem.setOnClickListener(this);
		if (!m_config.isBottomToolBarVisible) {
			m_leftMenuItem.setVisibility(View.GONE);
			m_middleMenuItem.setVisibility(View.GONE);
			m_rightMenuItem.setVisibility(View.GONE);
		} else {
			if (m_config.showcaseType == enumShowcaseType.enumShowcaseType_history) {
				m_leftMenuItem
						.setBackgroundResource(R.drawable.image_preview_selector);
				//m_middleMenuItem
				//		.setBackgroundResource(R.drawable.image_share_selector);
				m_middleMenuItem.setVisibility(View.INVISIBLE);
				m_rightMenuItem
						.setBackgroundResource(R.drawable.image_delete_selector);
			}
		}
		if (m_config.isOperationBtnVisible) {
			m_operationBtn.setOnClickListener(this);
		} else {
			m_operationBtn.setVisibility(View.GONE);
		}
		m_selectionBtn.setOnCheckedChangeListener(this);
		// if (!m_config.isNavigationBarVisible)
		// {
		// m_navigationBar.setVisibility(View.INVISIBLE);
		// }
		// Activity activity = ((Activity)m_context);
		// activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
		if (null == m_adapter) {
			this.setTitle(String.format(m_titleString, 0));
			this.setOperationBtn(0);
		} else {
			int count = getSelectedItems().size();
			this.setTitle(String.format(m_titleString, count));
			this.setOperationBtn(count);
		}

		notifyDataSetChanged();
	}

	protected void setTitle(String title) {
		// Activity activity = ((Activity)m_context);
		// TextView txt = (TextView)activity.findViewById(R.id.title);
		// txt.setText(title);
		this.m_title.setText(title);
	}

	protected void setOperationBtn(int selectedCount) {
		if (null != m_operationBtn) {
			m_operationBtn.setEnabled(selectedCount > 0 ? true : false);
		}
	}

	protected void setSelectionBtn(int selectedCount) {
		if (null != m_selectionBtn) {
			if (m_adapter.getItems().size() == 0) {
				m_selectionBtn.setEnabled(false);
				return;
			} else {
				m_selectionBtn.setEnabled(true);
			}
			m_selectionBtn.setOnCheckedChangeListener(null);
			boolean isChecked = selectedCount == 0 ? false : true;
			m_selectionBtn.setChecked(isChecked);
			if (isChecked) {
				m_selectionBtn.setText(R.string.general_unselect_all);
			} else {
				m_selectionBtn.setText(R.string.general_select_all);
			}
			m_selectionBtn.setOnCheckedChangeListener(this);
		}
	}

	public void notifyDataSetChanged() {
		m_adapter.notifyDataSetChanged();
		List<ImageItem> list = getSelectedItems();
		this.setTitle(String.format(m_titleString, list.size()));
		this.setOperationBtn(list.size());
		this.setSelectionBtn(list.size());
	}

	public void selectAll() {
		// TODO Auto-generated method stub
		m_selectionBtn.setChecked(true);
	}

	public void unselectAll() {
		// TODO Auto-generated method stub
		m_selectionBtn.setChecked(false);
	}

	private void doSelection(boolean selectionMode) {
		ImageAdapter imgAdapter = (ImageAdapter) m_adapter;
		List<ImageItem> items = imgAdapter.getItems();
		for (ImageItem imageItem : items) {
			imageItem.setChecked(selectionMode);
		}
		notifyDataSetChanged();
	}

	public void displaySelection() {
		ImageAdapter imgAdapter = (ImageAdapter) m_adapter;
		imgAdapter.setCheckable(true);
		imgAdapter.notifyDataSetChanged();
	}

	public List<ImageItem> getSelectedItems() {
		// TODO Auto-generated method stub
		List<ImageItem> selectedItems = new ArrayList<ImageItem>();
		List<ImageItem> items = ((ImageAdapter) m_adapter).getItems();
		for (ImageItem imageItem : items) {
			if (imageItem.isChecked())
				selectedItems.add(imageItem);
		}
		return selectedItems;
	}

	public enumSelectionState getSelectionState() {
		enumSelectionState bRel = enumSelectionState.enumSelectionState_none;
		List<ImageItem> selectedItems = getSelectedItems();
		if (selectedItems.size() == m_adapter.getCount()) {
			bRel = enumSelectionState.enumSelectionState_unselectall;
		} else if (selectedItems.size() >= 0
				&& selectedItems.size() < m_adapter.getCount()) {
			bRel = enumSelectionState.enumSelectionState_selectall;
		} else {
			bRel = enumSelectionState.enumSelectionState_none;
		}

		return bRel;
	}

	public void removeItem(int position) {
		if (null != m_adapter) {
			m_adapter.removeItem(position);
			notifyDataSetChanged();
		}
	}

	public void removeAllItem() {
		if (null != m_adapter) {
			m_adapter.removeAllItem();
			notifyDataSetChanged();
		}
	}

	public void removeItem(ImageItem item) {
		if (null != m_adapter) {
			m_adapter.removeItem(item);
			notifyDataSetChanged();
		}
	}

	public void removeSelectedItems() {
		List<ImageItem> list = this.getSelectedItems();
		for (ImageItem imageItem : list) {
			removeItem(imageItem);
		}
		m_adapter.setSelectedPosition(-1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == m_leftMenuItem) {
			doWork(0);
		} else if (v == m_middleMenuItem) {
			doWork(1);
		} else if (v == m_rightMenuItem) {
			doWork(2);
		} else if (v == m_operationBtn) {
			doWork(3);
		}
	}

	private void doWork(int menuIndex) {
		switch (menuIndex) {
		case 0:
			if (m_config.showcaseType == enumShowcaseType.enumShowcaseType_history) {
				preview();
			} else {
				// gotoHistoryActivity();
				m_leftMenuItem.showContextMenu();
			}
			break;
		case 1:
			if (m_config.showcaseType == enumShowcaseType.enumShowcaseType_history) {
				share();
			} else {
				gotoCamera();
			}
			break;
		case 2:
			if (m_config.showcaseType == enumShowcaseType.enumShowcaseType_history) {
				delete();
			} else {
				gotoSetting();
			}
			break;
		case 3:
			if (m_config.showcaseType == enumShowcaseType.enumShowcaseType_history) {
				Merge();
			}
		    else{
		    	gotoPreviewActivity();
		    }

		default:
			break;
		}
	}
	
	private void Merge() {
		if (this.getSelectedItems().size()>2)
		{
			String content = m_context.getResources().getString(R.string.general_OnlyTwoImg);

			Toast.makeText(m_context, content, Toast.LENGTH_LONG).show();
			return;
		}
		
		if (this.getSelectedItems().size() < 2)
		{
			String content = m_context.getResources().getString(R.string.general_SelectTwoImg);

			Toast.makeText(m_context, content, Toast.LENGTH_LONG).show();
			return;
		}
		
		String title = m_context.getResources().getString(R.string.general_merge);
		String content = m_context.getResources().getString(R.string.general_inprocess);

		progressDlg = ProgressDialog.show(m_context, title, content, true);
		
		runningThread = new Thread() {
			public void run() {
				try {
					LinkSelectedItems();
				} catch (Exception e) {
				}

				Message msg = new Message();
				msg.what = MERAGE_COMPLETE;
				handler.sendMessage(msg);
			}
		};
		
		runningThread.start();
	}
	
	public void LinkSelectedItems() {
		List<ImageItem> list = this.getSelectedItems();
		
		m_decoder = new GifDecodeView();
		m_decoder.LinkSelectedItems(list);
	}
	
	private void gotoHistoryActivity()
	{
		Intent intent = new Intent();
		intent.putExtra(ActivityActions.EXTRA_HISTORY_SOUCEPATH,
				FileHelper.getAppHistoryPath());
		intent.setAction(ActivityActions.ACTION_HISTORY);
		m_context.startActivity(intent);
	}

	private void gotoCamera() {
		Intent intent = new Intent();
		int picCount = SettingsHelper.getCameraDuration();
		intent.putExtra(ActivityActions.EXTRA_PREVIEW_CAPTURE_COUNT, picCount);
		intent.putExtra(
				ActivityActions.EXTRA_PREVIEW_CAPTURE_STORAGE_ROOT_PATH,
				FileHelper.getNextAppTempPath());
		intent.setAction(ActivityActions.ACTION_PREVIEW_CAPTURE);
		m_context.startActivity(intent);
		((Activity) m_context).finish();
	}

	private void preview() {
		if (m_adapter.getSelectedPosition() == -1)
			return;
		ImageItem imageItem = (ImageItem) m_adapter.getItem(m_adapter
				.getSelectedPosition());
		Intent intent = new Intent();
		intent.putExtra(PreviewActvity.SOURCEPATH, imageItem.getFile()
				.getAbsolutePath());
		intent.setClass(m_context, PreviewActvity.class);
		((Activity) m_context).startActivityForResult(intent,
				ActivityJump.GIF_PREVIEW);
	}

	private void share() {
		/*if (m_adapter.getSelectedPosition() == -1)
			return;
		ImageItem imageItem = (ImageItem) m_adapter.getItem(m_adapter
				.getSelectedPosition());
		Intent intent = new Intent();
		intent.putExtra(PreviewActvity.SOURCEPATH, imageItem.getFile()
				.getAbsolutePath());
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		Uri screenshotUri =  FileHelper.getMediaStoreUri(m_context, imageItem.getFile().getAbsolutePath()); 
		sharingIntent.setType("image/gif");
		sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
		m_context.startActivity(Intent.createChooser(sharingIntent,
				"Share image using"));*/
	}

	private void gotoSetting() {
		Intent intent = new Intent();
		intent.setClass(m_context, SettingActivity.class);
		m_context.startActivity(intent);
	}

	private void delete() {
		if (this.getSelectedItems().size() == 0)
			return;
		AlertDialog.Builder builder = new Builder(m_context);
		builder.setTitle(R.string.general_delete)
				.setMessage(R.string.general_delete_alert)
				.setPositiveButton(R.string.general_delete,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								removeSelectedItems();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).create().show();
	}
	private void gotoPreviewActivity(int type) {
		File sdDirFile = android.os.Environment.getExternalStorageDirectory();
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			Log.i(this.getClass().getName(),
					"state of " + sdDirFile.getAbsolutePath() + "is " + state
							+ " can't do process");
			return;
		}
		if (sdDirFile.exists() && sdDirFile.canWrite()) {
			File historyDirFile = new File(FileHelper.getAppHistoryPath());
			historyDirFile.mkdirs();
			if (historyDirFile.exists() && historyDirFile.canWrite()) {
				m_selectedImgItems.clear();
				try {
					List<ImageItem> listItems = getSelectedItems();
					Intent intent = ((Activity) m_context).getIntent();
					int maxCount = intent.getBooleanExtra(
							ActivityActions.EXTRA_IS_FROM_VIDEO_CAPTURE, false) ? SettingsHelper
							.getVideoDuration() : SettingsHelper
							.getCameraDuration();
					if (listItems.size() > maxCount) {
						String message = String.format(
								m_context.getResources().getString(
										R.string.img_operation_toomuchfiles),
										maxCount);
						AlertDialog.Builder builder = new Builder(m_context);
						builder.setTitle(R.string.general_warning)
								.setMessage(message)
								.setNeutralButton(R.string.ok,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub

											}
										}).create().show();
						return;

					}
					for (ImageItem imageItem : listItems) {
						m_selectedImgItems.add(imageItem);
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.i(this.getClass().getName(),
							"get selected items exception: " + e.getMessage());
				}

				if (m_selectedImgItems.isEmpty())
					return;

				GenerateData data = new GenerateData();
                for(int i = 0; i < m_decoder.GetLinkImgSize(); i++)
                {
					data.addItem(m_decoder.GetLinkImgPath(i), 0);
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable(GifGenerateActivity.PATHARRAY, data);
				intent.putExtras(bundle);
				intent.setClass(m_context, GifGenerateActivity.class);
				((Activity) m_context).startActivityForResult(intent,
						ActivityJump.GIF_PREVIEW);
			}
		}
	}
	private void gotoPreviewActivity() {
		File sdDirFile = android.os.Environment.getExternalStorageDirectory();
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			Log.i(this.getClass().getName(),
					"state of " + sdDirFile.getAbsolutePath() + "is " + state
							+ " can't do process");
			return;
		}
		if (sdDirFile.exists() && sdDirFile.canWrite()) {
			File historyDirFile = new File(FileHelper.getAppHistoryPath());
			historyDirFile.mkdirs();
			if (historyDirFile.exists() && historyDirFile.canWrite()) {
				m_selectedImgItems.clear();
				try {
					List<ImageItem> listItems = getSelectedItems();
					Intent intent = ((Activity) m_context).getIntent();
					int maxCount = intent.getBooleanExtra(
							ActivityActions.EXTRA_IS_FROM_VIDEO_CAPTURE, false) ? SettingsHelper
							.getVideoDuration() : SettingsHelper
							.getCameraDuration();
					if (listItems.size()> maxCount)
					{
						String message = String.format(
								m_context.getResources().getString(
										R.string.img_operation_toomuchfiles),
										maxCount);
						AlertDialog.Builder builder = new Builder(m_context);
						builder.setTitle(R.string.general_warning)
								.setMessage(message)
								.setNeutralButton(R.string.ok,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub

											}
										}).create().show();
						return;
								
					}
					for (ImageItem imageItem : listItems) {
						m_selectedImgItems.add(imageItem);
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.i(this.getClass().getName(),
							"get selected items exception: " + e.getMessage());
				}

				if (m_selectedImgItems.isEmpty())
					return;


				GenerateData data = new GenerateData();
				int count = m_selectedImgItems.size();
				for (ImageItem item : m_selectedImgItems) {
					data.addItem(item.getFile().getAbsolutePath(),
							item.getRotateDegree());
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable(GifGenerateActivity.PATHARRAY, data);
				intent.putExtras(bundle);
				intent.setClass(m_context, GifGenerateActivity.class);
				((Activity) m_context).startActivityForResult(intent,
						ActivityJump.GIF_PREVIEW);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		ImageItem imageItem = null;
		Intent intent = new Intent();

		switch (item.getItemId()) {
		case ID_VIEW:

			if (m_adapter.getSelectedPosition() != -1) {
				imageItem = (ImageItem) m_adapter.getItem(menuInfo.position);
				intent.putExtra(PreviewActvity.SOURCEPATH, imageItem.getFile()
						.getAbsolutePath());
			}
			intent.setClass(m_context, PreviewActvity.class);
			((Activity) m_context).startActivityForResult(intent,
					ActivityJump.GIF_PREVIEW);
			break;
		/*case ID_POST:
			if (m_adapter.getSelectedPosition() != -1) {
				imageItem = (ImageItem) m_adapter.getItem(menuInfo.position);
				intent.putExtra(PreviewActvity.SOURCEPATH, imageItem.getFile()
						.getAbsolutePath());
			}
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			Uri screenshotUri =  FileHelper.getMediaStoreUri(m_context, imageItem.getFile().getAbsolutePath()); 
			sharingIntent.setType("image/gif");
			sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
			m_context.startActivity(Intent.createChooser(sharingIntent,
					"Share image using"));

			break;*/
		case ID_DELETE:
			if (m_adapter.getSelectedPosition() != -1) {
				imageItem = (ImageItem) m_adapter.getItem(menuInfo.position);
				intent.putExtra(PreviewActvity.SOURCEPATH, imageItem.getFile()
						.getAbsolutePath());
			}
			AlertDialog.Builder builder = new Builder(m_context);
			builder.setTitle(R.string.general_delete)
					.setMessage(R.string.general_delete_alert)
					.setPositiveButton(R.string.general_delete,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									if (m_adapter.removeItem(menuInfo.position)) {
										notifyDataSetChanged();
									}
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).create().show();

			break;
		case ID_ASCENDINGSORT:
			m_adapter.sortAscending();
			break;
		case ID_DECREASINGSORT:
			m_adapter.sortDecreasing();
			break;
		case ID_LEFTROTATE:
			List<ImageItem> listImageItems = getSelectedItems();
			for (ImageItem selectedItem : listImageItems) {
				selectedItem.postRotate(90);
			}
			notifyDataSetChanged();
			break;
		case ID_RIGHTROTATE:
			List<ImageItem> listItems = getSelectedItems();
			for (ImageItem selectedItem : listItems) {
				selectedItem.postRotate(-90);
			}
			notifyDataSetChanged();
			break;
		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		if (!m_isContextMenuEnabled)
			return;
		menu.add(Menu.NONE, ID_VIEW, Menu.NONE, m_context.getResources()
				.getString(R.string.general_preview));
		menu.add(Menu.NONE, ID_DELETE, Menu.NONE, m_context.getResources()
				.getString(R.string.general_delete));
		menu.add(Menu.NONE, ID_POST, Menu.NONE, m_context.getResources()
				.getString(R.string.maintab_share));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		doSelection(isChecked);
		if (isChecked) {
			buttonView.setText(R.string.general_unselect_all);
		} else {
			buttonView.setText(R.string.general_select_all);
		}
	}
	
	public void SetOperationBtnTxt(int resid)
	{
		if(m_operationBtn != null){
		    m_operationBtn.setText(resid);
		}
	}
}
