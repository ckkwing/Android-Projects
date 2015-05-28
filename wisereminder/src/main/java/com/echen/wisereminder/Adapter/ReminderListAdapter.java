package com.echen.wisereminder.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/5/19.
 */
public class ReminderListAdapter extends BaseAdapter {
    private final String TAG = "ReminderListAdapter";
//    protected Context context = null;
    protected List<Reminder> reminderList = new ArrayList<>();
    protected LayoutInflater layoutInflater = null;

    public class ViewHolder
    {
        public long ID;
        public TextView Name;
    }

    public ReminderListAdapter(LayoutInflater layoutInflater, List<Reminder> reminderList)
    {
//        this.context = context;
//        if (null == context)
//            throw new NullPointerException("ReminderListAdapter: Passed Context is NULL!");
        this.reminderList = reminderList;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return reminderList.size();
    }

    @Override
    public Object getItem(int position) {
        return reminderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try
        {
            if (reminderList.isEmpty())
                return convertView;
            ViewHolder viewHolder;
            if (null == convertView)
            {
                convertView = layoutInflater.inflate(R.layout.reminder_item_view, null);
                viewHolder = new ViewHolder();
                viewHolder.Name = (TextView)convertView.findViewById(R.id.txtReminderName);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            Reminder reminder = reminderList.get(position);
            viewHolder.ID = reminder.getId();
            viewHolder.Name.setText(reminder.getName());
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
        return convertView;
    }

}
