package com.echen.androidcommon.Model;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by echen on 2015/3/5.
 */
public abstract class File extends FileSystemInfo {

    public File(){
        super();
    }

    public File(int id, String title, String displayName, String mimeType,
                String path, long size)
    {
        super(id, title, displayName, mimeType, path, size);
    }

    public abstract Bitmap getThumbnail(Context context);

}
