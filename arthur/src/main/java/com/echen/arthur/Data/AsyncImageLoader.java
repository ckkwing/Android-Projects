package com.echen.arthur.Data;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.echen.androidcommon.Utility.ImageUtility;

/**
 * Created by echen on 2015/2/17.
 */
public class AsyncImageLoader {
    private final String TAG = "AsyncImageLoader";
    private HashMap<String, SoftReference<Drawable>> imageCache;
    private BlockingQueue queue;
    private ThreadPoolExecutor executor;
    private final int minSideLength = -1;
    private final int maxNumOfPixels = 256 * 256;

    public interface ImageCallback {
        public void imageLoaded(Drawable imageDrawable, String imageUrl);
    }

    public AsyncImageLoader() {
        imageCache = new HashMap<String, SoftReference<Drawable>>();
        queue = new LinkedBlockingDeque();
        executor = new ThreadPoolExecutor(1, 50, 180, TimeUnit.SECONDS, queue);
    }

    public Drawable loadDrawable(final Context context,final int imageId, final String imageUrl, final ImageCallback imageCallback) {
        if (imageCache.containsKey(imageUrl)) {
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            Drawable drawable = softReference.get();
            if (null != drawable)
                return drawable;
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                imageCallback.imageLoaded((Drawable) msg.obj, imageUrl);
            }
        };

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = loadThumbnailFromUrl(context, imageId, imageUrl);
                if (null == drawable)
                {
                    drawable = autoLoadImageFromUrl(imageUrl);
                }
//                Drawable drawable = autoLoadImageFromUrl(imageUrl);
                if (null != drawable) {
                    imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                    Message msg = handler.obtainMessage(0, drawable);
                    handler.sendMessage(msg);
                }
            }
        });

        return null;
    }

    public Drawable loadImageFromUrl(String imageUrl) {
        Drawable drawable = Drawable.createFromPath(imageUrl);
        return drawable;
    }

    public Drawable autoLoadImageFromUrl(String imageUrl){
        Drawable drawable = null;
        Bitmap bmp = ImageUtility.autoLoadImageFromUrl(imageUrl, minSideLength, maxNumOfPixels);
        drawable = new BitmapDrawable(Resources.getSystem(),bmp);
        return drawable;
    }

    public Drawable loadThumbnailFromUrl(Context context,int imageId, String imageUrl){
        Drawable drawable = null;
        String thumbnailUrl = ImageUtility.getThumbnailUrl(context.getContentResolver(), imageId, MediaStore.Images.Thumbnails.MINI_KIND);
        if (!thumbnailUrl.isEmpty())
            drawable = Drawable.createFromPath(thumbnailUrl);
        return drawable;
    }
}
