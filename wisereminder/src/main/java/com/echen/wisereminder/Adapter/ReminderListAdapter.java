package com.echen.wisereminder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.echen.wisereminder.ConsistentString;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.MainActivity;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.R;
import com.echen.wisereminder.ReminderCreationActivity;
import com.echen.wisereminder.ReminderEditActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/5/19.
 */
public class ReminderListAdapter extends BaseAdapter {
    private final String TAG = "ReminderListAdapter";
    protected Context m_context = null;
    protected List<Reminder> m_reminderList = new ArrayList<>();
    protected LayoutInflater m_layoutInflater = null;

    public class ViewHolder
    {
        public long ID;
        public CheckBox ChkCompleted;
        public TextView Name;
        public ImageView Star;
    }

    public ReminderListAdapter(Context context, List<Reminder> reminderList)
    {
        this.m_context = context;
        if (null == context)
            throw new NullPointerException("ReminderListAdapter: Passed Context is NULL!");
        this.m_reminderList = reminderList;
        this.m_layoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateSource(List<Reminder> reminderList)
    {
        this.m_reminderList = reminderList;
        ReminderListAdapter.this.notifyDataSetChanged();
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
                convertView = m_layoutInflater.inflate(R.layout.reminder_item_view, null);
                viewHolder = new ViewHolder();
                viewHolder.Name = (TextView)convertView.findViewById(R.id.txtReminderName);
                viewHolder.Star = (ImageView)convertView.findViewById(R.id.imgStarFlag);
                viewHolder.ChkCompleted = (CheckBox)convertView.findViewById(R.id.chkIsCompleted);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            final Reminder reminder = m_reminderList.get(position);
            viewHolder.ID = reminder.getId();

            viewHolder.Name.setText(reminder.getName());
            viewHolder.Name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(m_context, ReminderEditActivity.class);
                    intent.putExtra(ConsistentString.PARAM_REMINDER_ID, reminder.getId());
                    m_context.startActivity(intent);
                }
            });

            viewHolder.Star.setBackgroundResource((true == reminder.getIsStar()) ? R.drawable.start_selected : R.drawable.start_normal);
            viewHolder.Star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean starToSet = !reminder.getIsStar();
                    reminder.setIsStar(starToSet);
                    if (!DataManager.getInstance().updateReminderByID(reminder))
                        reminder.setIsStar(!starToSet);
                    ReminderListAdapter.this.notifyDataSetChanged();
                }
            });

            viewHolder.ChkCompleted.setChecked(false);
            viewHolder.ChkCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reminder.setIsCompleted(true);
                    if (!DataManager.getInstance().updateReminderByID(reminder))
                        reminder.setIsCompleted(false);
                    else
                    {
                        m_reminderList.remove(reminder);
                        ReminderListAdapter.this.notifyDataSetChanged();
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
        return convertView;
    }

}
