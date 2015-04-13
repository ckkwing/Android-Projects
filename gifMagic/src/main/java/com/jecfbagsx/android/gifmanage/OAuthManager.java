package com.jecfbagsx.android.gifmanage;

import java.util.ArrayList;
import java.util.List;

import weibo4android.User;
import weibo4android.Weibo;
import weibo4android.WeiboException;
import weibo4android.http.AccessToken;
import weibo4android.http.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.renren.api.connect.android.AccessTokenManager;
import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.exception.RenrenException;
import com.renren.api.connect.android.friends.FriendsGetFriendsRequestParam;
import com.renren.api.connect.android.friends.FriendsGetFriendsResponseBean;
import com.renren.api.connect.android.friends.FriendsGetFriendsResponseBean.Friend;
import com.tencent.weibo.api.Friends_API;
import com.tencent.weibo.api.T_API;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.OAuthClient;

public class OAuthManager {
	private static final String TAG = "OAuthManager";
	public static final String SINA = "Sina";
	public static final String TENCENT = "Tencent";
	public static final String RENREN = "RenRen";
	public static final String OAUTH_INFO = "OAuth_Info";
	public static final String RENREN_OAUTH_INFO = "Renren_OAuth_Info";

	private static final String ACCESS_TOKEN = "AccessToken";
	private static final String ACCESS_TOKEN_SECRET = "AccessTokenSecret";
	
	private static final String OAUTH_TOKEN = "OAuthToken";
	private static final String OAUTH_TOKEN_SECRET = "OAuthTokenSecret";
	
	private static final String SINA_FRIENDS_USER_LIST = "sina_friends_user_list";
	private static final String TENCENT_FRIENDS_USER_LIST = "tencent_friends_user_list";
	private static final String RENREN_FRIENDS_USER_LIST = "renren_friends_user_list";
	
	private static final String USER_COUNT = "UserCount";
	
	private static final String SINA_HISTORY_USER_LIST = "sina_history_user_list";
	private static final String TENCENT_HISTORY_USER_LIST = "tencent_history_user_list";
	private static final String RENREN_HISTORY_USER_LIST = "renren_history_user_list";
	
	private static final int HISTORY_FRIENDS_MAX_COUNT = 10;

	private static OAuthManager mInstance = null;
	// Sina Weibo
	private Weibo mWeibo = null;
	private RequestToken mRequestToken = null;
	private AccessToken mAccessToken = null;

	// Tencent Weibo
	private T_API mTApi = null;
	private OAuth mOAuth = null;
	private OAuthClient mOAuthClient = null;
	
	// RenRen
	private Renren mRenren = null;

	//Other
	private String mMode = "";
	private String mOAuthErrorMsg = null;
	
	private ArrayList<String> mSelectedNameList = null;

	public static synchronized OAuthManager getInstance() {
		if (mInstance == null)
			mInstance = new OAuthManager();
		return mInstance;
	}

	public void setMode(String mode) {
		mMode = mode;
	}

	public String getMode() {
		return mMode;
	}

	public Weibo getWeibo() {
		if (mWeibo == null){
			Weibo.CONSUMER_KEY = "3828087740";
			Weibo.CONSUMER_SECRET = "4001ecabafc4b7be4e0a4f42f41351ff";
			mWeibo = new Weibo();			
			mWeibo.setOAuthConsumer(Weibo.CONSUMER_KEY,Weibo.CONSUMER_SECRET);
			Log.i(TAG, "Weibo instance is created...");
		}
		return mWeibo;
	}
	
	public void invalidCurrentWeibo() {
		Log.i(TAG, "invalid Current weibo...");
		if (mMode.equalsIgnoreCase(SINA)){
			mWeibo = null;
		}
		else if (mMode.equalsIgnoreCase(TENCENT)){
			mOAuth = null;
			mOAuthClient = null;
			mTApi = null;
		}
	}

	public T_API getTAPI() {
		if (mTApi == null) {
			mTApi = new T_API();
			Log.i(TAG, "T_API instance is created...");
		}
		return mTApi;
	}

