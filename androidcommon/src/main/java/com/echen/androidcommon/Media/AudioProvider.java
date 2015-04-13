package com.echen.androidcommon.Media;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/1/27.
 */
public class AudioProvider implements IMediaProvider {
    private Context context = null;

    public AudioProvider(Context context) {
        this.context = context;
    }

    @Override
    public List<?> getList() {
        List<Audio> list = new ArrayList<Audio>();
        if (null == context)
            return list;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null,null);
        if (null == cursor)
            return  list;
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            String title = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String album = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            String artist = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String path = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            String displayName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            String mimeType = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
            long duration = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            long size = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
            Audio audio = new Audio(id, title, album, artist, path,
                    displayName, mimeType, duration, size);
            list.add(audio);
        }
        cursor.close();
        return list;
    }
}
