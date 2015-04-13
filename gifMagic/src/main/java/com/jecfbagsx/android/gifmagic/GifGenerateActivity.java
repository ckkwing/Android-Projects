package com.jecfbagsx.android.gifmagic;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.jecfbagsx.android.customercontroler.AnimationImageView;
import com.jecfbagsx.android.data.GenerateData;
import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.gifmagic.service.GifService;
import com.jecfbagsx.android.gifmagic.service.IGifOperation;
import com.jecfbagsx.android.gifmanage.OAuthManager;
import com.jecfbagsx.android.utils.ActivityActions;
import com.jecfbagsx.android.utils.ActivityJump;
import com.jecfbagsx.android.utils.CustomerAnimationDrawable;
import com.jecfbagsx.android.utils.DataManger;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.LocationHelper;
import com.jecfbagsx.android.utils.SettingsHelper;

public class GifGenerateActivity extends Activity implements
	OnSeekBarChangeListener {

	enum enumPressedBtn
	{
		enumPressedBtn_None,
		enumPressedBtn_Sina,
		enumPressedBtn_Tencent,
		enumPressedBtn_Common,
	}
	
	private static final String TAG = "GifGenerateActivity";
	private ArrayList<String> m_fileStrings;
	private GenerateData m_data;
	private AnimationImageView m_imageView = null;
	private SeekBar m_seekBar = null;
	private CustomerAnimationDrawable m_animation = null;
	private int m_duration = 500;
	public static String PATHARRAY = "PATHARRAY";
	private static final int ID_CREATE = Menu.NONE;
	private ProgressDialog m_proProgressDialog = null;
	private static final int PROCESS_COMPLETE = 4;
	private static final int LOADMEDIA_FINISHED = 5;
	private static final int GETLOCATION_COMPLETED = 6;
	private static final int WEIBO_FINISHED = 0;
	private String m_currentFilePath = "";
	private Button mSinaImage = null;
	//private ImageView mTencentImage = null;
	private Button mShareImage = null;
	private IGifOperation m_iGifOperation;
	private Button m_photoEditBtn = null;
	private int MAXSIZE = 256 * 256;
	private enumPressedBtn m_lastestPressedBtn = enumPressedBtn.enumPressedBtn_None;
	private Activity m_thisActivity;
	private HashMap<Integer, SoftReference<Bitmap>> m_drawableCache = new HashMap<Integer, SoftReference<Bitmap>>();

	private ServiceConnection m_serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			m_iGifOperation = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			m_iGifOperation = (IGifOperation) service;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gif_generate_view);
		m_thisActivity = (Activity) this;
		Intent intent = new Intent();
		intent.setClass(GifGenerateActivity.this, GifService.class);
		this.getApplicationContext().bindService(intent, m_serviceConnection,
				BIND_AUTO_CREATE);

		// this.bindService(new Intent("android.intent.action.GIFSERVICE"),
		// this.m_serviceConnection, BIND_AUTO_CREATE);

		Bundle bundle = getIntent().getExtras();
		m_data = (GenerateData) bundle
				.getSerializable(GifGenerateActivity.PATHARRAY);
		
		DataManger.getInstance().reset();
		DataManger.getInstance().setGenerateData(m_data);
		
		
		m_imageView = (AnimationImageView) findViewById(R.id.gifgenerateview);
		mSinaImage = (Button) findViewById(R.id.sinaImage);
		//mTencentImage = (ImageView) findViewById(R.id.qqImage);
		mShareImage = (Button) findViewById(R.id.shareImage);
		mSinaImage.setOnClickListener(sinaImageLinstener);
		//mTencentImage.setOnClickListener(tencentImageLinstener);
		mShareImage.setOnClickListener(shareImageLinstenerListener);
		m_photoEditBtn = (Button)findViewById(R.id.edit);
		m_photoEditBtn.setOnClickListener(photoEditLinstenerListener);

		m_seekBar = (SeekBar) findViewById(R.id.durationseekbar);
		
		// if (m_fileStrings.length > 0 && null != m_imageView
		if (null != m_imageView && null != m_seekBar) {
			m_seekBar.setOnSeekBarChangeListener(this);
			m_duration = m_seekBar.getProgress() * 10;

//			LoadData();
			
//			resetAnimationImageView();
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		resetAnimationImageView();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		m_imageView.stopAnimation();
		super.onPause();
	}
	
	private void resetAnimationImageView()
	{
		m_imageView.stopAnimation();
		m_imageView.setImageSource(m_data.getDegreeMap());
		m_imageView.setDuration(m_duration);
		m_imageView.startAnimation();
	}
	
	private void LoadData()
	{
		m_proProgressDialog = ProgressDialog.show(this, getResources()
				.getString(R.string.video_select_loading_title),
				getResources().getString(R.string.please_wait));
		if ( null != m_fileStrings)
		{
			m_fileStrings = null;
		}

		m_fileStrings = new ArrayList<String>();

//		for (int i = 0; i < m_drawableCache.size(); i++) {
//			SoftReference<Bitmap> sReference = m_drawableCache.get(i);
//			if (sReference.get() != null) {
//				Bitmap bitmap = m_drawableCache.get(
//						i).get();
//				if (!bitmap.isRecycled())
//				{
//					bitmap.recycle();
//					bitmap = null;
//				}
//			}
//		}
		if (null != m_animation)
		{
			m_animation.stop();
			m_animation = null;
		}
		m_animation = new CustomerAnimationDrawable();
		m_imageView.setBackgroundDrawable(null);
		m_drawableCache.clear();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				composeMediaData();
				Message message = new Message();
				message.what = LOADMEDIA_FINISHED;
				handler.sendMessage(message);
			}
		}).start();
	}

	private void composeMediaData() {
		Bitmap bitmap = null;
		Map<String, Float> map = m_data.getDegreeMap();
		int index = 0;
		try {
			for (Iterator<String> iterator = map.keySet().iterator(); iterator
					.hasNext();) {
				String url = iterator.next();
				m_fileStrings.add(url);
				float degree = map.get(url);
				try {
					bitmap = FileHelper.getRefinedBitmap(url,
							FileHelper.MINSIDELENGTH, MAXSIZE);
					if (degree != 0) {
						Matrix mat = new Matrix();
						mat.setRotate(degree);
						Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
								bitmap.getWidth(), bitmap.getHeight(), mat, true);
						bitmap.recycle();
						bitmap = newBitmap;
					}
					m_drawableCache.put(index, new SoftReference<Bitmap>(bitmap));
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				bitmap = null;
				index++;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.i(this.getClass().getName(),
					"add frame exception:" + e.getMessage());
		}

		if (!m_drawableCache.isEmpty()) {
			for (int i = 0; i < m_drawableCache.size(); i++) {
				SoftReference<Bitmap> sReference = m_drawableCache.get(i);
				if (sReference.get() != null) {
					Drawable drawable = new BitmapDrawable(m_drawableCache.get(
							i).get());
					m_animation.addFrame(drawable, m_duration);
				} else {
					try {
						String url = "";
						float degree = 0;
						int j = 0;
						for (Iterator<String> iterator = map.keySet().iterator(); iterator
								.hasNext();) {
							url = iterator.next();
							if (j != i) {
								continue;
							}
							
							
							degree = map.get(url);
							break;
						}
						if (url.length() <= 0)
							continue;
						Bitmap newBitmap = FileHelper.getRefinedBitmap(url,
								FileHelper.MINSIDELENGTH, MAXSIZE);
						if (degree != 0) {
							Matrix mat = new Matrix();
							mat.setRotate(degree);
							Bitmap newTmepBitmap = Bitmap.createBitmap(
									newBitmap, 0, 0, newBitmap.getWidth(),
									newBitmap.getHeight(), mat, true);
							newBitmap.recycle();
							newBitmap = newTmepBitmap;
						}
						Drawable newDrawable = new BitmapDrawable(newBitmap);
						m_animation.addFrame(newDrawable, m_duration);
						newBitmap = null;

					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}

		m_animation.setDuration(m_duration);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		this.getApplicationContext().unbindService(m_serviceConnection);
		DataManger.getInstance().reset();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// menu.add(Menu.NONE, ID_CREATE, Menu.NONE,
		// R.string.img_operation_creategif).setIcon(R.drawable.icon);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case ID_CREATE:

			if (null != m_iGifOperation) {
				m_currentFilePath = FileHelper.getAppHistoryPath()
						+ File.separator + System.currentTimeMillis() + ".gif";
				m_iGifOperation.create(m_thisActivity, m_currentFilePath,
						m_duration, m_data.getDegreeMap());

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString(SettingsHelper.GIF_FILE_LOCATION,
						m_currentFilePath);
				intent.setClass(GifGenerateActivity.this,
						WeiboEditActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			if (m_proProgressDialog.isShowing()) {
				m_proProgressDialog.dismiss();
			}
			switch (message.what) {
			case PROCESS_COMPLETE:
				gotoWeiboEditActivity();
				break;
			case LOADMEDIA_FINISHED:
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
					
						m_imageView.setBackgroundDrawable(m_animation);
						m_animation.setOneShot(false);
						m_animation.start();
					}
				}, 50);
				break;
			case GETLOCATION_COMPLETED:
//				Location location = (Location)message.obj;
//				LocationHelper.getInstance().stop();
				switch (m_lastestPressedBtn) {
				case enumPressedBtn_Sina:
					OAuthManager.getInstance().setMode(OAuthManager.SINA);
					gotoWeiboEditActivity();
					break;
				case enumPressedBtn_Tencent:
					OAuthManager.getInstance().setMode(OAuthManager.TENCENT);
					gotoWeiboEditActivity();
					break;
				case enumPressedBtn_Common:
					gotoCommonShare();
					break;
				default:
					break;
				}
				break;
			}
		}
	};

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		int rate = seekBar.getProgress() + 30;
//		if (rate >= 80)
//		{
//			rate *= 2;
//		}
		
		m_duration = rate * 10;
		
		m_imageView.setDuration(m_duration);
//		m_animation.setDuration(m_duration);
	}

	private ImageView.OnClickListener sinaImageLinstener = new ImageView.OnClickListener() {
		public void onClick(View arg0) {
			Log.i(TAG, "sina weibo image button clicked...");
//			m_lastestPressedBtn = enumPressedBtn.enumPressedBtn_Sina;
//			if (!LocationHelper.getInstance().getIsNetworkAvailable())
//			{
//				LocationHelper.getInstance().openNetwork();
//				return;
//			}
//			getLocation();
			
			OAuthManager.getInstance().setMode(OAuthManager.SINA);
			gotoWeiboEditActivity();
		}
	};

	private ImageView.OnClickListener tencentImageLinstener = new ImageView.OnClickListener() {
		public void onClick(View arg0) {
			Log.i(TAG, "Tecent weibo image button clicked...");
//			m_lastestPressedBtn = enumPressedBtn.enumPressedBtn_Tencent;
//			if (!LocationHelper.getInstance().getIsNetworkAvailable())
//			{
//				LocationHelper.getInstance().openNetwork();
//				return;
//			}
//			getLocation();
			OAuthManager.getInstance().setMode(OAuthManager.TENCENT);
			gotoWeiboEditActivity();
		}
	};

	private ImageView.OnClickListener shareImageLinstenerListener = new ImageView.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "Share image button clicked...");
