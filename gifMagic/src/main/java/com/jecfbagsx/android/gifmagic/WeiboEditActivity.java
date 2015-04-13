package com.jecfbagsx.android.gifmagic;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

import weibo4android.Status;
import weibo4android.User;
import weibo4android.Weibo;
import weibo4android.WeiboException;
import weibo4android.http.RequestToken;
import weibo4android.org.json.JSONException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jecfbagsx.android.data.ResolutionInfo;
import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.gifmagic.service.CommandReceiver;
import com.jecfbagsx.android.gifmanage.OAuthManager;
import com.jecfbagsx.android.utils.ActivityJump;
import com.jecfbagsx.android.utils.FileHelper;
import com.jecfbagsx.android.utils.SettingsHelper;
import com.jecfbagsx.android.utils.TitlebarHelper;
import com.renren.api.connect.android.AsyncRenren;
import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.common.AbstractRequestListener;
import com.renren.api.connect.android.exception.RenrenAuthError;
import com.renren.api.connect.android.exception.RenrenError;
import com.renren.api.connect.android.photos.PhotoUploadRequestParam;
import com.renren.api.connect.android.photos.PhotoUploadResponseBean;
import com.renren.api.connect.android.view.RenrenAuthListener;
import com.tencent.weibo.api.Friends_API;
import com.tencent.weibo.api.T_API;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.OAuthClient;

public class WeiboEditActivity extends Activity {
	private static final String TAG = "WeiboEditActivity";
	private static final String WEIBOEDITACTIVITY_PREF = "WeiboEditActivity_Pref";
	private static final String SELECTED_MODE = "Selected_Mode";
	private static final String FIRST_RUN = "First_Run";
		
	public static final int SELECTED_SINA = 1;
	public static final int SELECTED_TENCENT = 2;
	public static final int SELECTED_RENREN = 3;
	
	private static final int TASK_COMPLETED = 1001;
	private static final int SINA_WEIBO_OAUTH_REQUEST_CODE = 1002;
	private static final int TENCENT_WEIBO_OAUTH_REQUEST_CODE = 1003;
	private static final int TASK_OAUTH_FAILED = 1004;
	private static final int TASK_OAUTH_SUCCESSFUL = 1005;	
	private static final int TASK_UPLOAD_SUCCESSFUL = 1006;
	private static final int TASK_UPLOAD_FAILED = 1007;
	private static final int AT_FRIENDS_REQUEST_CODE = 1008;
	private static final int RENREN_OAUTH_REQUEST_CODE = 2003;
	
	private static boolean mbFirstStart = true;

	private String mGifMagicUserID = "2293819314";

	private boolean mbCurrentAuthorized = false;
	private boolean mbSinaAuthorized = false;
	private boolean mbTencentAuthorized = false;
	private boolean mbRenrenAuthorized = false;
	private	Button mBtnSina = null;
	private	Button mBtnTencent = null;
	private	Button mBtnRenren = null;
	
	private int miSelected = SELECTED_SINA;
	private String mMessage = "";
	private String mGifLocation = "";

	private ProgressDialog mPublishProgressDlg = null;

	private EditText weiboEditText = null;
	private ProgressBar weiboEditProgressbar = null;
	private TextView progressTextView = null;
	private ProgressReceiver mProgressReceiver = null;
	private TextView mFileDesc;
	private boolean mIsGifGenerated = false;
	
	private String mUploadErrorMsg = null;
	
	private ImageView mImgAtFriends = null;
	private ImageButton mImageBtnBackToHome = null;
	private Handler handler = null; 

