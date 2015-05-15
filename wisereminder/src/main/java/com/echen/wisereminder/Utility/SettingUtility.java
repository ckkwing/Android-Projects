package com.echen.wisereminder.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by echen on 2015/5/15.
 */
public class SettingUtility {
    private Context context = null;
    private volatile static SettingUtility instance = null;
    private SharedPreferences sharedPreferences;

    private final String KEY_FIRST_USE_NOTE = "key_first_use_note";

    public static SettingUtility getInstance()
    {
        if (null == instance)
        {
            synchronized (SettingUtility.class)
            {
                if (null == instance)
                {
                    instance = new SettingUtility();
                }
            }
        }
        return instance;
    }

    public boolean initiate(Context context)
    {
        if (null == context)
            return false;
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        return true;
    }

    public void  uninit()
    {
    }

    public boolean getIsFirstUse()
    {
        return sharedPreferences.getBoolean(KEY_FIRST_USE_NOTE, true);
    }
    public void setIsFirstUse(boolean isFirstUse)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_FIRST_USE_NOTE, isFirstUse);
        editor.apply();
    }
}
