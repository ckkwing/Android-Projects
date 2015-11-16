package com.echen.androidcommon.Utility;

import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by echen on 2015/2/12.
 */
public class PathUtility {

    public static String getInternalStorage() {
        File SDRoot = android.os.Environment.getExternalStorageDirectory();
        return SDRoot.getAbsolutePath();
    }

//    public static String getExternalStorage() {
//        File SDRoot = android.os.Environment.getExternalStorageDirectory();
//        String external = SDRoot.getAbsolutePath() + "-ext";
//        return external;
//    }

    public static boolean isSDCardExist()
    {
        String sdCardStatus = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(sdCardStatus))
            return true;
        else
            return false;
    }

    public static File createDir(Context context, String dirName)
    {
        File dir = null;
        String path = null;
        if (PathUtility.isSDCardExist())
        {
            File sdcardDir = Environment.getExternalStorageDirectory();
            path= sdcardDir.getPath()+"/" + dirName;
        }
        else
        {
            File cacheDir = context.getCacheDir();
            path = cacheDir.getPath();
        }
        if (null != path)
        {
            dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
        }
        return dir;
    }

    public static String getParent(String path)
    {
        String parent = "";
        if (null == path)
            return parent;

        if (!path.contains("/"))
            return parent;
        parent = path.substring(0, path.lastIndexOf("/"));
        return parent;
    }


}
