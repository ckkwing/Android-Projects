package com.echen.androidcommon.Media;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import com.echen.androidcommon.Model.File;
import com.echen.androidcommon.Model.FileSystemInfo;

/**
 * Created by echen on 2015/1/27.
 */
public class Image extends File {
//    private int id;
//    private String title;
//    private String displayName;
//    private String mimeType;
//    private String path;
//    private long size;

    public Image() {
        super();
    }

    /**
     * @param id
     * @param title
     * @param displayName
     * @param mimeType
     * @param path
     * @param size
     */
    public Image(int id, String title, String displayName, String mimeType,
                 String path, long size) {
        super(id, title, displayName, mimeType, path, size);
    }

    @Override
    public Bitmap getThumbnail(Context context)
    {
        String uri = "";
        Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                context.getContentResolver(), this.id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            }
        cursor.close();
        if (uri.isEmpty())
            return null;
        return BitmapFactory.decodeFile(uri);
    }
}