	public OAuth getOAuth() {
		if (mOAuth == null) {
			mOAuth = new OAuth();
			mOAuth.setOauth_consumer_key("1766dc9b79aa45e59ae83f3be8656cbf");
			mOAuth.setOauth_consumer_secret("259dc11efb88fbd768db3470023f9214");
			Log.i(TAG, "OAuth instance is created...");
		}
		return mOAuth;
	}

	public OAuthClient getOAuthClient() {
		if (mOAuthClient == null) {
			mOAuthClient = new OAuthClient();
			Log.i(TAG, "OAuthClient instance is created...");
		}
		return mOAuthClient;
	}
	
	public Renren getRenRen(Context context) {
		if (mRenren == null)
		{
			mRenren = new Renren("1241d9f4c822410aae69975d35f4184f", "a7133544f870416faedc5ddfae6a4042", "161165", context);
			Log.i(TAG, "RenRen instance is created...");
		}
		return mRenren;
	}

	public RequestToken getRequestToken() {
		return mRequestToken;
	}

	public void setRequestToken(RequestToken requestToken) {
		Log.i(TAG, "SetRequestToken is called...");
		mRequestToken = requestToken;
	}

	public AccessToken getAccessToken() {
		return mAccessToken;
	}

	public void setAccessToken(AccessToken accessToken) {
		Log.i(TAG, "setAccessToken is called...");
		mAccessToken = accessToken;
	}

	public boolean Cleanup(String mode,Activity activity) {
		SharedPreferences oauthInfo = activity.getSharedPreferences(OAUTH_INFO,Context.MODE_WORLD_WRITEABLE);
		if(oauthInfo==null)
			return false;
		Editor editor = oauthInfo.edit(); 
		if(editor==null)
			return false;
		if (mode.equalsIgnoreCase(SINA)) {
			mWeibo = null;
			mRequestToken = null;
			mAccessToken = null;
			editor.putString(ACCESS_TOKEN,null);
			editor.putString(ACCESS_TOKEN_SECRET, null);
			if(editor.commit())
			{
				Log.i(TAG, "Cleanup Sina oauth is successful!");
				return true;
			}			
		} else if (mode.equalsIgnoreCase(TENCENT)) {			
			mTApi = null;
			mOAuth = null;
			editor.putString(OAUTH_TOKEN, null);
			editor.putString(OAUTH_TOKEN_SECRET, null);
			if(editor.commit())
			{
				Log.i(TAG, "Cleanup Tencent oauth is successful!");
				return true;
			}	
		} else if (mode.equalsIgnoreCase(RENREN)) {				
			if (null == mRenren)
			{
				getRenRen(activity);
			}
				
			mRenren.logout(activity);			
			mRenren.GetAccessTokenManager().clearPersistSession();			
			editor.putString(RENREN_OAUTH_INFO, null);

			if(editor.commit())
			{
				Log.i(TAG, "Cleanup Renren oauth is successful!");
				return true;
			}
		}
		
		return false;
	}	
	
	public boolean LoadOAuthInfo(Activity activity,String mode)
	{
		Log.i(TAG, "LoadOAuthInfo is called...,Mode == " +mode);
		if (mode.equalsIgnoreCase(SINA)) {
			SharedPreferences oauthInfo = activity.getSharedPreferences(OAUTH_INFO,Context.MODE_WORLD_READABLE);
			if(oauthInfo==null)
				return false;
			String accessToken = oauthInfo.getString(ACCESS_TOKEN, null);
			String accessTokenSecret = oauthInfo.getString(ACCESS_TOKEN_SECRET, null);
			if(accessToken==null || accessTokenSecret==null)
				return false;
			mWeibo = getWeibo();
			mWeibo.setOAuthAccessToken(accessToken,accessTokenSecret);
			Log.i(TAG, "LoadOAuthInfo(Sina) is success....");
			return true;
		} else if (mode.equalsIgnoreCase(TENCENT)) {
			SharedPreferences oauthInfo = activity.getSharedPreferences(OAUTH_INFO,Context.MODE_WORLD_WRITEABLE);
			if(oauthInfo==null)
				return false;
			String oauthToken = oauthInfo.getString(OAUTH_TOKEN, null);
			String oauthTokenSecret = oauthInfo.getString(OAUTH_TOKEN_SECRET, null);
			if(oauthToken==null || oauthTokenSecret==null)
				return false;
			mOAuth = getOAuth();
			mOAuth.setOauth_token(oauthToken);
			mOAuth.setOauth_token_secret(oauthTokenSecret);
			Log.i(TAG, "LoadOAuthInfo(Tencent) is success....");
			return true;						
		} else if (mode.equalsIgnoreCase(RENREN)) {
			SharedPreferences oauthInfo = activity.getSharedPreferences(OAUTH_INFO,Context.MODE_WORLD_WRITEABLE);
			if(oauthInfo==null)
				return false;
			String renrenOauthInfo = oauthInfo.getString(RENREN_OAUTH_INFO, null);
			if(renrenOauthInfo == null)
				return false;

			Log.i(TAG, "LoadOAuthInfo(Renren) is success....");
			return true;					
		}
		
		return false;
	}
	
