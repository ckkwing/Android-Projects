package com.echen.androidcommon.Media;

import android.content.Context;
import android.graphics.Bitmap;

import com.echen.androidcommon.FileSystem.File;

/**
 * Created by echen on 2015/1/27.
 */
public class Audio extends File {

//    private int id;
//    private String title;
    private String album;
    private String artist;
//    private String path;
//    private String displayName;
//    private String mimeType;
    private long duration;
//    private long size;

    /**
     *
     */
    public Audio() {
        super();
    }

    /**
     * @param id
     * @param title
     * @param album
     * @param artist
     * @param path
     * @param displayName
     * @param mimeType
     * @param duration
     * @param size
     */
    public Audio(int id, String title, String album, String artist,
                 String path, String displayName, String mimeType, long duration,
                 long size) {
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
        return null;
    }
}
