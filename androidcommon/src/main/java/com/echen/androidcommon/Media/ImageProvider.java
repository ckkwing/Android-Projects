package com.echen.androidcommon.Media;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/1/27.
 */
public class ImageProvider implements IMediaProvider {
    private Context context = null;

    public ImageProvider(Context context){
        this.context = context;
    }

    @Override
    public List<?> getList() {
        List<Image> list = new ArrayList<Image>();
        if (null == context)
            return list;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,null,null,null);
        if (null == cursor)
            return  list;
        while (cursor.moveToNext())
        {
            int id = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            String title = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
            String path = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            String displayName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            String mimeType = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
            long size = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
            Image image = new Image(id, title, displayName, mimeType,
                    path, size);
            Uri uri = Uri.parse(path);

            list.add(image);
        }
        cursor.close();
        return list;
    }
}