	public boolean SaveOAuthInfo(Activity activity,String mode)
	{
		Log.i(TAG, "SaveOAuthInfo is called...,Mode == " +mode);
		if (mode.equalsIgnoreCase(SINA)) {			
			SharedPreferences oauthInfo = activity.getSharedPreferences(OAUTH_INFO,Context.MODE_WORLD_WRITEABLE);
			if(oauthInfo==null)
				return false;
			Editor editor = oauthInfo.edit(); 
			if(editor==null)
				return false;
			editor.putString(ACCESS_TOKEN, mAccessToken.getToken());
			editor.putString(ACCESS_TOKEN_SECRET, mAccessToken.getTokenSecret());
			if(editor.commit());
			{
				Log.i(TAG, "SaveOAuthInfo(Sina) is success....");
				return true;
			}			
		} else if (mode.equalsIgnoreCase(TENCENT)) {
			SharedPreferences oauthInfo = activity.getSharedPreferences(OAUTH_INFO,Context.MODE_WORLD_WRITEABLE);
			if(oauthInfo==null)
				return false;
			Editor editor = oauthInfo.edit();  
			if(editor==null)
				return false;
			editor.putString(OAUTH_TOKEN, mOAuth.getOauth_token());
			editor.putString(OAUTH_TOKEN_SECRET, mOAuth.getOauth_token_secret());
			if(editor.commit());
			{
				Log.i(TAG, "SaveOAuthInfo(Tencent) is success....");
				return true;
			}
		} else if (mode.equalsIgnoreCase(RENREN)) {
			SharedPreferences oauthInfo = activity.getSharedPreferences(OAUTH_INFO,Context.MODE_WORLD_WRITEABLE);
			if(oauthInfo==null)
				return false;
			Editor editor = oauthInfo.edit();  
			if(editor==null)
				return false;
			editor.putString(RENREN_OAUTH_INFO, RENREN_OAUTH_INFO);
			if(editor.commit());
			{
				Log.i(TAG, "SaveOAuthInfo(Renren) is success....");
				return true;
			}
		}
		
		return false;
	}
	