	private class ProgressReceiver extends CommandReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String path = intent
					.getStringExtra(CommandReceiver.MESSAGE_CREATEGIFPATH_STRING);
			if (mGifLocation.equals(path)) {
				int iProgress = (int) (100 * intent.getDoubleExtra(
						CommandReceiver.MESSAGE_CREATEGIFPROCESSRATE_DOUBLE, 0));
				Log.i(TAG,
						"Receive progress value = " + String.valueOf(iProgress));
				mIsGifGenerated = intent.getBooleanExtra(
						CommandReceiver.MESSAGE_FINISHPROCESS_BOOL, false);
				if (mIsGifGenerated) {
					weiboEditProgressbar.setProgress(100);
					setProgressString(false);
					updateFileInfo();
					Log.i(TAG, "Gif is generated...");
				} else {
					weiboEditProgressbar.setProgress(iProgress);
				}
			}
		}
	}

	private void setProgressString(boolean isProgress) {
		String disc ="";
		if (isProgress)
		{
			 disc = getResources().getString(R.string.weibo_edit_gif_is_in_processing);
		}else
		{
			 disc = getResources().getString(R.string.weibo_edit_gif_is_completed);
		}
		String his = getResources().getString(R.string.history_gif_path);
		disc +="\r\n "+his+FileHelper.DISPLAY_GIF_MAGIC_HISTORY;
		progressTextView.setText(disc);
	}	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weibo_edit);
		Log.i(TAG, "Web Edit Activity create...");

		mProgressReceiver = new ProgressReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommandReceiver.ACTION_COMMANDRECEIVER);
		this.getApplicationContext()
				.registerReceiver(mProgressReceiver, filter);

		TitlebarHelper.setbackground(this);

		Bundle bundle = this.getIntent().getExtras();
		mGifLocation = bundle.getString(SettingsHelper.GIF_FILE_LOCATION);
		Log.i(TAG, "Weibo edit Image Path = " + mGifLocation);
		mIsGifGenerated = bundle.getBoolean(SettingsHelper.GIF_FILE_ISCREATED,
				false);	
		
		mbSinaAuthorized = OAuthManager.getInstance().LoadOAuthInfo(WeiboEditActivity.this,OAuthManager.SINA);	
		mBtnSina = (Button) findViewById(R.id.weiboEdit_buttonSina);
		mBtnSina.setOnClickListener(btnOAuthListener);
		if(mbSinaAuthorized){			
			mBtnSina.setText(R.string.authorized);
			mBtnSina.setEnabled(false);
		}
		
		mbTencentAuthorized = OAuthManager.getInstance().LoadOAuthInfo(WeiboEditActivity.this,OAuthManager.TENCENT);
		mBtnTencent = (Button) findViewById(R.id.weiboEdit_buttonTencent);
		mBtnTencent.setOnClickListener(btnOAuthListener);
		if(mbTencentAuthorized){
			mBtnTencent.setText(R.string.authorized);
			mBtnTencent.setEnabled(false);
		}
		
		mbRenrenAuthorized = OAuthManager.getInstance().LoadOAuthInfo(WeiboEditActivity.this,OAuthManager.RENREN);
		mBtnRenren = (Button) findViewById(R.id.weiboEdit_buttonRenren);
		mBtnRenren.setOnClickListener(btnOAuthListener);
		if(mbRenrenAuthorized){
			mBtnRenren.setText(R.string.authorized);
			mBtnRenren.setEnabled(false);
		}
		
		boolean bFirstRun = false;
		SharedPreferences sharedPref = getSharedPreferences(WEIBOEDITACTIVITY_PREF,Context.MODE_WORLD_READABLE);
		if(sharedPref!=null){
			miSelected = sharedPref.getInt(SELECTED_MODE, SELECTED_SINA);
			Log.i(TAG, "Get SELECTED_MODE with " + miSelected);
			bFirstRun = sharedPref.getBoolean(FIRST_RUN, true);
		}
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.weiboEdit_radioGroup);		
		radioGroup.setOnCheckedChangeListener(checkedChangeListener);		
		if(miSelected == SELECTED_SINA)	{
			miSelected = SELECTED_SINA;
			mbCurrentAuthorized = mbSinaAuthorized;
			radioGroup.check(R.id.weiboEdit_radioSina);
		}else if (miSelected == SELECTED_TENCENT){
			miSelected = SELECTED_TENCENT;
			mbCurrentAuthorized = mbTencentAuthorized;
			radioGroup.check(R.id.weiboEdit_radioTencent);
		}else if (miSelected == SELECTED_RENREN){
			miSelected = SELECTED_RENREN;
			mbCurrentAuthorized = mbRenrenAuthorized;
			radioGroup.check(R.id.weiboEdit_radioRenren);
		}

		weiboEditText = (EditText) findViewById(R.id.weiboEdit_editText);
		mFileDesc = (TextView) findViewById(R.id.weiboEdit_file_desc);
		weiboEditProgressbar = (ProgressBar) findViewById(R.id.weiboEdit_progressBar);

		Button btnGotoPublish = (Button) findViewById(R.id.weiboedit_go_to_publish);
		btnGotoPublish.setOnClickListener(btnGotoPublishListener);		

		progressTextView = (TextView) findViewById(R.id.weiboEdit_progress_textView);
		
		mImageBtnBackToHome = (ImageButton) findViewById(R.id.weiboEdit_backtohome);	
		mImageBtnBackToHome.setOnClickListener(btnBackToHomeListener);
		
		mImgAtFriends = (ImageView) findViewById(R.id.weiboEdit_atFriends);	
		mImgAtFriends.setOnClickListener(imageAtFriendsListener);
		
		if (mIsGifGenerated) {
			weiboEditProgressbar.setProgress(100);
			setProgressString(false);
			updateFileInfo();
		} else {
			weiboEditProgressbar.setProgress(0);
			setProgressString(true);
		}	
		
		setTitle(R.string.weibo_edit_input_tip);
		handler = new Handler();
		
		if(bFirstRun)
		{
			Builder builderTip = new AlertDialog.Builder(this);
			builderTip.setTitle(getResources().getString(R.string.general_tip));
			builderTip.setMessage(getResources().getString(R.string.weibo_edit_gprs_or_wifi_upload_tip));
			builderTip.setPositiveButton(getResources().getString(R.string.ok),	new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog,int whichButton) {
							dialog.cancel();									
						}
					});					
			builderTip.show();
		}
	}

	private void updateFileInfo() {
		File gifFile = new File(mGifLocation);
		long fileSize = 0;
		if (gifFile != null) {
			fileSize = gifFile.length();
		}
		ResolutionInfo res = FileHelper.getImageResolution(mGifLocation);
		String fileSizeStr = FileHelper.convertFileSize(fileSize);
		mFileDesc.setText("(" + res.FormatRes() + ")" + fileSizeStr);
	}
	
	public void InsertFriendsToMsg()
	{
		ArrayList<String> selectedNameList = OAuthManager.getInstance().getSelectedNameList();
		if(selectedNameList==null||selectedNameList.size()<=0)
			return;
		int iPos = weiboEditText.getSelectionStart();
		Editable edit = weiboEditText.getText();
		String atNames = new String();
		for(int i=0; i<selectedNameList.size();i++)
		{
			atNames += "@"+selectedNameList.get(i)+" ";
		}
		edit.insert(iPos,atNames);
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "Web Edit Activity onDestroy...");
		mbFirstStart = false;
		SharedPreferences sharedPref = getSharedPreferences(WEIBOEDITACTIVITY_PREF,Context.MODE_WORLD_WRITEABLE);
		if(sharedPref!=null)
		{
			Editor editor = sharedPref.edit();
			if(editor!=null){
				editor.putInt(SELECTED_MODE, miSelected);	
				editor.putBoolean(FIRST_RUN, false);
				if(!editor.commit()) {
					Log.i(TAG, "Save SELECTED_MODE is failed....");
				}else {
					Log.i(TAG, "Set SELECTED_MODE to " + miSelected);
				}
			}
		}
		this.getApplicationContext().unregisterReceiver(mProgressReceiver);
		super.onDestroy();
	}
	
	RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener(){
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if(checkedId == R.id.weiboEdit_radioSina)
			{
				miSelected = SELECTED_SINA;
				mbCurrentAuthorized = mbSinaAuthorized;
			}				
			else if (checkedId == R.id.weiboEdit_radioTencent)
			{
				miSelected = SELECTED_TENCENT;
				mbCurrentAuthorized = mbTencentAuthorized;
			}
			else if (checkedId == R.id.weiboEdit_radioRenren)
			{
				miSelected = SELECTED_RENREN;
				mbCurrentAuthorized = mbRenrenAuthorized;
			}
		}		
	};
	
	private Button.OnClickListener btnOAuthListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			if (!isNetworkAvailable()) {
				Log.e(TAG, "connectivity is unavailable... ");
				return;
			}
			final int btnID = arg0.getId();
			mPublishProgressDlg = new ProgressDialog(WeiboEditActivity.this);
			mPublishProgressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mPublishProgressDlg.setIndeterminate(true);
			mPublishProgressDlg.setTitle(R.string.weibo_edit_progress_dlg_title);
			mPublishProgressDlg.setMessage(getString(R.string.weibo_edit_progress_dlg_msg_authorizing));
			if (btnID != R.id.weiboEdit_buttonRenren)
			{
				mPublishProgressDlg.show();
			}
			

			Thread authorizeTread = new Thread(new Runnable() {
				@Override
				public void run() {
					boolean bSuccess = false;
					if(btnID == R.id.weiboEdit_buttonSina)
					{
						OAuthManager.getInstance().setMode(OAuthManager.SINA);
						bSuccess = OAuthSina();
					}
					else if (btnID == R.id.weiboEdit_buttonTencent)
					{
						OAuthManager.getInstance().setMode(OAuthManager.TENCENT);
						bSuccess = OAuthTencent();
					}
					else if (btnID == R.id.weiboEdit_buttonRenren)
					{
						OAuthManager.getInstance().setMode(OAuthManager.RENREN);
						bSuccess = OAuthRenren();
					}
					
					Log.w(TAG, "send Empty Message --TASK_COMPLETED... ");
					publishMsgListener.sendEmptyMessage(TASK_COMPLETED);
					if (!bSuccess) {
						OAuthManager.getInstance().invalidCurrentWeibo();
						Log.w(TAG,"send Empty Message --TASK_OAUTH_FAILED... ");
						publishMsgListener.sendEmptyMessage(TASK_OAUTH_FAILED);
					}					
				}
			});
			authorizeTread.start();
		}
	};
	
	private View.OnClickListener btnBackToHomeListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			Log.e(TAG, "BackToHome event is trigered... ");			
			Intent intent = new Intent();
			intent.putExtra(ActivityJump.CLOSE_YOURSELF, true);
			setResult(RESULT_OK, intent);
			finish();
		}				
	};	
	
	private View.OnClickListener imageAtFriendsListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			if(!isAuthorized())
			{
				Log.e(TAG, "Still have not been authorized... ");
				return;
			}
			
			if (miSelected == SELECTED_SINA)
				OAuthManager.getInstance().setMode(OAuthManager.SINA);
			else if (miSelected == SELECTED_TENCENT)
				OAuthManager.getInstance().setMode(OAuthManager.TENCENT);
			else if (miSelected == SELECTED_RENREN)
				OAuthManager.getInstance().setMode(OAuthManager.RENREN);
			
			Intent intentAtFriends = new Intent();				
			intentAtFriends.setClass(WeiboEditActivity.this, WeiboAtFriendsActivity.class);
			startActivityForResult(intentAtFriends, AT_FRIENDS_REQUEST_CODE);
		}				
	};	

	private Button.OnClickListener btnGotoPublishListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			Log.i(TAG, "GotoPublish event is trigered... ");		
			if(!isAuthorized())
			{
				Log.e(TAG, "Still have not been authorized... ");
				return;
			}
			if (!mIsGifGenerated) {
				Toast.makeText(WeiboEditActivity.this,
						R.string.weibo_edit_gif_is_still_generating,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!isNetworkAvailable()) {
				Log.e(TAG, "connectivity is unavailable... ");
				return;
			}
			mPublishProgressDlg = new ProgressDialog(WeiboEditActivity.this);
			mPublishProgressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mPublishProgressDlg.setIndeterminate(true);
			mPublishProgressDlg.setTitle(R.string.weibo_edit_progress_dlg_title);
			mPublishProgressDlg.setMessage(getString(R.string.weibo_edit_progress_dlg_msg_publishing));			
			mPublishProgressDlg.show();

			Thread uploadTread = new Thread(new Runnable() {
				@Override
				public void run() {
					if (SettingsHelper.isHideGifMagicTag()) {
						mMessage = weiboEditText.getText().toString();
					} else {
						mMessage = getResources().getString(R.string.app_trend)
								+ weiboEditText.getText().toString();
					}
					Log.i(TAG, "Weibo edit msg = " + mMessage);					

					boolean bSuccess = false;
					if (miSelected == SELECTED_SINA)
						bSuccess = publishToSinaWeibo();
					else if (miSelected == SELECTED_TENCENT)
						bSuccess = publishToTencentWeibo();
					else if (miSelected == SELECTED_RENREN)
						bSuccess = publishToRenren();

					Log.w(TAG, "send Empty Message --TASK_COMPLETED... ");
					publishMsgListener.sendEmptyMessage(TASK_COMPLETED);

					if (bSuccess) {
						Log.w(TAG,"send Empty Message --TASK_UPLOAD_SUCCESSFUL... ");
						publishMsgListener.sendEmptyMessage(TASK_UPLOAD_SUCCESSFUL);
					} else {
						Log.w(TAG,"send Empty Message --TASK_UPLOAD_FAILED... ");
						publishMsgListener.sendEmptyMessage(TASK_UPLOAD_FAILED);
					}					
				}
			});
			uploadTread.start();
		}
	};

	private boolean OAuthSina() {
		try {
			Log.i(TAG, "Ready to got request token...");
			Weibo weibo = OAuthManager.getInstance().getWeibo();
			RequestToken requestToken = weibo.getOAuthRequestToken();
			if (requestToken == null) {
				String oauthErrorMsg = "Got request token is Failed...";
				Log.e(TAG, oauthErrorMsg);
				OAuthManager.getInstance().setOAuthErrorMsg(oauthErrorMsg);
				return false;
			}
			Log.i(TAG, "Get request token: " + requestToken.getToken());
			Log.i(TAG,"Get request token secret: "	+ requestToken.getTokenSecret());
			OAuthManager.getInstance().setRequestToken(requestToken);

			String url = requestToken.getAuthenticationURL();
			if (url == null) {
				String oauthErrorMsg = "Got Authentication URL is Failed...";
				Log.e(TAG, oauthErrorMsg);
				OAuthManager.getInstance().setOAuthErrorMsg(oauthErrorMsg);
				return false;
			}
			Intent intent = new Intent(WeiboEditActivity.this,WebViewActivity.class);
			intent.putExtra("url", url);
			startActivityForResult(intent, SINA_WEIBO_OAUTH_REQUEST_CODE);
			return true;
		} catch (WeiboException te) {
			te.printStackTrace();
			OAuthManager.getInstance().setOAuthErrorMsg(te.getLocalizedMessage());
			return false;
		}
	}

	private boolean OAuthTencent() {
		try {
			OAuth oauth = OAuthManager.getInstance().getOAuth();
			OAuthClient oauthClient = OAuthManager.getInstance()
					.getOAuthClient();
			oauth = oauthClient.requestToken(oauth);
			if (oauth.getStatus() == 1) {
				Log.e(TAG, "oauth.getStatus() == 1");
				OAuthManager.getInstance().setOAuthErrorMsg(oauth.getMsg());
				return false;
			}
			Log.i(TAG, "Ready to got oauth token...");
			String oauth_token = oauth.getOauth_token();
			Log.i(TAG, "oauth token: " + oauth.getOauth_token());
			Log.i(TAG, "oauth token secret: " + oauth.getOauth_token_secret());

			String url = "http://open.t.qq.com/cgi-bin/authorize?oauth_token="+ oauth_token;

			Intent intent = new Intent(WeiboEditActivity.this,
					WebViewActivity.class);
			intent.putExtra("url", url);
			startActivityForResult(intent, TENCENT_WEIBO_OAUTH_REQUEST_CODE);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			OAuthManager.getInstance().setOAuthErrorMsg(e.getLocalizedMessage());
			return false;
		}
	}
	
	final RenrenAuthListener listener = new RenrenAuthListener() {

		@Override
		public void onComplete(Bundle values) {
			onActivityResult(RENREN_OAUTH_REQUEST_CODE, RESULT_OK , null);
			Log.i(TAG, "RenrenAuthListener onComplete...");
		}

		@Override
		public void onRenrenAuthError(
				RenrenAuthError renrenAuthError) 
		{
			Log.i(TAG, "renren OAuth is failed...");
//			handler.post(new Runnable() 
//			{
//				
//				@Override
//				public void run() 
//				{
//					Toast.makeText(WeiboEditActivity.this, 
//							"验证失败", 
//							Toast.LENGTH_SHORT).show();
//				}
//			});
		}

		@Override
		public void onCancelLogin() {
		}

		@Override
		public void onCancelAuth(Bundle values) {
		}
		
	};
	
	private boolean OAuthRenren() {
		try {
			Renren renren = OAuthManager.getInstance().getRenRen(WeiboEditActivity.this);
			if (renren == null) {
				Log.e(TAG, "OAuthManager Get renren instance failed!");
				return false;
			}
			 
			Looper.prepare(); 
			renren.authorize(WeiboEditActivity.this, listener);
			Looper.loop(); 
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult() is activated...");
		
		switch (requestCode) {
		case SINA_WEIBO_OAUTH_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Log.i(TAG, "requestCode = SINA_WEIBO_OAUTH_REQUEST_CODE...");
				Log.i(TAG, "OAuth is successful...");
				if (!OAuthManager.getInstance().SaveOAuthInfo(WeiboEditActivity.this,OAuthManager.SINA)) 
				{
					Log.e(TAG, "Save OAuth Info occur error!");
				}
				mbSinaAuthorized = true;
				mBtnSina.setText(R.string.authorized);
				mBtnSina.setEnabled(false);
				Log.w(TAG,"send Empty Message --TASK_OAUTH_SUCCESSFUL... ");
				publishMsgListener.sendEmptyMessage(TASK_OAUTH_SUCCESSFUL);
			}
			else if(resultCode == RESULT_CANCELED)
			{
				Log.e(TAG, "requestCode = SINA_WEIBO_OAUTH_REQUEST_CODE...");
				Log.e(TAG, "resultCode == RESULT_CANCELED...");
				Log.e(TAG,"send Empty Message --TASK_OAUTH_FAILED... ");
				publishMsgListener.sendEmptyMessage(TASK_OAUTH_FAILED);				
			}
			break;
			
		case TENCENT_WEIBO_OAUTH_REQUEST_CODE:
			if (resultCode == RESULT_OK) 
			{
				Log.i(TAG, "requestCode = TENCENT_WEIBO_OAUTH_REQUEST_CODE...");
				Log.i(TAG, "OAuth is successful...");
				if (!OAuthManager.getInstance().SaveOAuthInfo(WeiboEditActivity.this,OAuthManager.TENCENT)) 
				{
					Log.e(TAG, "Save OAuth Info occur error!");
				}
				mbTencentAuthorized = true;
				mBtnTencent.setText(R.string.authorized);
				mBtnTencent.setEnabled(false);
				Log.w(TAG,"send Empty Message --TASK_OAUTH_SUCCESSFUL... ");
				publishMsgListener.sendEmptyMessage(TASK_OAUTH_SUCCESSFUL);
			}
			else if(resultCode == RESULT_CANCELED)
			{
				Log.i(TAG, "requestCode = TENCENT_WEIBO_OAUTH_REQUEST_CODE...");
				Log.i(TAG, "resultCode == RESULT_CANCELED...");
				Log.e(TAG,"send Empty Message --TASK_OAUTH_FAILED... ");
				publishMsgListener.sendEmptyMessage(TASK_OAUTH_FAILED);
			}
			break;
			
		case RENREN_OAUTH_REQUEST_CODE:
			if (resultCode == RESULT_OK)
			{		
				Log.i(TAG, "requestCode = RENREN_OAUTH_REQUEST_CODE...");
				Log.i(TAG, "OAuth is successful...");
				if (!OAuthManager.getInstance().SaveOAuthInfo(WeiboEditActivity.this,OAuthManager.RENREN)) 
				{
					Log.e(TAG, "Save OAuth Info occur error!");
				}
				mbRenrenAuthorized = true;
				mBtnRenren.setText(R.string.authorized);
				mBtnRenren.setEnabled(false);
				Log.w(TAG,"send Empty Message --TASK_OAUTH_SUCCESSFUL... ");
				publishMsgListener.sendEmptyMessage(TASK_OAUTH_SUCCESSFUL);
			}
			break;
			
		case AT_FRIENDS_REQUEST_CODE:
			if (resultCode == RESULT_OK)
			{
				Log.i(TAG, "requestCode = AT_FRIENDS_REQUEST_CODE...");
				Log.i(TAG, "At Friends is done...");
				InsertFriendsToMsg();
			}
			break;
		}
	}

	private boolean publishToSinaWeibo() {
		Log.i(TAG, "publish to sina weibo is triggerd...");
		Weibo weibo = OAuthManager.getInstance().getWeibo();
		try {
			Log.i(TAG, "mGifLocation = " + mGifLocation);
			String picPath = mGifLocation;
			File picFile = new File(picPath);
			boolean bExist = picFile.exists();
			if (!bExist) {
				mUploadErrorMsg = "image file [" + picPath + "] is not found...";
				return false;
			}
			
			if (picFile.length() == 0) {
				mUploadErrorMsg = "image content is empty...";
				return false;
			}
			
			if(picFile.length()>=5*1000*1000)
			{
				mUploadErrorMsg = "the image size is exceeed 5M,It's not support by Sina!";
				return false;
			}					
			
			String msg = URLEncoder.encode(mMessage, "UTF-8");
			try {
				Status status = weibo.uploadStatus(msg, picFile);
				if (status.getOriginal_pic().length() == 0
						&& status.getText().length() == 0) {
					mUploadErrorMsg = getResources().getString(R.string.error_return_from_sina)+getResources().getString(R.string.weibo_service_or_network_is_unavailable);
					return false;
				}
				Log.i(TAG, "Successfully upload the status to [" + status.getText()	+ status.getOriginal_pic() + "].");				
			} catch (WeiboException we) {
				Log.i(TAG, "Upload the status occur exception...");	
				mUploadErrorMsg = getResources().getString(R.string.error_return_from_sina)+we.getLocalizedMessage();
				we.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			mUploadErrorMsg = getResources().getString(R.string.error_exception)+e.getLocalizedMessage();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean publishToTencentWeibo() {
		Log.i(TAG, "publish to Tencent weibo is triggerd...");
		try {
			OAuth oauth = OAuthManager.getInstance().getOAuth();
			T_API tapi = OAuthManager.getInstance().getTAPI();
			// get ip
			String clientIp = "127.0.0.1";
			// check gif file
			Log.i(TAG, "mGifLocation = " + mGifLocation);
			String picPath = mGifLocation;
			File picFile = new File(picPath);
			boolean bExist = picFile.exists();
			if (!bExist) {
				mUploadErrorMsg = "image file [" + picPath + "] is not found...";
				return false;
			}
			// post weibo msg and pic
			String s = tapi.add_pic(oauth, "json", mMessage, clientIp, picPath);
			Log.i(TAG, "Successfully add_pic to [" + s + "]");
		} catch (Exception e) {
			mUploadErrorMsg = getResources().getString(R.string.error_exception)+e.getLocalizedMessage();
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean publishToRenren() {
		Log.i(TAG, "publish to Renren is triggerd...");
		try {

			Log.i(TAG, "mGifLocation = " + mGifLocation);
			String picPath = mGifLocation;
			File picFile = new File(picPath);
			boolean bExist = picFile.exists();
			if (!bExist) {
				Log.i(TAG, "image file [" + picPath + "] is not found...");
				return false;
			}
			
			PhotoUploadRequestParam photoParam = new PhotoUploadRequestParam();
			// 设置caption参数
			if (mMessage != null && !"".equals(mMessage.trim())) {
				photoParam.setCaption(mMessage);
			}
			// 设置file参数
			photoParam.setFile(picFile);
			Renren renren = OAuthManager.getInstance().getRenRen(WeiboEditActivity.this);
			
			try 
			{
				renren.publishPhoto(photoParam);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
	
//			Log.i(TAG, "Successfully add_pic to [" + s + "]");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void GoToMainActivity(boolean bUploaded) {
		String msgResult = null;
		if (bUploaded) {
			msgResult = getResources().getString(R.string.weibo_edit_publish_is_successful);			
		} else {
			msgResult = getResources().getString(R.string.weibo_edit_publish_occur_error)+" " +mUploadErrorMsg;
		}
		Builder builderResult = new AlertDialog.Builder(WeiboEditActivity.this);		
		builderResult.setMessage(msgResult);
		builderResult.setPositiveButton(getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();						
					}
				});		
		builderResult.create().show();
	}	
	
	private void ShowOAuthDetail(boolean bAuthorized)
	{
		final boolean bGifMagicIsFollowedBy = IsGifmagicFollowedBy();
		String msgResult = null;
		Builder builderOAuthRequest = new AlertDialog.Builder(WeiboEditActivity.this);
		builderOAuthRequest.setTitle(getResources().getString(R.string.weibo_edit_oauth_result));
		if(bAuthorized)
		{
			msgResult = getResources().getString(R.string.weibo_edit_oauth_is_successful);
			if (!bGifMagicIsFollowedBy) {
				msgResult += getResources().getString(R.string.weibo_edit_create_friendship);
			}			
		}
		else
		{
			msgResult = getResources().getString(R.string.weibo_edit_oauth_occur_error)+" "+getResources().getString(R.string.error_exception)+OAuthManager.getInstance().getOAuthErrorMsg();
		}		
		builderOAuthRequest.setMessage(msgResult);
		builderOAuthRequest.setPositiveButton(getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						if (!bGifMagicIsFollowedBy) {
							CreateFriendshipWithGifMagic();
						}					
					}
				});
		if (!bGifMagicIsFollowedBy) {
			builderOAuthRequest.setNegativeButton(
					getResources().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();						
						}
					});
		}
		builderOAuthRequest.create();
		builderOAuthRequest.show();
	}

	public boolean IsGifmagicFollowedBy() {
		boolean bIsFollowedBy = true;
		String mode = OAuthManager.getInstance().getMode();
		if (mode.equalsIgnoreCase(OAuthManager.SINA)) {
			Weibo weibo = OAuthManager.getInstance().getWeibo();
			try {
				weibo4android.org.json.JSONObject jsonObj = weibo
						.showFriendships(mGifMagicUserID);
				try {
					bIsFollowedBy = jsonObj.getJSONObject("target").getBoolean(
							"followed_by");
					Log.i(TAG, jsonObj.toString());
				} catch (JSONException json_e) {
					Log.e(TAG,
							"Get JSONObject attribute of target.followed_by is Failed!");
					json_e.printStackTrace();
				}
			} catch (WeiboException e) {
				Log.e(TAG, "Show Friendship By GifMagic UserID is Failed!");
				e.printStackTrace();
			}
		} else if (mode.equalsIgnoreCase(OAuthManager.TENCENT)) {			
			OAuth oauth = OAuthManager.getInstance().getOAuth();
			org.json.JSONObject jsonObj = null;
			try {
				Friends_API friendsAPI = new Friends_API();
				String data = friendsAPI.check(oauth, "json","gifmagic","1");
				jsonObj = new org.json.JSONObject(data);
				bIsFollowedBy = jsonObj.getJSONObject("data").getBoolean("gifmagic");
				Log.i(TAG, data.toString());				
			} catch (Exception e) {
				Log.e(TAG, "FriendsAPI check by gifmagic is failed!");
				e.printStackTrace();
			}
		}
		return bIsFollowedBy;
	}

	public boolean CreateFriendshipWithGifMagic() {
		String mode = OAuthManager.getInstance().getMode();
		if (mode.equalsIgnoreCase(OAuthManager.SINA)) {
			Weibo weibo = OAuthManager.getInstance().getWeibo();
			try {
				User user = weibo.createFriendshipByUserid(mGifMagicUserID);
				Log.i(TAG, user.toString());
			} catch (WeiboException e) {
				Log.e(TAG, "Create Friendship By GifMagic UserID is Failed!");
				e.printStackTrace();
				return false;
			}
		} else if (mode.equalsIgnoreCase(OAuthManager.TENCENT)) {
			OAuth oauth = OAuthManager.getInstance().getOAuth();
			try {
				Friends_API friendsAPI = new Friends_API();
				String clientIp = "127.0.0.1";
				String data = friendsAPI.add(oauth, "json", "gifmagic", clientIp);
				Log.i(TAG, data.toString());				
			} catch (Exception e) {
				Log.e(TAG, "FriendsAPI check by gifmagic is failed!");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public Handler publishMsgListener = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TASK_COMPLETED:
				Log.w(TAG, "Receive Empty Message --TASK_COMPLETED... ");
				if(mPublishProgressDlg.isShowing()){
					mPublishProgressDlg.dismiss();
				}
				break;
			case TASK_OAUTH_FAILED:
				Log.e(TAG, "Receive Empty Message --TASK_OAUTH_FAILED... ");
				OAuthManager.getInstance().invalidCurrentWeibo();
				ShowOAuthDetail(false);
				break;
			case TASK_OAUTH_SUCCESSFUL:
				Log.e(TAG, "Receive Empty Message --TASK_OAUTH_SUCCESSFUL... ");
				OAuthManager.getInstance().SaveFriends(WeiboEditActivity.this);
				ShowOAuthDetail(true);
				break;				
			case TASK_UPLOAD_SUCCESSFUL:
				Log.w(TAG,"Receive Empty Message --TASK_PUBLISH_SUCCESSFUL... ");
				OAuthManager.getInstance().SaveHistoryUser(WeiboEditActivity.this,mMessage);
				if(mbFirstStart){
					OAuthManager.getInstance().SaveFriends(WeiboEditActivity.this);
				}
				GoToMainActivity(true);
				break;
			case TASK_UPLOAD_FAILED:
				Log.e(TAG, "Receive Empty Message --TASK_PUBLISH_FAILED... ");
				GoToMainActivity(false);
				break;
			}
		}
	};	
	
	private boolean isAuthorized() {
		if (miSelected == SELECTED_SINA)
			mbCurrentAuthorized = mbSinaAuthorized;
		else if (miSelected == SELECTED_TENCENT)
			mbCurrentAuthorized = mbTencentAuthorized;
		else if (miSelected == SELECTED_RENREN)
			mbCurrentAuthorized = mbRenrenAuthorized;
		
		if (!mbCurrentAuthorized) {
			Builder builderAuthorized = new AlertDialog.Builder(this)
					.setTitle(
							getResources().getString(R.string.unauthorized))
					.setMessage(
							getResources()
									.getString(
											R.string.general_go_to_authorize));
			builderAuthorized
					.setPositiveButton(getResources().getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int whichButton) {
									dialog.cancel();									
								}
							});					
			builderAuthorized.show();
		}
		return mbCurrentAuthorized;
	}

	private boolean isNetworkAvailable() {
		boolean flag = false;
		NetworkInfo netInfo = null;
		ConnectivityManager cvtManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		netInfo = cvtManager.getActiveNetworkInfo();
		if (netInfo != null) {
			flag = netInfo.isAvailable();
			Log.i(TAG,
					"connectivity is available...,TypeName = "
							+ netInfo.getTypeName());
		}
		if (!flag) {
			Builder builderNetwork = new AlertDialog.Builder(this)
					.setTitle(
							getResources().getString(
									R.string.networkinfo_unavailable))
					.setMessage(
							getResources()
									.getString(
											R.string.networkinfo_start_gprs_or_wifi_connect));
			builderNetwork
					.setPositiveButton(getResources().getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									Intent mIntent = new Intent("/");
									ComponentName comp = new ComponentName(
											"com.android.settings",
											"com.android.settings.WirelessSettings");
									mIntent.setComponent(comp);
									mIntent.setAction("<span>android</span>.intent.action.VIEW");
									startActivity(mIntent);
								}
							})
					.setNeutralButton(
							getResources().getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}
							}).create();
			builderNetwork.show();
		}
		return flag;
	}

}
