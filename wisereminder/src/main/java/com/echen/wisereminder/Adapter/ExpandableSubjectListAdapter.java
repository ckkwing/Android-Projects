package com.echen.wisereminder.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echen.wisereminder.Model.IListItem;
import com.echen.wisereminder.Model.Subject;
import com.echen.wisereminder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/6/3.
 */
public class ExpandableSubjectListAdapter extends BaseExpandableListAdapter {

    private final String TAG = "ExpandableSubjectListAdapter";
    protected Context context = null;
    protected List<Subject> subjectList = new ArrayList<>();
    protected LayoutInflater layoutInflater = null;

    public ExpandableSubjectListAdapter(Context context, List<Subject> subjectList)
    {
        this.context = context;
        if (null == context)
            throw new NullPointerException("ExpandableSubjectListAdapter: Passed Context is NULL!");
        this.subjectList = subjectList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return subjectList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return subjectList.get(groupPosition).getChildren().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return subjectList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<IListItem> children = subjectList.get(groupPosition).getChildren();
        if (children.size() > 0)
            return children.get(childPosition);
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.subject_item_view, null);
        Subject subject = subjectList.get(groupPosition);
        if (null != subject) {
            TextView subjectIcon = (TextView)convertView.findViewById(R.id.subjectIco);
            GradientDrawable bgShape = (GradientDrawable  )subjectIcon.getBackground();
            bgShape.setColor(Color.BLACK);
//            subjectIcon.setImageResource(R.drawable.img_calendar);
            TextView textView = (TextView) convertView.findViewById(R.id.subjectTitle);
            textView.setText(subject.getName());
            ImageView expandIcon = (ImageView)convertView.findViewById(R.id.subjectExpandIco);
            if (0 == subject.getChildren().size())
            {
                expandIcon.setVisibility(View.GONE);
            }
            else {
                if (isExpanded) {
                    expandIcon.setBackgroundResource(R.drawable.img_collapse);
                } else {
                    expandIcon.setBackgroundResource(R.drawable.img_expand);
                }
                expandIcon.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.category_item_view, null);
        Subject subject = subjectList.get(groupPosition);
        if (null != subject && subject.getChildren().size() > 0) {
            IListItem child = subject.getChildren().get(childPosition);
            if (null != child) {
                TextView subjectIcon = (TextView)convertView.findViewById(R.id.subjectIco);
                GradientDrawable bgShape = (GradientDrawable)subjectIcon.getBackground();
                bgShape.setColor(Color.parseColor(child.getItemColor()));
                TextView textView = (TextView) convertView.findViewById(R.id.txtCategoryName);
                textView.setText(child.getItemName());
            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
