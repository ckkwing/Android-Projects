package com.jecfbagsx.android.gifmagic;

import java.util.Map;
import com.jecfbagsx.android.data.GenerateData;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.CustomColorMatrix;
import com.jecfbagsx.android.utils.DataManger;
import com.jecfbagsx.android.utils.FileHelper;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ColorPaletteActivity extends Activity implements
		OnSeekBarChangeListener {

	private Intent m_intent;
	private GenerateData m_data;
	private SeekBar m_brightnessBar = null;
	private SeekBar m_saturationBar = null;
	private SeekBar m_contrastBar = null;
	private ImageView m_imageView = null;
	private ImageView m_approveImg = null;
	private ImageView m_rejectimg = null;
	private CustomColorMatrix m_colorMatrix = null;
	private Bitmap m_bitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo_edit_colorpalette_view);

		m_intent = getIntent();
		Bundle bundle = m_intent.getExtras();
		m_data = (GenerateData) bundle
				.getSerializable(ActivityActions.EXTRA_PHOTO_COLORPALETTE_SOURCEDATA);

		Map<String, Float> map = m_data.getDegreeMap();
		if (!map.isEmpty()) {
			String path = map.keySet().iterator().next();
			m_bitmap = FileHelper.getBitmapAutomatically(path);
		}

		m_colorMatrix = new CustomColorMatrix(new float[] { 1,
				0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, });

		m_imageView = (ImageView) findViewById(R.id.displayImg);
		m_imageView.setImageBitmap(m_bitmap);
		m_brightnessBar = (SeekBar) findViewById(R.id.brightnessBar);
		m_brightnessBar.setOnSeekBarChangeListener(this);
		m_saturationBar = (SeekBar) findViewById(R.id.saturationBar);
		m_saturationBar.setOnSeekBarChangeListener(this);
		m_contrastBar = (SeekBar) findViewById(R.id.contrastBar);
		m_contrastBar.setOnSeekBarChangeListener(this);
		m_approveImg = (ImageView) findViewById(R.id.approveImg);
		m_approveImg.setOnClickListener(approveLinstenerListener);
		m_rejectimg = (ImageView) findViewById(R.id.rejectImg);
		m_rejectimg.setOnClickListener(rejectLinstenerListener);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (m_bitmap != null) {
			m_bitmap.recycle();
			m_bitmap = null;
		}
		super.onDestroy();
	}

	private ImageView.OnClickListener approveLinstenerListener = new ImageView.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			GenerateData data = DataManger.getInstance().getGenerateData();
			data.setColorMatrixArray(m_colorMatrix.getColorMatrixArray());
			DataManger.getInstance().setGenerateData(data);
			setResult(RESULT_OK, m_intent);
			finish();
		}

	};

	private ImageView.OnClickListener rejectLinstenerListener = new ImageView.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			setResult(RESULT_CANCELED, m_intent);
			finish();
		}

	};

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		m_colorMatrix.reset();
		m_colorMatrix.adjustColor(m_brightnessBar.getProgress() - 50 ,
				m_contrastBar.getProgress() -50, m_saturationBar.getProgress()-100, 0);
		m_imageView.clearColorFilter();
		m_imageView.setColorFilter(new ColorMatrixColorFilter(m_colorMatrix.getColorMatrixArray()));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

}
