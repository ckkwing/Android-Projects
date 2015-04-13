package com.jecfbagsx.android.gifmagic;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.gifmanage.GifView;
import com.jecfbagsx.android.gifmanage.OAuthManager;
import com.jecfbagsx.android.utils.ActivityJump;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.SettingsHelper;
import com.jecfbagsx.android.utils.SingleGifMediaScanner;

public class PreviewActvity extends Activity implements ViewFactory,
		OnClickListener {

	// private ImageView m_imgView = null;
	protected static final int MEDIA_SCAN_COMPLETED = 0;
	public static final String SOURCEPATH = "SOURCEPATH";
	private static final int WEIBO_FINISHED = 0;
	private TextView m_title = null;
	private Button m_operationBtnButton = null;
	private GifView m_gifView = null;
	private File m_file = null;
	private Button m_SinaImage = null;
	private Button m_mailImage = null;
	private ProgressDialog progressDlg;
	private Thread runningThread;
	private Uri mFileUri;
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case MEDIA_SCAN_COMPLETED:
				progressDlg.dismiss();
				showShareIntent();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gif_preview);

		String path = getIntent().getStringExtra(PreviewActvity.SOURCEPATH);
		m_file = new File(path);

		m_gifView = (GifView) findViewById(R.id.gifPreview);
		m_title = (TextView) findViewById(R.id.previewTitle);
		m_operationBtnButton = (Button) findViewById(R.id.previewOperationBtn);
		m_SinaImage = (Button) findViewById(R.id.sinaImage);
		m_mailImage = (Button) findViewById(R.id.mailImage);
		m_SinaImage.setOnClickListener(sinaImageLinstener);
		m_mailImage.setOnClickListener(tencentImageLinstener);
		
		if (null != m_title && null != m_operationBtnButton
				&& null != m_gifView) {
			try {
				// gf1.setShowDimension(120, 80);
				FileInputStream isStream = new FileInputStream(path);
				
				
//				Bitmap bitmap = FileHelper.getRefinedBitmap(path, FileHelper.MINSIDELENGTH, 256*256);
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				bitmap.compress(CompressFormat.PNG, 100, baos);
//
//				InputStream is = new ByteArrayInputStream(baos.toByteArray());
				
				m_gifView.setGifImage(isStream);
				
				m_title.setText(m_file.getName());
				m_operationBtnButton.setText(R.string.maintab_share);
				m_operationBtnButton.setOnClickListener(this);
			} catch (Exception e) {
			}

		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		m_gifView.stopGifDecoder();
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
	public View makeView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra(PreviewActvity.SOURCEPATH, m_file.getAbsolutePath());
		intent.setClass(this, WeiboEditActivity.class);
		PreviewActvity.this.startActivityForResult(intent, WEIBO_FINISHED);
	}

	private ImageView.OnClickListener sinaImageLinstener = new ImageView.OnClickListener() {
		public void onClick(View arg0) {
			Log.i(this.getClass().getName(),
					"sina weibo image button clicked...");
			OAuthManager.getInstance().setMode(OAuthManager.SINA);
			gotoWeiboEditActivity();
		}
	};

	private ImageView.OnClickListener tencentImageLinstener = new ImageView.OnClickListener() {
		public void onClick(View arg0) {
			mFileUri = FileHelper.getMediaStoreUri(PreviewActvity.this,
					m_file.getAbsolutePath());
			if (mFileUri == null) {
				String title = getResources().getString(
						R.string.scanning);
				progressDlg = ProgressDialog.show(PreviewActvity.this,
						title, null, true);
				runningThread = new Thread() {
					public void run() {
						try {
							scanMedia();
						} catch (Exception e) {
						}

						Message msg = new Message();
						msg.what = MEDIA_SCAN_COMPLETED;
						handler.sendMessage(msg);
					}
				};
				runningThread.start();
			}else
			{
				showShareIntent();
			}
		}
	};

	private void gotoWeiboEditActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString(SettingsHelper.GIF_FILE_LOCATION,
				m_file.getAbsolutePath());
		bundle.putBoolean(SettingsHelper.GIF_FILE_ISCREATED, true);
		intent.setClass(PreviewActvity.this, WeiboEditActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, WEIBO_FINISHED);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == WEIBO_FINISHED) {
			if (resultCode == RESULT_OK) {
				if (data.getBooleanExtra(ActivityJump.CLOSE_YOURSELF, false)) {
					setResult(RESULT_OK, data);
					finish();
				}
			}
		}
	}
	
	private void scanMedia() {
		int icount = 0;
		SingleGifMediaScanner scanner = new SingleGifMediaScanner(this,
				m_file.getAbsolutePath());
		while (!scanner.isScanCompleted()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			icount++;
			if (icount > 60) {
				break;
			}
		}

		if (scanner.isScanCompleted()) {
			mFileUri = scanner.getMediaUri();
			if (mFileUri == null) {
				mFileUri = Uri.parse(m_file.getAbsolutePath());
			}
		}
	}

	private void showShareIntent() {
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		Uri screenshotUri = mFileUri;
		sharingIntent.setType("image/gif");
		sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
		startActivity(Intent.createChooser(sharingIntent,
				"Share image using"));
	}

}
