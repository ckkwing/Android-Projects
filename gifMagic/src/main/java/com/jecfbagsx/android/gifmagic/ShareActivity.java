package com.jecfbagsx.android.gifmagic;

import java.io.File;

import com.jecfbagsx.android.data.ImageItem;
import com.jecfbagsx.android.data.ResolutionInfo;
import com.jecfbagsx.android.gifmagic.service.CommandReceiver;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.SingleGifMediaScanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ShareActivity extends Activity {

	protected static final int MEDIA_SCAN_COMPLETED = 0;
	private String m_GifLocation = "";
	private boolean m_IsGifGenerated = false;
	private TextView m_title = null;
	private ProgressBar m_progressBar = null;
	private TextView m_rate = null;
	private TextView m_detail = null;
	private ImageView m_shareImage = null;
	private ProgressReceiver m_ProgressReceiver = null;

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

	private class ProgressReceiver extends CommandReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String path = intent
					.getStringExtra(CommandReceiver.MESSAGE_CREATEGIFPATH_STRING);
			if (m_GifLocation.equals(path)) {
				int iProgress = (int) (100 * intent.getDoubleExtra(
						CommandReceiver.MESSAGE_CREATEGIFPROCESSRATE_DOUBLE, 0));
				m_IsGifGenerated = intent.getBooleanExtra(
						CommandReceiver.MESSAGE_FINISHPROCESS_BOOL, false);
				if (m_IsGifGenerated) {
					updateUI(100);
				} else {
					updateUI(iProgress);
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share_view);

		m_ProgressReceiver = new ProgressReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommandReceiver.ACTION_COMMANDRECEIVER);
		this.getApplicationContext().registerReceiver(m_ProgressReceiver,
				filter);

		Bundle bundle = this.getIntent().getExtras();
		m_GifLocation = bundle.getString(ActivityActions.EXTRA_SHARE_SOUCEPATH);
		m_IsGifGenerated = bundle.getBoolean(
				ActivityActions.EXTRA_SHARE_GIF_FILE_ISCREATED, false);

		m_title = (TextView) findViewById(R.id.shareTitle);
		File file = new File(m_GifLocation);
		m_title.setText(file.getName());
		m_progressBar = (ProgressBar) findViewById(R.id.shareProcessBar);
		m_shareImage = (ImageView) findViewById(R.id.shareImage);
		m_rate = (TextView) findViewById(R.id.shareRate);
		m_detail = (TextView) findViewById(R.id.shareDetail);
		m_shareImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mFileUri = FileHelper.getMediaStoreUri(ShareActivity.this,
						m_GifLocation);
				if (mFileUri == null) {
					String title = getResources().getString(
							R.string.scanning);
					progressDlg = ProgressDialog.show(ShareActivity.this,
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
		});
	}

	private void scanMedia() {
		int icount = 0;
		SingleGifMediaScanner scanner = new SingleGifMediaScanner(this,
				m_GifLocation);
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
				mFileUri = Uri.parse(m_GifLocation);
			}
		}
	}

	private void showShareIntent() {
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		Uri screenshotUri = mFileUri;
		sharingIntent.setType("image/gif");
		sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
		ShareActivity.this.startActivity(Intent.createChooser(sharingIntent,
				"Share image using"));
	}

	private void updateUI(int rate) {
		if (100 == rate) {
			m_progressBar.setProgress(100);
			m_rate.setText("100%");
			m_shareImage.setVisibility(View.VISIBLE);
			updateFileInfo();
		} else {
			m_progressBar.setProgress(rate);
			m_rate.setText(rate + "%");
		}
	}

	private void updateFileInfo() {
		File gifFile = new File(m_GifLocation);
		long fileSize = 0;
		if (gifFile != null) {
			fileSize = gifFile.length();
		}
		ResolutionInfo res = FileHelper.getImageResolution(m_GifLocation);
		String fileSizeStr = FileHelper.convertFileSize(fileSize);
		m_detail.setText("(" + res.FormatRes() + ")" + fileSizeStr);
	}

}
