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
 * Created by echen on 2015/10/21.
 */
public class NotificationItemListAdapter extends BaseAdapter {

    private final String TAG = "NotificationItemAdapter";
    protected Context m_context = null;
    protected List<Reminder> m_reminderList = new ArrayList<>();
    protected LayoutInflater m_layoutInflater = null;

    class ViewHolder
    {
        TextView txtReminderName;
    }

    public NotificationItemListAdapter(Context context, List<Reminder> reminderList)
    {
        this.m_context = context;
        this.m_reminderList = reminderList;
        m_layoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return m_reminderList.size();
    }

    @Override
    public Object getItem(int position) {
        return m_reminderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try
        {
            if (m_reminderList.isEmpty())
                return convertView;
            ViewHolder viewHolder;
            if (null == convertView)
            {
                convertView = m_layoutInflater.inflate(R.layout.notification_item_view, null);
                viewHolder = new ViewHolder();
                viewHolder.txtReminderName = (TextView)convertView.findViewById(R.id.txtNotificationItemName);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            final Reminder reminder = m_reminderList.get(position);
            viewHolder.txtReminderName.setText(reminder.getName());


        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
        return convertView;
    }
}
