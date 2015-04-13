package com.echen.arthur.ActivityAdapter;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.echen.androidcommon.Media.Image;
import com.echen.androidcommon.Utility.ImageUtility;
import com.echen.arthur.Data.AsyncImageLoader;
import com.echen.arthur.R;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/2/17.
 */
public class ImageAdapter extends BaseAdapter {
    private final String TAG = "ImageAdapter";
    protected Context context = null;
    protected List<Image> images = new ArrayList<>();
    protected LayoutInflater layoutInflater = null;
    protected AsyncImageLoader asyncImageLoader;
    protected Picasso picasso = null;

    public class ViewHolder
    {
        ImageView imageView;
    }

    public ImageAdapter(Context context, List<Image> images)
    {
        this.context = context;
        if (null == context)
            throw new NullPointerException("ImageAdapter: Passed Context is NULL!");
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.images = images;
        asyncImageLoader = new AsyncImageLoader();

//        int memClass = ((ActivityManager) context
//                .getSystemService(Context.ACTIVITY_SERVICE))
//                .getLargeMemoryClass();
//        int cacheSize = 1024 * 1024 * memClass / 4;
//        picasso = new Picasso.Builder(context).memoryCache(
//                new LruCache(cacheSize)).build();
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return images.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            final ViewGroup m_parent = parent;
            ViewHolder viewHolder;
            if (images.isEmpty())
                return convertView;
            if (null == convertView) {
                convertView = layoutInflater.inflate(R.layout.image_item_view,
                        null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView)convertView.findViewById(R.id.imgThumb);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Image image = images.get(position);
            viewHolder.imageView.setId(position);
            viewHolder.imageView.setTag(image.getPath());

            System.out.println("position " + position + " convertView " + convertView.toString() + " imageView " + viewHolder.imageView.toString());
//            picasso.with(context).load(new File(image.getPath()))
//                    .resize(256,256).centerCrop().into(viewHolder.imageView);

            Drawable cachedDrawable = asyncImageLoader.loadDrawable(context, image.getId(), image.getPath(), new AsyncImageLoader.ImageCallback() {
                        @Override
                        public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                            ImageView imageViewByTag = (ImageView)m_parent.findViewWithTag(imageUrl);
                            if (null != imageViewByTag) {
//                                Bitmap bitmap = ImageUtility.drawableToBitmap(imageDrawable);
//                                Bitmap scaledBitmap = ImageUtility.centerSquareScaleBitmap(bitmap, R.dimen.image_thumbnail_width);
//                                imageViewByTag.setImageBitmap(scaledBitmap);

                                imageViewByTag.setImageDrawable(imageDrawable);
                            }else {
                                // load image failed from Internet
                            }
                        }
                    });
            if (cachedDrawable == null) {
                viewHolder.imageView.setImageResource(android.R.drawable.ic_menu_search);
            } else {
                viewHolder.imageView.setImageDrawable(cachedDrawable);
            }
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return convertView;
    }

}
