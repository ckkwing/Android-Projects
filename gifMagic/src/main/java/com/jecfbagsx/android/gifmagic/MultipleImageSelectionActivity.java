package com.jecfbagsx.android.gifmagic;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.jecfbagsx.android.data.ImageItem;
import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.gifmagic.Showcase.enumSelectionState;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.ActivityJump;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.ImageAdapter;
import com.jecfbagsx.android.utils.ListImageAdapter;
import com.jecfbagsx.android.utils.SettingsHelper;

public class MultipleImageSelectionActivity extends Activity {


	private ProgressDialog m_proProgressDialog = null;
	private List<ImageItem> m_selectedImgItems = new ArrayList<ImageItem>();
	private Thread m_thread;
	private Showcase showcase = null;
	private enumSelectionState m_enumSelectionState = enumSelectionState.enumSelectionState_selectall;
	private boolean m_isFromCamera;
	private static final int ID_SELECT = Menu.NONE;
	private static final int ID_CREATE = Menu.FIRST;
	private static final int ID_VIEWMODE = Menu.FIRST + 1;
	private static final int PROCESS_COMPLETE = 4;
	private static final int PROCESS_REFRESH = 5;
	private static final int MAX_REFRESH_COUNT = 10;
	private int viewType = 0; // 0:grid 1:list
	private ImageAdapter imageAdapter = null;
	private List<ImageItem> m_items = new ArrayList<ImageItem>();
	private String[] m_supportedFileTypeStrings = { ".gif", ".jpg", ".png" };
	private Showcase.ShowCaseConfig m_configCaseConfig = new Showcase.ShowCaseConfig(
			false, true, true, true);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Bundle bundle = getIntent().getExtras();
		m_isFromCamera = bundle.getBoolean(ActivityActions.EXTRA_MULTIPLE_SELECTION_ISFROMCAMERA);
		
		imageAdapter = new ImageAdapter(this, m_items,m_isFromCamera);
		showcase = new GridViewShowcase(this, imageAdapter, m_configCaseConfig);
		showcase.display(); 
		
