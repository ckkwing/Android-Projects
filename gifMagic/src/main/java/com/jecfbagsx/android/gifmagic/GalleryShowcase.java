package com.jecfbagsx.android.gifmagic;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

import com.jecfbagsx.android.data.ImageItem;
import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.utils.GestureProcessor;
import com.jecfbagsx.android.utils.ImageAdapter;

public class GalleryShowcase extends Showcase implements
		AdapterView.OnItemSelectedListener, ViewFactory, OnTouchListener {

	private ImageSwitcher m_imageSwitcher = null;
	private Gallery m_gallery = null;
	private GestureDetector m_gestureDetector;

	public GalleryShowcase(Context context, ImageAdapter imageAdapter) {
		super(context, imageAdapter);
		this.m_layoutResId = R.layout.image_selection_gallery_view;
		// this.m_adapter = new ImageAdapter(this.m_context);
		GestureProcessor gestureListener = new GestureProcessor(this.m_context,
				this);
		m_gestureDetector = new GestureDetector(gestureListener);
	}

	@Override
	public void display() {
		// TODO Auto-generated method stub
		View view = this.m_layoutInflater.inflate(m_layoutResId, null);
		((Activity) m_context).setContentView(view);
		m_imageSwitcher = (ImageSwitcher) view.findViewById(R.id.switcher);

		if (null != m_imageSwitcher) {
			m_imageSwitcher.setFactory((ViewFactory) this);
			m_imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(
					this.m_context, android.R.anim.slide_in_left));
			m_imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(
					this.m_context, android.R.anim.slide_out_right));
			m_imageSwitcher.setOnTouchListener(this);
			m_imageSwitcher.setLongClickable(true);
		}

		m_gallery = (Gallery) view.findViewById(R.id.gallery);
		if (null != m_gallery) {
			m_gallery.setAdapter(m_adapter);
			m_gallery.setOnItemSelectedListener(this);
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if (null != m_imageSwitcher) {
			ImageItem item = (ImageItem) (((ImageAdapter) m_adapter)
					.getItem(arg2));
			File file = item.getFile();
			Uri imgUri = Uri.fromFile(file);
			m_imageSwitcher.setImageURI(imgUri);

			item.setChecked(item.isChecked() ? false : true);
			m_adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		ImageView i = new ImageView(this.m_context);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return i;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		boolean rel = m_gestureDetector.onTouchEvent(event);
		return rel;
	}

	public void setNextPage() {
		this.m_gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
	}

	public void setPreviousPage() {
		this.m_gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
	}
}
