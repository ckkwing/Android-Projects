package com.echen.wisereminder.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    protected List<Category> m_categoryList = new ArrayList<>();
    protected LayoutInflater m_layoutInflater = null;

    public class ViewHolder
    {
        public long id;
        public TextView categoryIcon;
        public TextView textView_Name;
    }

    public CategoryListAdapter(Context context, List<Category> categoryList)
    {
        this.context = context;
        if (null == context)
            throw new NullPointerException("CategoryListAdapter: Passed Context is NULL!");
        this.m_categoryList = categoryList;
        this.m_layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return m_categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return m_categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try
        {
            if (m_categoryList.isEmpty())
                return convertView;
            Category category = m_categoryList.get(position);
            ViewHolder viewHolder;
            if (null == convertView)
            {
                convertView = m_layoutInflater.inflate(R.layout.category_item_view, null);
                viewHolder = new ViewHolder();
                viewHolder.categoryIcon = (TextView)convertView.findViewById(R.id.categoryIco);
                viewHolder.textView_Name = (TextView)convertView.findViewById(R.id.txtCategoryName);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.id = category.getId();
//            RelativeLayout.MarginLayoutParams margins = new RelativeLayout.MarginLayoutParams(20,20);
//            margins.setMargins(5,5,5,5);
//            viewHolder.categoryIcon.setLayoutParams(margins);
            GradientDrawable bgShape = (GradientDrawable)viewHolder.categoryIcon.getBackground();
            bgShape.setColor(Color.parseColor(category.getColor()));
            viewHolder.textView_Name.setText(category.getName());
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
        return convertView;
    }
}
