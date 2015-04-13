package com.jecfbagsx.android.gifmagic;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.jecfbagsx.android.gifmagic.R;
import com.jecfbagsx.android.gifmanage.OAuthManager;
import com.jecfbagsx.android.utils.TitlebarHelper;

public class WeiboAtFriendsActivity extends Activity{  
	private static final String TAG = "WeiboAtFriendsActivity";
	
	private enum ListMode
	{
		HistoryListMode,
		FirendsListMode,
		SearchListMode,
	}
	
	private ListMode mListMode = ListMode.HistoryListMode;

	private Button mBtnHistoryList = null;
	private Button mBtnFriendsList = null;
	
	private Button mBtnDone = null;
	private Button mBtnCancel = null;
	
	private ArrayAdapter<String> mHistoryListItemAdapter = null;	
	private ArrayAdapter<String> mFriendsListItemAdapter = null;
	private ArrayAdapter<String> mSearchListItemAdapter = null;
	private ListView mListView = null;
	
	private EditText mFindEditText = null; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibo_atfriends);
		
		TitlebarHelper.setbackground(this);
		setTitle(R.string.weibo_atfriends);
		
		Log.i(TAG, "Weibo AtFriends Activity create...");
		
		mHistoryListItemAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice);
		OAuthManager.getInstance().LoadHistoryUser(WeiboAtFriendsActivity.this,mHistoryListItemAdapter);
		
		mFriendsListItemAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice);
		OAuthManager.getInstance().LoadFriends(WeiboAtFriendsActivity.this,mFriendsListItemAdapter);
		
		mSearchListItemAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice);
		
		mFindEditText = (EditText) findViewById(R.id.weibo_atfriends_search_edit);
		mFindEditText.addTextChangedListener(textChangedWatcher);
				
		mBtnHistoryList = (Button) findViewById(R.id.weibo_atfriends_historylist);		
		mBtnHistoryList.setOnClickListener(historyListBtnListener);
		
		mBtnFriendsList = (Button) findViewById(R.id.weibo_atfriends_friendslist);		
		mBtnFriendsList.setOnClickListener(friendsListBtnListener);
		
		mListView = (ListView) findViewById(R.id.weibo_atfriends_listview);
		mListView.setItemsCanFocus(true);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mListView.setAdapter(mHistoryListItemAdapter);
		mListView.setOnItemClickListener(itemClickListener);
		mHistoryListItemAdapter.setNotifyOnChange(true);
		
		mBtnDone = (Button) findViewById(R.id.weibo_atfriends_done);
		mBtnDone.setOnClickListener(doneBtnListener);
		
		mBtnCancel = (Button) findViewById(R.id.weibo_atfriends_cancel);
		mBtnCancel.setOnClickListener(cancelBtnListener);

	}	
	
	private ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			Log.i(TAG, "onItemClick is trigered...");			
			ArrayAdapter<String> arrayAdapter = (ArrayAdapter<String>)arg0.getAdapter();
			CheckedTextView checkedTextView = (CheckedTextView)arg1;
			boolean bIsChecked = checkedTextView.isChecked();
			mListView.setItemChecked(arg2,!bIsChecked);			
		}		
	};	
	
	private TextWatcher textChangedWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			Log.i(TAG, "afterTextChanged..., s == "+s.toString());	
			mSearchListItemAdapter.clear();
			String prefix = mFindEditText.getText().toString();			
			ArrayList<String> historyList = new ArrayList<String>();
			for(int i=0; i<mHistoryListItemAdapter.getCount();i++)
			{
				String UserName = mHistoryListItemAdapter.getItem(i); 
				if(UserName.toLowerCase().startsWith(prefix.toLowerCase()))
				{
					historyList.add(UserName);
					mSearchListItemAdapter.add(UserName);
				}
			}			
			
			for(int i=0; i<mFriendsListItemAdapter.getCount();i++)
			{
				String UserName = mFriendsListItemAdapter.getItem(i);				
				if(UserName.toLowerCase().startsWith(prefix.toLowerCase()))
				{
					if(!historyList.contains(UserName))
					{
						mSearchListItemAdapter.add(UserName);
					}
				}
			}
			
			mListView.setAdapter(mSearchListItemAdapter);
			mSearchListItemAdapter.setNotifyOnChange(true);	
			mListMode = ListMode.SearchListMode;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			Log.i(TAG, "beforeTextChanged..., s == "+s.toString());		
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,int count) {
			Log.i(TAG, "onTextChanged..., s == "+s.toString());					
		}		
	};
	
	private Button.OnClickListener historyListBtnListener = new Button.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if(mFindEditText.getText().length()>0){
				mFindEditText.getText().clear();
			}
			mListView.setAdapter(mHistoryListItemAdapter);
			mHistoryListItemAdapter.setNotifyOnChange(true);
			mListMode = ListMode.HistoryListMode;
		}
	};

	private Button.OnClickListener friendsListBtnListener = new Button.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if(mFindEditText.getText().length()>0){
				mFindEditText.getText().clear();
			}
			mListView.setAdapter(mFriendsListItemAdapter);
			mFriendsListItemAdapter.setNotifyOnChange(true);
			mListMode = ListMode.FirendsListMode;
		}
	};	
	
	private Button.OnClickListener doneBtnListener = new Button.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			ArrayList<String> nameList = new ArrayList<String>();
			nameList.clear();
			ArrayAdapter<String> arrayAdapter = (ArrayAdapter<String>)mListView.getAdapter();
			long[] Ids = mListView.getCheckItemIds();
			for(int i=0; i<Ids.length;i++)
			{
				String userName = arrayAdapter.getItem((int)Ids[i]);
				nameList.add(userName);
			}			
			OAuthManager.getInstance().setSelectedNameList(nameList);
			setResult(RESULT_OK);
			finish();
		}
	};

	private Button.OnClickListener cancelBtnListener = new Button.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			finish();
		}
	};	
}