		m_proProgressDialog = ProgressDialog.show(this,
				 getResources().getString(R.string.scan_photo),
				 getResources().getString(R.string.please_wait));
				 m_thread = new Thread() {
				 public void run() {
				 try {
					 loadData();
				
				 } catch (Exception e) {
				 }
				
				 Message msg = new Message();
				 msg.what = PROCESS_COMPLETE;
				 handler.sendMessage(msg);
				 }
				 };
				 m_thread.start();
	}
	
	private void loadData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String pathString = bundle
				.getString(ActivityActions.EXTRA_MULTIPLE_SELECTION_SOUCEPATH);
		if (pathString != null || "".equals(pathString)) {
			File dirFile = new File(pathString);

			if (dirFile.exists() && dirFile.isDirectory() && dirFile.canRead()) {
				File[] imageFiles = dirFile.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String filename) {
						// TODO Auto-generated method stub
						for (String extension : m_supportedFileTypeStrings) {
							if (filename.toLowerCase().endsWith(extension))
								return true;
						}
						return false;
					}
				});

				Arrays.sort(imageFiles, new Comparator<File>() {
					public int compare(File f1, File f2) {
						try {
							String strF1 = f1.getName();
							String strF2 = f2.getName();

							int iStartF1 = strF1.indexOf("#");
							int iEndF1 = strF1.lastIndexOf(".");
							String count1 = strF1.substring(iStartF1 + 1,
									iEndF1);

							int iStartF2 = strF2.indexOf("#");
							int iEndF2 = strF2.lastIndexOf(".");
							String count2 = strF2.substring(iStartF2 + 1,
									iEndF2);
							return Long.valueOf(count1).compareTo(
									Long.valueOf(count2));
						} catch (Exception e) {
							// TODO: handle exception
							return -1;
						}
					}
				});
				
				int count = 0;
				if (null != imageFiles) {
					for (File file : imageFiles) {
						ImageItem item = new ImageItem(file);
						m_items.add(item);
						count++;
//						if (count == MAX_REFRESH_COUNT)
//						{
//							count = 0;
//							Message msg = new Message();
//							msg.what = PROCESS_REFRESH;
//							handler.sendMessage(msg);
//						}
					}
				}

			}
		}
	
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
//		try {
//			if (m_thread.isAlive())
//				m_thread.wait();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
//		synchronized (this) {
//			m_thread.notify();
//		}
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return showcase.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
//		menu.add(Menu.NONE, ID_SELECT, Menu.NONE, R.string.general_select_items)
//				.setIcon(android.R.drawable.ic_menu_agenda);
//
//		menu.add(Menu.NONE, ID_CREATE, Menu.NONE,
//				R.string.img_operation_creategif).setIcon(R.drawable.icon);
//
//		menu.add(Menu.NONE, ID_VIEWMODE, Menu.NONE,
//				R.string.general_viewmode_list).setIcon(
//				android.R.drawable.ic_menu_sort_by_size);

		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		MenuItem createItem = menu.getItem(ID_CREATE);
		createItem.setVisible(false);

		MenuItem viewmodeItem = menu.getItem(ID_VIEWMODE);
		if (0 == viewType) {
			viewmodeItem.setTitle(R.string.general_viewmode_list).setIcon(
					android.R.drawable.ic_menu_sort_by_size);
		} else if (1 == viewType) {
			viewmodeItem.setTitle(R.string.general_viewmode_grid).setIcon(
					android.R.drawable.ic_dialog_dialer);
		}

		MenuItem selectItem = menu.getItem(ID_SELECT);
		if (enumSelectionState.enumSelectionState_none == m_enumSelectionState) {
			selectItem.setTitle(R.string.general_select_items);
			return true;
		}

		m_enumSelectionState = showcase.getSelectionState();
		if (enumSelectionState.enumSelectionState_selectall == m_enumSelectionState) {
			selectItem.setTitle(R.string.general_select_all).setIcon(
					android.R.drawable.ic_menu_agenda);
		} else if (enumSelectionState.enumSelectionState_unselectall == m_enumSelectionState) {
			selectItem.setTitle(R.string.general_unselect_all).setIcon(
					android.R.drawable.ic_menu_close_clear_cancel);
		} else {
			selectItem.setTitle(R.string.general_select_items);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case ID_VIEWMODE:
			if (0 == viewType) {
				ListImageAdapter adapter = new ListImageAdapter(this, m_items,m_isFromCamera);
				List<ImageItem> selectedItems = showcase.getSelectedItems();
				for (ImageItem imageItem : selectedItems) {
					for (ImageItem newItem : adapter.getItems()) {
						if (imageItem.getFile().equals(newItem.getFile())) {
							newItem.setChecked(imageItem.isChecked());
							break;
						}
					}
				}

				showcase = new ListViewShowcase(this, adapter,
						m_configCaseConfig);
				showcase.display();
				viewType = 1;
			} else if (1 == viewType) {
				ImageAdapter adapter = new ImageAdapter(this, m_items,m_isFromCamera);
				List<ImageItem> selectedItems = showcase.getSelectedItems();
				for (ImageItem imageItem : selectedItems) {
					for (ImageItem newItem : adapter.getItems()) {
						if (imageItem.getFile().equals(newItem.getFile())) {
							newItem.setChecked(imageItem.isChecked());
							break;
						}
					}
				}

				showcase = new GridViewShowcase(this, adapter);
				showcase.display();
				viewType = 0;
			}
			break;
		case ID_SELECT:
			if (enumSelectionState.enumSelectionState_none == m_enumSelectionState) {
				showcase.displaySelection();
				m_enumSelectionState = enumSelectionState.enumSelectionState_selectall;
			} else if (enumSelectionState.enumSelectionState_selectall == m_enumSelectionState) {
				showcase.selectAll();
			} else {
				showcase.unselectAll();
			}
			break;
		case ID_CREATE:
			File sdDirFile = android.os.Environment
					.getExternalStorageDirectory();
			String state = android.os.Environment.getExternalStorageState();
			if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
				Log.i(this.getClass().getName(),
						"state of " + sdDirFile.getAbsolutePath() + "is "
								+ state + " can't do process");
				return false;
			}
			if (sdDirFile.exists() && sdDirFile.canWrite()) {
				File historyDirFile = new File(FileHelper.getAppHistoryPath());
				historyDirFile.mkdirs();
				if (historyDirFile.exists() && historyDirFile.canWrite()) {
					m_selectedImgItems.clear();
					try {
						List<ImageItem> listItems = showcase.getSelectedItems();
						for (ImageItem imageItem : listItems) {
							m_selectedImgItems.add(imageItem);
						}
					} catch (Exception e) {
						// TODO: handle exception
						Log.i(this.getClass().getName(),
								"get selected items exception: "
										+ e.getMessage());
					}

					if (m_selectedImgItems.isEmpty()) {
						AlertDialog.Builder builder = new Builder(this);
						builder.setTitle(
								getResources().getString(R.string.app_name))
								.setMessage(
										getResources()
												.getString(
														R.string.general_select_nothing))
								.setNegativeButton(
										getResources().getString(R.string.ok),
										new OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												dialog.dismiss();
											}
										}).create().show();

						return false;
					}

					String[] fileStrings = new String[m_selectedImgItems.size()];
					for (int i = 0; i < fileStrings.length; i++) {
						fileStrings[i] = ((ImageItem) m_selectedImgItems.get(i))
								.getFile().getAbsolutePath();
					}
					Intent intent = new Intent();
					intent.putExtra(GifGenerateActivity.PATHARRAY, fileStrings);
					intent.setClass(MultipleImageSelectionActivity.this,
							GifGenerateActivity.class);
					MultipleImageSelectionActivity.this.startActivityForResult(
							intent, ActivityJump.GIF_PREVIEW);
				}
			}

			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case PROCESS_COMPLETE:
				if ( m_proProgressDialog.isShowing())
					m_proProgressDialog.dismiss();
				showcase.notifyDataSetChanged();
				showcase.selectAll();
				tipAlert();
				break;
			case PROCESS_REFRESH:
				if ( m_proProgressDialog.isShowing())
					m_proProgressDialog.dismiss();
				showcase.notifyDataSetChanged();
				break;
			}
		}
	};
	
	private void tipAlert() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean firstFlag = prefs.getBoolean("MULTIPLESELECTION_TIP_ALERT",
				false);
		if (!firstFlag) {
			prefs.edit().putBoolean("MULTIPLESELECTION_TIP_ALERT", true)
					.commit();
			String message = getResources().getString(
					R.string.img_operation_tipalert);
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(R.string.general_warning)
					.setMessage(message)
					.setNeutralButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method
									// stub

								}
							}).create().show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ActivityJump.PHOTO_EDIT_DEFAULT:
			if (resultCode == RESULT_OK) {
				String source = getResources().getString(R.string.photoedit_savedtoast);
				String info = String.format(source, data.getStringExtra(ActivityActions.EXTRA_PHOTO_EDIT_SAVEDPATH));
				Toast.makeText(this, info, Toast.LENGTH_LONG).show();
			}
			break;
		case ActivityJump.GIF_PREVIEW:
			Intent intent = new Intent();
			intent.putExtra(ActivityJump.CLOSE_YOURSELF, true);
			setResult(RESULT_OK, intent);
			finish();
			break;
			
		default:
			break;
		}
	}
}
