package com.echen.wisereminder.Utility;

import com.echen.androidcommon.Utility.PathUtility;

import java.io.File;

/**
 * Created by echen on 2015/11/3.
 */
public class AppPathHelper {

    public static final String PATH_ROOT = "WiseReminder";

    //Folder
    public static final String FOLDER_USER = "user";

    //File
    public static final String FILE_USER_PROFILE = "user.json";
    public static final String FILE_AVATAR = "avatar.png";

    public static String getAppRootPath() {
        String mPath = "";
        if (PathUtility.isSDCardExist()) {
            File SDFile = android.os.Environment.getExternalStorageDirectory();
            mPath = SDFile.getAbsolutePath() + File.separator + PATH_ROOT;
            File newPath = new File(mPath);
            if (!newPath.exists()) {
                newPath.mkdirs();
            }
        }
        return mPath;
    }

    public static String getAvatarFilePath()
    {
        return AppPathHelper.getUserRootPath() + File.separator + FILE_AVATAR;
    }

    public static String getUserRootPath()
    {
        String filePath = AppPathHelper.getAppRootPath() + File.separator + FOLDER_USER;
        File file = new File(filePath);
        if (!file.exists())
        {
            file.mkdirs();
        }
        return filePath;
    }

    public static String getUserProfileFilePath()
    {
        return AppPathHelper.getUserRootPath() + File.separator + FILE_USER_PROFILE;
    }
}
