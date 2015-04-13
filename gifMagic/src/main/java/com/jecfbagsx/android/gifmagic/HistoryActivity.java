package com.jecfbagsx.android.gifmagic;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import com.jecfbagsx.android.data.ImageItem;
import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.gifmagic.Showcase.ShowCaseConfig;
import com.jecfbagsx.android.gifmagic.Showcase.enumSelectionState;
import com.jecfbagsx.android.gifmagic.Showcase.enumShowcaseType;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.ActivityJump;
import com.jecfbagsx.android.utils.ImageAdapter;


public class HistoryActivity extends Activity {

	private Showcase showcase = null;
	public static final String SOURCEPATH = "SOUCEPATH";
	private static final int ID_SELECT = Menu.NONE;
	private static final int ID_DELETE = Menu.FIRST;
	private enumSelectionState m_enumSelectionState = enumSelectionState.enumSelectionState_selectall;
	private List<ImageItem> m_items = new ArrayList<ImageItem>();
	private String[] m_supportedFileTypeStrings = { ".gif" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String pathString = bundle
				.getString(ActivityActions.EXTRA_HISTORY_SOUCEPATH);
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
						return Long.valueOf(f2.lastModified()).compareTo(
								Long.valueOf(f1.lastModified()));
					} catch (Exception e) {
						// TODO: handle exception
						return -1;
					}
				}
			});

			if (null != imageFiles) {
				for (File file : imageFiles) {
					ImageItem item = new ImageItem(file);
					m_items.add(item);
				}
			}

		}

		ImageAdapter imageAdapter = new ImageAdapter(this, m_items, false);
		showcase = new GridViewShowcase(this, imageAdapter, new ShowCaseConfig(
				false, false, true, true, enumShowcaseType.enumShowcaseType_history));
		
		showcase.display();
		showcase.SetOperationBtnTxt(R.string.general_merge);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
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
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		m_enumSelectionState = showcase.getSelectionState();
		MenuItem selectItem = menu.getItem(ID_SELECT);
		if (enumSelectionState.enumSelectionState_selectall == m_enumSelectionState) {
			selectItem.setTitle(R.string.general_select_all).setIcon(
					android.R.drawable.ic_menu_agenda);
		} else if (enumSelectionState.enumSelectionState_unselectall == m_enumSelectionState) {
			selectItem.setTitle(R.string.general_unselect_all).setIcon(
					android.R.drawable.ic_menu_close_clear_cancel);
		} else {
			selectItem.setTitle(R.string.general_select_items);
		}

		MenuItem deleteItem = menu.getItem(ID_DELETE);
		deleteItem.setEnabled(showcase.getSelectedItems().size() > 0 ? true
				: false);

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
//		menu.add(Menu.NONE, ID_SELECT, Menu.NONE, R.string.general_select_items)
//				.setIcon(android.R.drawable.ic_menu_agenda);
//
//		menu.add(Menu.NONE, ID_DELETE, Menu.NONE, R.string.general_delete)
//				.setIcon(android.R.drawable.ic_menu_delete);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case ID_DELETE:
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(R.string.general_delete)
					.setMessage(R.string.general_delete_alert)
					.setPositiveButton(R.string.general_delete,
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									showcase.removeSelectedItems();
								}
							})
					.setNegativeButton(R.string.cancel, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					}).create().show();
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
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		showcase.onContextItemSelected(item);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ActivityJump.GIF_PREVIEW) {
			if (resultCode == RESULT_OK) {
				if (data.getBooleanExtra(ActivityJump.CLOSE_YOURSELF, false)) {
					finish();
				}
			}
		}
	}
}