//			m_lastestPressedBtn = enumPressedBtn.enumPressedBtn_Common;
//			if (!LocationHelper.getInstance().getIsNetworkAvailable())
//			{
//				LocationHelper.getInstance().openNetwork();
//				return;
//			}
//			getLocation();
			
			gotoCommonShare();
			
		}

	};
	
	private ImageView.OnClickListener photoEditLinstenerListener = new ImageView.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "Edit button clicked...");
			Intent intent = new Intent();
			intent.setAction(ActivityActions.ACTION_PHOTO_EDIT);
//			Bundle bundle = new Bundle();
//			bundle.putSerializable(ActivityActions.EXTRA_PHOTO_EDIT_SOURCEDATA, m_data);
//			intent.putExtras(bundle);
			intent.putExtra(ActivityActions.EXTRA_PHOTO_EDIT_ISGROUP, true);
			GifGenerateActivity.this.startActivityForResult(intent,
					ActivityJump.PHOTO_EDIT_DEFAULT);
			
		}

	};
	
	private void gotoCommonShare() {
		m_currentFilePath = FileHelper.getAppHistoryPath() + File.separator
				+ System.currentTimeMillis() + ".gif";
		m_iGifOperation.create(m_thisActivity, m_currentFilePath, m_duration,
				m_data.getDegreeMap());

		Intent intent = new Intent();
		intent.setAction(ActivityActions.ACTION_SHARE);
		Bundle bundle = new Bundle();
		bundle.putString(ActivityActions.EXTRA_SHARE_SOUCEPATH,
				m_currentFilePath);
		bundle.putBoolean(ActivityActions.EXTRA_SHARE_GIF_FILE_ISCREATED, false);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	private void getLocation() {
		if (LocationHelper.getInstance().getIsNetworkAvailable()) {
			try {
				LocationHelper.getInstance().start();
				if (m_proProgressDialog.isShowing())
					return;
				final boolean isStop = false;
				final int TIMEOUT = 20000;
				m_proProgressDialog = ProgressDialog.show(this, getResources()
						.getString(R.string.general_searching), getResources()
						.getString(R.string.please_wait));
				new Thread() {
					public void run() {
						Location location = null;
						int passedTime = 0;
						try {
							
							while (!isStop && passedTime < TIMEOUT) {
								location = LocationHelper.getInstance()
										.getLocation();
								if (null != location)
									break;
								sleep(500);
								passedTime += 500;
							}

						} catch (Exception e) {
						}

						Message msg = new Message();
						msg.what = GETLOCATION_COMPLETED;
						msg.obj = location;
						handler.sendMessage(msg);
					}
				}.start();

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		else {
			switch (m_lastestPressedBtn) {
			case enumPressedBtn_Sina:
				OAuthManager.getInstance().setMode(OAuthManager.SINA);
				gotoWeiboEditActivity();
				break;
			case enumPressedBtn_Tencent:
				OAuthManager.getInstance().setMode(OAuthManager.TENCENT);
				gotoWeiboEditActivity();
				break;
			case enumPressedBtn_Common:
				gotoCommonShare();
				break;
			default:
				break;
			}
		}
	}

	private void gotoWeiboEditActivity() {
		if (null != m_iGifOperation) {
			m_currentFilePath = FileHelper.getAppHistoryPath() + File.separator
					+ System.currentTimeMillis() + ".gif";
			m_iGifOperation.create(m_thisActivity, m_currentFilePath,
					m_duration, m_data.getDegreeMap());

			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString(SettingsHelper.GIF_FILE_LOCATION,
					m_currentFilePath);
			bundle.putBoolean(SettingsHelper.GIF_FILE_ISCREATED, false);
			intent.setClass(GifGenerateActivity.this, WeiboEditActivity.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, WEIBO_FINISHED);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		case WEIBO_FINISHED:
			if (resultCode == RESULT_OK) {
				if (data.getBooleanExtra(ActivityJump.CLOSE_YOURSELF, false)) {
					setResult(RESULT_OK, data);
					finish();
				}
			}
			break;
		case LocationHelper.ID_OPENNETWORKCALLBACK:
			getLocation();
			break;
		case ActivityJump.PHOTO_EDIT_DEFAULT:
			if (resultCode == RESULT_OK)
			{
				m_data = DataManger.getInstance().getGenerateData();
//				resetAnimationImageView();
//				LoadData();
			}
			break;
		default:
			break;
		}

	}
}
