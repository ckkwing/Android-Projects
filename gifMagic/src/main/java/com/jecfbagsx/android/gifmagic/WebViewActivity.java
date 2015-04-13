package com.jecfbagsx.android.gifmagic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weibo4android.Weibo;
import weibo4android.WeiboException;
import weibo4android.http.AccessToken;
import weibo4android.http.RequestToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.gifmanage.OAuthManager;
import com.jecfbagsx.android.utils.TitlebarHelper;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.OAuthClient;

public class WebViewActivity extends Activity {
	private static final String TAG = "WebViewActivity";
	
	private static final int TASK_SINA_EXTRACTPIN_SUCCESSFUL = 3002;

	private static final int TASK_TENCENT_EXTRACTPIN_SUCCESSFUL = 3004;
	
	private String mPin = null;
	
	private JavaScriptInterface mJSI = null;
	private ProgressDialog mLoadURLProgressDlg = null;
	
	public void setPin(String pin) {
		mPin = pin;
	}
	public String getPin() {
		return mPin;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);

		TitlebarHelper.setbackground(this);

		Log.i(TAG, "WebView Activity create...");
		WebView webview = (WebView) findViewById(R.id.web_view);
		if (webview == null) {
			Log.i(TAG, "webview is null! invalid...");
			return;
		}
		webview.getSettings().setJavaScriptEnabled(true);
		webview.requestFocus();
		webview.getSettings().setSupportZoom(true);
		// webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		webview.getSettings().setBuiltInZoomControls(true);

		String url = this.getIntent().getStringExtra("url");
		Log.i(TAG, "###weibo request url = " + url);
		if (url.length() == 0) {
			Log.i(TAG, "url is invalid...");
			return;
		}
		// show Loading URL progress dialog...
		mLoadURLProgressDlg = new ProgressDialog(WebViewActivity.this);
		mLoadURLProgressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mLoadURLProgressDlg.setIndeterminate(true);
		mLoadURLProgressDlg.setTitle(R.string.webview_progress_dlg_title);
		mLoadURLProgressDlg.setMessage(getString(R.string.webview_progress_dlg_msg_loading_authorize_page));		
		mLoadURLProgressDlg.show();
		webview.loadUrl(url);

		mJSI = new JavaScriptInterface();
		webview.addJavascriptInterface(mJSI, "Methods");
		WebViewClient wvc = new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap bmp) {
				Log.i(TAG, "onPageStarted() entering...,url = " + url);
				Log.i(TAG, "super.onPageStarted() is called...");
				super.onPageStarted(view, url, bmp);				
				String mode = OAuthManager.getInstance().getMode();
				if (mode.equalsIgnoreCase(OAuthManager.TENCENT)){
					if(mLoadURLProgressDlg.isShowing())
						mLoadURLProgressDlg.dismiss();
					if (mode.equalsIgnoreCase(OAuthManager.TENCENT)&& url.contains("&code=0&v=")){					
						Uri uri = Uri.parse(url);
						String pin = uri.getQueryParameter("v");					
						setPin(pin);
						Log.w(TAG,"send Empty Message --TASK_TENCENT_EXTRACTPIN_SUCCESSFUL... ");
						getPinListener.sendEmptyMessage(TASK_TENCENT_EXTRACTPIN_SUCCESSFUL);
					}
				}
			}

			@Override
			synchronized public void onPageFinished(WebView view, String url) {
				Log.i(TAG, "onPageFinished() entering...,url = " + url);				
				String mode = OAuthManager.getInstance().getMode();
				if (mode.equalsIgnoreCase(OAuthManager.SINA)){
					if(mLoadURLProgressDlg.isShowing())
						mLoadURLProgressDlg.dismiss();
					if (url.equalsIgnoreCase("http://api.t.sina.com.cn/oauth/authorize")){					
						view.loadUrl("javascript:window.Methods.getHTML(document.getElementsByTagName('span')[0].innerHTML);");
					}
				}
				Log.i(TAG, "super.onPageFinished() is called...");
				super.onPageFinished(view, url);
			}			
		};
		webview.setWebViewClient(wvc);
		Log.i(TAG, "WebViewActivity Create is Finished...");
	}	

	public Handler getPinListener = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			String pin = null;
			switch (msg.what) 
			{
			case TASK_SINA_EXTRACTPIN_SUCCESSFUL:
				Log.i(TAG, "Receive Empty Message --TASK_SINA_EXTRACTPIN_SUCCESSFUL... ");
				pin = getPin();
				Log.i(TAG, "extract Pin =" + pin);
				RequestToken requestToken = OAuthManager.getInstance().getRequestToken();
				AccessToken accessToken = null;
				try {
					Log.i(TAG, "Ready to get accessToken ...");
					accessToken = requestToken.getAccessToken(pin);
					Log.i(TAG, "Get accessToken is successful...");
				} catch (WeiboException we) {					
					we.printStackTrace();
					OAuthManager.getInstance().setOAuthErrorMsg(we.getLocalizedMessage());
					setResult(RESULT_CANCELED);	
					finish();
					return;
				}
				OAuthManager.getInstance().setAccessToken(accessToken);
				Weibo weibo = OAuthManager.getInstance().getWeibo();
				weibo.setOAuthAccessToken(accessToken);
				Log.i(TAG, "setResult to RESULT_OK...");
				setResult(RESULT_OK);
				finish();
				break;			
			case TASK_TENCENT_EXTRACTPIN_SUCCESSFUL:
				Log.i(TAG, "Receive Empty Message --TASK_TENCENT_EXTRACTPIN_SUCCESSFUL... ");
				pin = getPin();
				OAuth oauth = OAuthManager.getInstance().getOAuth();
				Log.i(TAG, "Get Tencent pin = " + pin);
				try {
					oauth.setOauth_verifier(pin);
					OAuthClient oauthClient = OAuthManager.getInstance().getOAuthClient();
					oauth = oauthClient.accessToken(oauth);
					if (oauth.getStatus() == 2) {
						Log.e(TAG, "oauth.getStatus() == 2, Msg=="+oauth.getMsg());
						OAuthManager.getInstance().setOAuthErrorMsg(oauth.getMsg());
						setResult(RESULT_CANCELED);
						finish();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
					OAuthManager.getInstance().setOAuthErrorMsg(e.getLocalizedMessage());
					setResult(RESULT_CANCELED);
					finish();
					return;
				}
				Log.i(TAG, "setResult to RESULT_OK...");
				setResult(RESULT_OK);				
				finish();
				break;
			}
		}
	};
	
	class JavaScriptInterface {
		private static final String TAG = "JavaScriptInterface";		

		public void getHTML(String html) {
			Log.i(TAG, "getHTML..., content = " + html);
			String regEx = "[0-9]{6}";			
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(html);
			boolean result = m.find();
			if (result) {
				String pin = m.group(0);
				setPin(pin);
				Log.w(TAG,"send Empty Message --TASK_SINA_EXTRACTPIN_SUCCESSFUL... ");
				getPinListener.sendEmptyMessage(TASK_SINA_EXTRACTPIN_SUCCESSFUL);
			}
		}		
	}	
}