	public boolean SaveHistoryUser(Activity activity,String msg) {
		Log.i(TAG, "Save History User is called...");
		int atToken = '@';
		int start = 0;
		start = msg.indexOf(atToken, start);
		if(start == -1)
			return true;
		SharedPreferences historyInfo = null;
		if (mMode.equalsIgnoreCase(SINA)) {				
			historyInfo = activity.getSharedPreferences(SINA_HISTORY_USER_LIST,Context.MODE_WORLD_WRITEABLE|Context.MODE_WORLD_READABLE);
		} else if (mMode.equalsIgnoreCase(TENCENT)) {
			historyInfo = activity.getSharedPreferences(TENCENT_HISTORY_USER_LIST,Context.MODE_WORLD_WRITEABLE|Context.MODE_WORLD_READABLE);
		} else if (mMode.equalsIgnoreCase(RENREN)) {
			historyInfo = activity.getSharedPreferences(RENREN_HISTORY_USER_LIST,Context.MODE_WORLD_WRITEABLE|Context.MODE_WORLD_READABLE);
		}
		
		if(historyInfo==null)
			return false;
		Editor editor = historyInfo.edit(); 
		if(editor==null)
			return false;
		ArrayList<String> userArray = new ArrayList<String>();
		int end = msg.length();
		while(start!=-1)
		{
			String userName = new String();
			++start;
			if(start>=end)
				break;
			int ch = msg.charAt(start);
			while(ch!=' '&&ch!='@')
			{
				userName += String.valueOf((char)ch);
				++start;
				if(start>=end)
					break;
				ch = msg.charAt(start);					
			}
			if(false==userArray.contains(userName))
			{
				userArray.add(userName);
			}
			start = msg.indexOf(atToken, start);
		}
		for(int i=0; i<HISTORY_FRIENDS_MAX_COUNT;i++)
		{
			String userName = historyInfo.getString(String.valueOf(i), null);
			if(userName!=null&&false==userArray.contains(userName))
			{
				userArray.add(userName);
			}
		}			
		editor.clear();
		for(int i= 0; i<HISTORY_FRIENDS_MAX_COUNT&&i<userArray.size();i++)
		{
			editor.putString(String.valueOf(i),userArray.get(i));				
		}
		if(editor.commit())
		{
			Log.i(TAG, "Save History User Is Success....");
			return true;
		}
		return false;		
	}
	
	public boolean LoadHistoryUser(Activity activity,ArrayAdapter<String> arrayAdapter)
	{
		Log.i(TAG, "Load History User is called...");
		SharedPreferences historyInfo = null;
		if (mMode.equalsIgnoreCase(SINA)) {
			historyInfo = activity.getSharedPreferences(SINA_HISTORY_USER_LIST,Context.MODE_WORLD_READABLE);
		} else if (mMode.equalsIgnoreCase(TENCENT)) {
			historyInfo = activity.getSharedPreferences(TENCENT_HISTORY_USER_LIST,Context.MODE_WORLD_READABLE);
		} else if (mMode.equalsIgnoreCase(RENREN)) {
			historyInfo = activity.getSharedPreferences(RENREN_HISTORY_USER_LIST,Context.MODE_WORLD_READABLE);
		}
		
		if(historyInfo==null)
			return false;
		arrayAdapter.clear();			
		for(int i=0; i<HISTORY_FRIENDS_MAX_COUNT;i++)
		{
			String userName = historyInfo.getString(String.valueOf(i), null);
			if(userName!=null)
			{
				arrayAdapter.add(userName);
			}
		}			
		Log.i(TAG, "Load History User is success....");
		return true;		
	}
	
