package uicommon.customcontrol;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by echen on 2015/11/16.
 */
public class PreferenceUtility {
    private static SharedPreferences m_SharedPreferences = null;
    private static SharedPreferences.Editor m_Editor = null;

    public static void init(Context context){
        if (null == m_SharedPreferences) {
            m_SharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context) ;
        }
    }

    public static void removeKey(String key){
        m_Editor = m_SharedPreferences.edit();
        m_Editor.remove(key);
        m_Editor.commit();
    }

    public static void removeAll(){
        m_Editor = m_SharedPreferences.edit();
        m_Editor.clear();
        m_Editor.commit();
    }

    public static void commitString(String key, String value){
        m_Editor = m_SharedPreferences.edit();
        m_Editor.putString(key, value);
        m_Editor.commit();
    }

    public static String getString(String key, String faillValue){
        return m_SharedPreferences.getString(key, faillValue);
    }

    public static void commitInt(String key, int value){
        m_Editor = m_SharedPreferences.edit();
        m_Editor.putInt(key, value);
        m_Editor.commit();
    }

    public static int getInt(String key, int failValue){
        return m_SharedPreferences.getInt(key, failValue);
    }

    public static void commitLong(String key, long value){
        m_Editor = m_SharedPreferences.edit();
        m_Editor.putLong(key, value);
        m_Editor.commit();
    }

    public static long getLong(String key, long failValue) {
        return m_SharedPreferences.getLong(key, failValue);
    }

    public static void commitBoolean(String key, boolean value){
        m_Editor = m_SharedPreferences.edit();
        m_Editor.putBoolean(key, value);
        m_Editor.commit();
    }

    public static Boolean getBoolean(String key, boolean failValue){
        return m_SharedPreferences.getBoolean(key, failValue);
    }
}
