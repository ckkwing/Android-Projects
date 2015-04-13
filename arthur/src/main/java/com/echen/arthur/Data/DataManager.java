package com.echen.arthur.Data;

import android.content.Context;
import android.provider.MediaStore;

import com.echen.androidcommon.Media.Audio;
import com.echen.androidcommon.Media.IMediaProvider;
import com.echen.androidcommon.Media.Image;
import com.echen.androidcommon.Media.MediaCenter;
import com.echen.androidcommon.Media.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/2/12.
 */
public class DataManager {
    private Context context = null;
    private IMediaProvider imageProvider = null;
    private IMediaProvider videoProvider = null;
    private IMediaProvider audioProvider = null;
    private List<Image> images;
    private List<Video> videos;
    private List<Audio> audios;

    private volatile static DataManager instance;

    public static DataManager getInstance() {
        if (null == instance) {
            synchronized (DataManager.class) {
                if (null == instance) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    public boolean init(Context context) {
        this.context = context;
        if (null == this.context)
            return false;
        imageProvider = MediaCenter.CreateMediaProvider(this.context, MediaCenter.MediaType.Image);
        videoProvider = MediaCenter.CreateMediaProvider(this.context, MediaCenter.MediaType.Video);
        audioProvider = MediaCenter.CreateMediaProvider(this.context, MediaCenter.MediaType.Audio);
        if (null == imageProvider || null == videoProvider || null == audioProvider)
            return false;
        return true;
    }

    public void uninit() {
        imageProvider = null;
        videoProvider = null;
        audioProvider = null;
    }

    public List<?> getList(MediaCenter.MediaType mediaType)
    {
        List<?> list = new ArrayList<>();
        switch (mediaType)
        {
            case Image:
                list = getImages();
                break;
            case Video:
                list = getVideos();
                break;
            case Audio:
                list = getAudios();
                break;
            default:
                break;
        }
        return list;
    }

    public List<Image> getImages() {
        if (null == images)
            return (List<Image>) imageProvider.getList();
        else
            return images;
    }

    public List<Image> getImagesByForce() {
        return (List<Image>) imageProvider.getList();
    }

    public List<Video> getVideos() {
        if (null == videos)
            return (List<Video>) videoProvider.getList();
        else
            return videos;
    }

    public List<Video> getVideosByForce() {
        return (List<Video>) videoProvider.getList();
    }

    public List<Audio> getAudios() {
        if (null == audios)
            return (List<Audio>) audioProvider.getList();
        else
            return audios;
    }

    public List<Audio> getAudiosByForce() {
        return (List<Audio>) audioProvider.getList();
    }
}
