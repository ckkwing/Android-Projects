package com.jecfbagsx.android.gifmagic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.jecfbagsx.android.data.GenerateData;
import com.jecfbagsx.android.data.IImageConverter;
import com.jecfbagsx.android.data.ImageSolutionFactory;
import com.jecfbagsx.android.data.ResolutionInfo;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.ActivityJump;
import com.jecfbagsx.android.utils.DataManger;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.BitmapRecycleThread;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class PhotoEditActivity extends Activity {

	private Intent m_intent;
	private GenerateData m_data;
	private Bitmap m_bitmap = null;
	private ImageView m_colorPaletteImg = null;
	private ImageView m_displayImg = null;
	private ImageView m_approveImg = null;
	private ImageView m_rejectimg = null;
	private ProgressDialog m_proProgressDialog = null;
	private static final int PROCESS_APPROVED = 1;
	private boolean m_isGroup = false;
	private String m_savedPath = "";
	private String CHILDFOLDERTITLE = "GifMagic_Edit_";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo_edit_default_view);

		m_displayImg = (ImageView) findViewById(R.id.displayImg);
		m_colorPaletteImg = (ImageView) findViewById(R.id.colorEditor);
		m_colorPaletteImg.setOnClickListener(colorPaletteImageLinstener);
		m_approveImg = (ImageView) findViewById(R.id.approveImg);
		m_approveImg.setOnClickListener(approveLinstenerListener);
		m_rejectimg = (ImageView) findViewById(R.id.rejectImg);
		m_rejectimg.setOnClickListener(rejectLinstenerListener);

		m_intent = getIntent();
		m_isGroup = m_intent.getBooleanExtra(
				ActivityActions.EXTRA_PHOTO_EDIT_ISGROUP, false);
		m_data = DataManger.getInstance().getGenerateData();
		if (m_data == null) {
			m_data = new GenerateData();
		}

		Map<String, Float> map = m_data.getDegreeMap();
		if (!map.isEmpty()) {
			String path = map.keySet().iterator().next();
			m_bitmap = FileHelper.getBitmapAutomatically(path);
		}

		m_displayImg.setImageBitmap(m_bitmap);

		updateUI();
	}

	private void updateUI() {
		float[] colorMatrixArray = m_data.getColorMatrixArray();
		m_displayImg.clearColorFilter();
		m_displayImg.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(
				colorMatrixArray)));
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			if (m_proProgressDialog.isShowing()) {
				m_proProgressDialog.dismiss();
			}
			switch (message.what) {
			case PROCESS_APPROVED:
				m_intent.putExtra(ActivityActions.EXTRA_PHOTO_EDIT_SAVEDPATH,
						m_savedPath);
				setResult(RESULT_OK, m_intent);
				finish();
				break;
			default:
				break;
			}
		}
	};

	public void saveBitmap() throws IOException {
		java.util.Date date = new java.util.Date();
		m_savedPath = FileHelper.getAppEditPath() + File.separator + CHILDFOLDERTITLE
				+ java.text.DateFormat.getDateInstance().format(date);
		File newPath = new File(m_savedPath);
		if (!newPath.exists()) {
			newPath.mkdirs();
		}

		String[] filePathStrings = new String[m_data.getDegreeMap().keySet()
				.size()];
		int i = 0;
		for (Iterator<String> iterator = m_data.getDegreeMap().keySet()
				.iterator(); iterator.hasNext();) {
			String url = iterator.next();

			Bitmap tempBitmap = null;
			Bitmap bitmap = null;

			try {
				tempBitmap = FileHelper.getBitmapAutomatically(url);
				bitmap = Bitmap.createBitmap(tempBitmap.getWidth(),
						tempBitmap.getHeight(), tempBitmap.getConfig());

				Paint paint = new Paint();
				paint.setColorFilter(new ColorMatrixColorFilter(
						new ColorMatrix(m_data.getColorMatrixArray())));
				Canvas canvas = new Canvas(bitmap);
				canvas.drawBitmap(tempBitmap, 0, 0, paint);
			} catch (OutOfMemoryError e) {
				// TODO: handle exception
				e.printStackTrace();
				if (bitmap != null) {
					BitmapRecycleThread thread = new BitmapRecycleThread(bitmap);
					thread.start();
					thread.waitInfinite();
				}
				if (tempBitmap != null) {
					BitmapRecycleThread thread = new BitmapRecycleThread(
							tempBitmap);
					thread.start();
					thread.waitInfinite();
				}
				List<String> list = new ArrayList<String>();
				list.add(url);
				IImageConverter iImageConverter = ImageSolutionFactory
						.getImageSolution(list);
				ResolutionInfo info = iImageConverter.getTargetResolution();
				tempBitmap = FileHelper.getRefinedBitmap(url,
						FileHelper.MINSIDELENGTH,
						info.getWidth() * info.getHeight());
				bitmap = Bitmap.createBitmap(info.getWidth(),
						info.getHeight(), tempBitmap.getConfig());
				// continue;
			}

			String newFilePath = m_savedPath + File.separator
					+ System.currentTimeMillis() + ".jpg";
			filePathStrings[i] = newFilePath;
			File f = new File(newFilePath);
			f.createNewFile();
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			try {
				fOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (bitmap != null) {
				BitmapRecycleThread thread = new BitmapRecycleThread(bitmap);
				thread.start();
				thread.waitInfinite();
			}
			if (tempBitmap != null) {
				BitmapRecycleThread thread = new BitmapRecycleThread(tempBitmap);
				thread.start();
				thread.waitInfinite();
			}
			i++;
		}

		m_data.clear();
		for (int j = 0; j < filePathStrings.length; j++) {
			m_data.addItem(filePathStrings[j], 0);
		}
	}

	private ImageView.OnClickListener approveLinstenerListener = new ImageView.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			m_proProgressDialog = ProgressDialog.show(PhotoEditActivity.this,
					getResources().getString(R.string.general_inprocess),
					getResources().getString(R.string.please_wait));
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						saveBitmap();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Message message = new Message();
					message.what = PROCESS_APPROVED;
					handler.sendMessage(message);
				}
			}).start();
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

	private ImageView.OnClickListener colorPaletteImageLinstener = new ImageView.OnClickListener() {
		public void onClick(View arg0) {
			if (m_isGroup) {
				AlertDialog.Builder builder = new Builder(
						PhotoEditActivity.this);
				String message = getResources().getString(
						R.string.photoedit_groupalert);
				builder.setTitle(R.string.general_warning)
						.setMessage(message)
						.setNeutralButton(R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method
										// stub
										gotoColorPalette();
									}
								}).create().show();
			} else {
				gotoColorPalette();
			}
		}

	};

	private void gotoColorPalette() {
		Intent intent = new Intent();
		intent.setAction(ActivityActions.ACTION_PHOTO_COLORPALETTE);
		Bundle bundle = new Bundle();
		bundle.putSerializable(
				ActivityActions.EXTRA_PHOTO_COLORPALETTE_SOURCEDATA, m_data);
		intent.putExtras(bundle);
		PhotoEditActivity.this.startActivityForResult(intent,
				ActivityJump.PHOTO_EDIT_COLORPALETTE);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case ActivityJump.PHOTO_EDIT_COLORPALETTE:
			if (resultCode == RESULT_OK) {
				m_data = DataManger.getInstance().getGenerateData();
				updateUI();
			}
			break;
		default:
			break;
		}
	}

}
