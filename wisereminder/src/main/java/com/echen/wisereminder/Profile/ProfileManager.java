package com.echen.wisereminder.Profile;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;

import com.echen.androidcommon.FileHelper;
import com.echen.wisereminder.R;
import com.echen.wisereminder.Utility.AppPathHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Type;

/**
 * Created by echen on 2015/10/27.
 */
public class ProfileManager {
    private volatile static ProfileManager instance;
    private Context m_context = null;
    private User m_user = null;

    public User getUser() {
        return this.m_user;
    }

    public static ProfileManager getInstance() {
        if (null == instance) {
            synchronized (ProfileManager.class) {
                instance = new ProfileManager();
            }
        }
        return instance;
    }

    public boolean initiate(Context context) {
        if (null == context)
            return false;
        this.m_context = context;
        loadCurrentUser();
        return true;
    }

    public void uninit() {

    }

    private void loadCurrentUser() {
        FileHelper.deleteFile(new File(AppPathHelper.getUserProfileFilePath()));
        Type type = new TypeToken<User>() {
        }.getType();
        Gson gson = new Gson();
        if (!FileHelper.isExist(AppPathHelper.getUserProfileFilePath())) {
            m_user = new DefaultUser(m_context.getString(R.string.profile_press_to_login), "eric@sohu.com");
//            String jsonUser = gson.toJson(m_user);
//            boolean isWriteSuccess = FileHelper.writeToFile(jsonUser, AppPathHelper.getUserProfileFilePath());
//            if (isWriteSuccess) {
//
//            } else {
//
//            }
        } else {
            StringBuffer strBuffer = FileHelper.readFromFile(AppPathHelper.getUserProfileFilePath());
            m_user = gson.fromJson(strBuffer.toString(), User.class);
        }
    }

}
