package com.echen.androidcommon.Media;

import android.content.Context;
import android.provider.MediaStore;

/**
 * Created by echen on 2015/2/11.
 */
public class MediaCenter {
    public enum MediaType
    {
        Image,
        Video,
        Audio
    }

    public static IMediaProvider CreateMediaProvider(Context context, MediaType mediaType)
    {
        IMediaProvider iMediaProvider = null;
        switch (mediaType)
        {
            case Image:
                iMediaProvider = new ImageProvider(context);
                break;
            case Video:
                iMediaProvider = new VideoProvider(context);
                break;
            case Audio:
                iMediaProvider = new AudioProvider(context);
                break;
            default:
                break;
        }
        return iMediaProvider;
    }
}
