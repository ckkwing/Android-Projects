package com.echen.androidcommon.Media;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import com.echen.androidcommon.FileSystem.File;

/**
 * Created by echen on 2015/1/27.
 */
public class Video extends File {

    private String album;
    private String artist;
    private long duration;

    /**
     *
     */
    public Video() {
        super();
    }

    /**
     * @param id
     * @param title
     * @param album
     * @param artist
     * @param displayName
     * @param mimeType
     * @param data
     * @param size
     * @param duration
     */
    public Video(int id, String title, String album, String artist,
                 String displayName, String mimeType, String path, long size,
                 long duration) {
        super(id, title, displayName, mimeType, path, size);
        this.album = album;
        this.artist = artist;
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public Bitmap getThumbnail(Context context) {
        Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                context.getContentResolver(), this.id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null);
        return bitmap;
    }
}