	public boolean SaveFriends(Activity activity) {
		Log.i(TAG, "Save Friends is called...");
		SharedPreferences friendsInfo = null;
		ArrayList<String> nameList = new ArrayList<String>();
		if (mMode.equalsIgnoreCase(SINA)) {
			Weibo weibo = OAuthManager.getInstance().getWeibo();
			try {
				List<User> userList = weibo.getFriends();	
				for(int i=0; i<userList.size();i++)
				{
					String userName = userList.get(i).getName();
					if(userName!=null){
						nameList.add(userName);
					}
				}	
			} catch (WeiboException e) {
				Log.e(TAG, "get friends user_list is failed!");
				e.printStackTrace();
				return false;
			}	
			friendsInfo = activity.getSharedPreferences(SINA_FRIENDS_USER_LIST,Context.MODE_WORLD_WRITEABLE);					
		} else if (mMode.equalsIgnoreCase(TENCENT)) {			
			OAuth oauth = OAuthManager.getInstance().getOAuth();
			int iHasNext = 0;
			int page = 1;
			int reqnum = 200;
			while(iHasNext==0)
			{
				String dataResult = null;
				try {
					Friends_API friendsAPI = new Friends_API();	
					dataResult = friendsAPI.idollist_s(oauth, "json", String.valueOf(reqnum), String.valueOf(reqnum*(page-1)));
				}catch (Exception e) {
					Log.e(TAG, "get tencent idollist_s occur Exception!");
					e.printStackTrace();
					return false;
				}
				try {
					org.json.JSONObject jsonObject = new org.json.JSONObject(dataResult).getJSONObject("data");				
					org.json.JSONArray jsonArray = jsonObject.getJSONArray("info");
					for(int i=0; i<jsonArray.length();i++)
					{				            
						org.json.JSONObject jsonObjectInfo = jsonArray.getJSONObject(i); 
						String userName = jsonObjectInfo.getString("name");
						if(userName!=null){
							nameList.add(userName);
						}
					}
					iHasNext = jsonObject.getInt("hasnext");						
				}catch (org.json.JSONException e) {
					Log.e(TAG, "get tencent jsonObjectInfo occur JSONException...!");
					e.printStackTrace();
					return false;
				}
				++page;
			}						        
			friendsInfo = activity.getSharedPreferences(TENCENT_FRIENDS_USER_LIST,Context.MODE_WORLD_WRITEABLE);			
		}  else if (mMode.equalsIgnoreCase(RENREN)) {			
			Renren renren = OAuthManager.getInstance().getRenRen(activity);
			FriendsGetFriendsRequestParam param = new FriendsGetFriendsRequestParam();
			try 
			{
				FriendsGetFriendsResponseBean bean = renren.getFriends(param);
				if (null != bean){
					ArrayList<Friend> friendList = bean.getFriendList();
					if (friendList != null) {
						for (Friend f : friendList) {
							nameList.add(f.getName());
						}
					}
				}
				
			} catch (RenrenException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "get renren Friends throw RenrenException...!");
				e.printStackTrace();
				return false;
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "get renren Friends throw RenrenException...!");
				e.printStackTrace();
				return false;
			}
			
							        
			friendsInfo = activity.getSharedPreferences(RENREN_FRIENDS_USER_LIST,Context.MODE_WORLD_WRITEABLE);			
		}
		if(nameList.size()<1)
			return true;
		if(friendsInfo==null)
			return false;
		Editor editor = friendsInfo.edit(); 
		if(editor==null)
			return false;
		editor.clear();
		editor.putInt(USER_COUNT, nameList.size());
		for(int i=0; i<nameList.size();i++)
		{				            
			editor.putString(String.valueOf(i), nameList.get(i));				
		}						
		if(editor.commit())
		{
			Log.i(TAG, "Save Friends is success....");
			return true;
		}
		return false;
	}
	
	public boolean LoadFriends(Activity activity,ArrayAdapter<String> arrayAdapter)
	{
		Log.i(TAG, "Load Friends is called...");
		SharedPreferences friendsInfo = null;
		if (mMode.equalsIgnoreCase(SINA)) {
			friendsInfo = activity.getSharedPreferences(SINA_FRIENDS_USER_LIST,Context.MODE_WORLD_READABLE);
		}
		else if (mMode.equalsIgnoreCase(TENCENT)) {
			friendsInfo = activity.getSharedPreferences(TENCENT_FRIENDS_USER_LIST,Context.MODE_WORLD_READABLE);
		} else if (mMode.equalsIgnoreCase(RENREN)) {
			friendsInfo = activity.getSharedPreferences(RENREN_FRIENDS_USER_LIST,Context.MODE_WORLD_READABLE);
		}
		if(friendsInfo==null)
			return false;
		arrayAdapter.clear();
		int iUserCount = friendsInfo.getInt(USER_COUNT, 0);
		for(int i=0; i<iUserCount;i++)
		{
			String userName = friendsInfo.getString(String.valueOf(i), null);
			if(userName!=null){
				arrayAdapter.add(userName);
			}
		}			
		Log.i(TAG, "Load Friends is success....");
		return true;		
	}
	
	public ArrayList<String> getSelectedNameList() {
		Log.i(TAG, "getSelectedNameList is called...");
		return mSelectedNameList;
	}

	public void setSelectedNameList(ArrayList<String> arrayList) {
		Log.i(TAG, "setSelectedNameList is called...");
		mSelectedNameList = arrayList;
	}

	public void setOAuthErrorMsg(String oauthErrorMsg) {
		this.mOAuthErrorMsg = oauthErrorMsg;
	}

	public String getOAuthErrorMsg() {
		return mOAuthErrorMsg;
	}
}
