package com.jecfbagsx.android.gifmagic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jecfbagsx.android.data.ImageItem;
import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.utils.ActivityJump;
import com.jecfbagsx.android.utils.ImageAdapter;

public class ListViewShowcase extends Showcase implements OnItemClickListener {

	private ListView m_listView = null;

	public ListViewShowcase(Context context, ImageAdapter imageAdapter) {
		// TODO Auto-generated constructor stub
		super(context, imageAdapter);
		this.m_layoutResId = R.layout.image_selection_list_view;
	}

	public ListViewShowcase(Context context, ImageAdapter imageAdapter,
			ShowCaseConfig config) {
		// TODO Auto-generated constructor stub
		super(context, imageAdapter, config);
		this.m_layoutResId = R.layout.image_selection_list_view;
	}

	@Override
	public void display() {
		// TODO Auto-generated method stub
		View view = this.m_layoutInflater.inflate(m_layoutResId, null);
		((Activity) m_context).setContentView(view);

		m_listView = (ListView) view.findViewById(R.id.imageSelectionList);
		if (null != m_listView) {
			m_listView.setAdapter(m_adapter);
			m_listView.setOnItemClickListener(this);
			registerForContextMenu(m_listView);
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

}
