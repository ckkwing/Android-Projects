package com.echen.arthur.ActivityAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echen.androidcommon.FileSystem.File;
import com.echen.androidcommon.FileSystem.FileSystemInfo;
import com.echen.androidcommon.FileSystem.Folder;
import com.echen.arthur.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/2/12.
 */
public class FolderAdapter extends BaseAdapter {
    private final String TAG = "FolderAdapter";
    protected Context context = null;
    protected List<Folder> folders = new ArrayList<>();
    protected LayoutInflater layoutInflater = null;
    protected String[] projection = {
            "_data"
    };

    public class ViewHolder {
        int id;
        TextView textView;
        ImageView imageViewOne;
        ImageView imageViewTwo;
        ImageView imageViewThree;
        ImageView imageViewFour;
    }

    public FolderAdapter(Context context, List<Folder> folders) {
        this.context = context;
        if (null == context)
            throw new NullPointerException("FolderAdapter: Passed Context is NULL!");
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.folders = folders;
    }


    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return folders.size();
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
        return folders.get(position);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder viewHolder;
            if (folders.isEmpty())
                return convertView;
            if (null == convertView) {
                convertView = layoutInflater.inflate(R.layout.folder_item_view,
                        null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.txtFolderName);
                viewHolder.imageViewOne = (ImageView) convertView
                        .findViewById(R.id.imgOne);
                viewHolder.imageViewTwo = (ImageView) convertView
                        .findViewById(R.id.imgTwo);
                viewHolder.imageViewThree = (ImageView) convertView
                        .findViewById(R.id.imgThree);
                viewHolder.imageViewFour = (ImageView) convertView
                        .findViewById(R.id.imgFour);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Folder folder = folders.get(position);
            viewHolder.id = position;
            viewHolder.textView.setText(folder.getDisplayName());

            int i = 0;
            ArrayList<ImageView> imageViews = new ArrayList<>();
            imageViews.add(viewHolder.imageViewOne);
            imageViews.add(viewHolder.imageViewTwo);
            imageViews.add(viewHolder.imageViewThree);
            imageViews.add(viewHolder.imageViewFour);
            for (ImageView imgView : imageViews) {
                imgView.setImageBitmap(null);
            }
            for (FileSystemInfo fileSystemInfo : folder.getChildren()) {
                if (4 == i)
                    break;
                if (!(fileSystemInfo instanceof File))
                    continue;
                File file = (File) fileSystemInfo;
                if (null == file)
                    continue;
                Bitmap thumbnail = file.getThumbnail(context);
                if (null != thumbnail)
                {
                    ImageView imageView = imageViews.get(i);
                    imageView.setImageBitmap(thumbnail);
                    i++;
                }
//                Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
//                        context.getContentResolver(), file.getId(),
//                        MediaStore.Images.Thumbnails.MINI_KIND,
//                        null);
//                if (cursor != null && cursor.getCount() > 0) {
//                    cursor.moveToFirst();//**EDIT**
//                    String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
//                    ImageView imageView = imageViews.get(i);
//                    if (null != imageView) {
//                        imageView.setImageURI(Uri.parse(uri));
//                        i++;
//                    }
//                }
//                cursor.close();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return convertView;
    }
}
