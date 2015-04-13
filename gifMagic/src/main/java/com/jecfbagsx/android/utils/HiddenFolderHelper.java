package com.jecfbagsx.android.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class HiddenFolderHelper {
	private static final String HIDDEN_FOLDER_LIST = "";
	private static int increaseIndex = 0;

	public static List<String> getUserHiddenFolder(Context context) {
		List<String> lst = new ArrayList<String>();
		SharedPreferences folderList = context.getSharedPreferences(
				HIDDEN_FOLDER_LIST, Activity.MODE_PRIVATE);
		Map<String, String> values = (Map<String, String>) folderList.getAll();
		if (values != null && values.size() > 0) {
			for (Iterator<String> iterator = values.values().iterator(); iterator
					.hasNext();) {
				String url = iterator.next();
				lst.add(url);
			}
		}
		return lst;
	}

	public static void removeAllUserHiddenFolder(Context context) {
		SharedPreferences folderList = context.getSharedPreferences(
				HIDDEN_FOLDER_LIST, Activity.MODE_PRIVATE);
		folderList.edit().clear().commit();
	}

	public static boolean addUserHiddenFolder(Context context, String path) {
		boolean bFound = false;
		SharedPreferences folderList = context.getSharedPreferences(
				HIDDEN_FOLDER_LIST, Activity.MODE_PRIVATE);
		Map<String, String> values = (Map<String, String>) folderList.getAll();
		if (values != null && values.size() > 0) {
			for (Iterator<String> iterator = values.values().iterator(); iterator
					.hasNext();) {
				String url = iterator.next();
				if (url.equalsIgnoreCase(path)) {
					bFound = true;
					break;
				}
			}
		}

		if (!bFound) {
			String key = String.format("%d_%d", System.currentTimeMillis(), increaseIndex++);
			folderList.edit().putString(key, path)
					.commit();
		}
		return true;
	}
}
