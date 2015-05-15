package com.echen.wisereminder.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echen.wisereminder.Model.Category;
import com.echen.wisereminder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/4/27.
 */
public class CategoryListAdapter extends BaseAdapter {
    private final String TAG = "CategoryListAdapter";
    protected Context context = null;
    protected List<Category> categoryList = new ArrayList<>();
    protected LayoutInflater layoutInflater = null;

    public class ViewHolder
    {
        public long id;
        public TextView textView_Name;
    }

    public CategoryListAdapter(Context context, List<Category> categoryList)
    {
        this.context = context;
        if (null == context)
            throw new NullPointerException("CategoryListAdapter: Passed Context is NULL!");
        this.categoryList = categoryList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try
        {
            if (categoryList.isEmpty())
                return convertView;
            ViewHolder viewHolder;
            if (null == convertView)
            {
                convertView = layoutInflater.inflate(R.layout.category_item_view, null);
                viewHolder = new ViewHolder();
                viewHolder.textView_Name = (TextView)convertView.findViewById(R.id.txtCategoryName);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            Category category = categoryList.get(position);
            viewHolder.id = category.getId();
            viewHolder.textView_Name.setText(category.getName());
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
        return convertView;
    }
}
