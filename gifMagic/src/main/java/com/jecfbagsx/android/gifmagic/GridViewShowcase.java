package com.jecfbagsx.android.gifmagic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jecfbagsx.android.data.GenerateData;
import com.jecfbagsx.android.data.ImageItem;
import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.ActivityJump;
import com.jecfbagsx.android.utils.DataManger;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.ImageAdapter;

public class GridViewShowcase extends Showcase implements 
	OnItemClickListener, OnItemLongClickListener {

	private GridView m_gridView = null;

	public GridViewShowcase(Context context, ImageAdapter imageAdapter) {
		// TODO Auto-generated constructor stub
		super(context, imageAdapter);
		this.m_layoutResId = R.layout.image_selection_grid_view;
	}

	public GridViewShowcase(Context context, ImageAdapter imageAdapter,
			ShowCaseConfig config) {
		// TODO Auto-generated constructor stub
		super(context, imageAdapter, config);
		this.m_layoutResId = R.layout.image_selection_grid_view;
	}

	@Override
	public void display() {
		// TODO Auto-generated method stub
		View view = this.m_layoutInflater.inflate(m_layoutResId, null);
		((Activity) m_context).setContentView(view);

		m_gridView = (GridView) view.findViewById(R.id.imageSelectionGrid);
		if (null != m_gridView) {
			m_gridView.setAdapter(m_adapter);
			m_gridView.setOnItemClickListener(this);
			m_gridView.setOnItemLongClickListener(this);
			registerForContextMenu(m_gridView);
		}
		m_operationBtn = (Button) view.findViewById(R.id.showcaseOperationBtn);
		m_selectionBtn = (CheckBox)view.findViewById(R.id.showcaseSelectionBtn);

		m_title = (TextView) view.findViewById(R.id.showcaseTitle);
		m_navigationBar = (ImageView) view.findViewById(R.id.navigationBar);
		m_leftMenuItem = (ImageView) view.findViewById(R.id.folderImage);
		m_middleMenuItem = (ImageView) view.findViewById(R.id.cameraImage);
		m_rightMenuItem = (ImageView) view.findViewById(R.id.settingImage);
		super.display();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (!m_adapter.getCheckable())
			return;
		ImageItem item = (ImageItem) m_adapter.getItem(arg2);
		item.setChecked(item.isChecked() ? false : true);
		m_adapter.setSelectedPosition(arg2);
		notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		ImageItem item = (ImageItem) m_adapter.getItem(arg2);
		DataManger.getInstance().reset();
		GenerateData data = new GenerateData();
		data.addItem(item.getFile().getAbsolutePath(), 0);
		DataManger.getInstance().setGenerateData(data);
		Intent intent = new Intent();
//		intent.putExtra(ActivityActions.EXTRA_PHOTO_EDIT_ISGROUP, false);
		intent.setAction(ActivityActions.ACTION_PHOTO_EDIT);
		((Activity) m_context).startActivityForResult(intent,
				ActivityJump.PHOTO_EDIT_DEFAULT);
		return true;
	}
}
