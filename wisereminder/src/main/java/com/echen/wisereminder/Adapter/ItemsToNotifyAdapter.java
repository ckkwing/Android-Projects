package com.echen.wisereminder.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.echen.wisereminder.ConsistentParameter;
import com.echen.wisereminder.ConsistentString;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.Model.Subject;
import com.echen.wisereminder.R;
import com.echen.wisereminder.ReminderEditActivity;
import com.echen.wisereminder.Utility.ReminderUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/10/22.
 */
public class ItemsToNotifyAdapter extends BaseAdapter {
    private final String TAG = "ItemsToNotifyAdapter";
    private Context m_context;
    protected LayoutInflater m_layoutInflater = null;
    private List<Reminder> m_reminderList = new ArrayList<>();

    public class ViewHolder
    {
        public long ID;
        public CheckBox ChkCompleted;
        public TextView Name;
        public ImageView Star;
        public ImageView PriorityColor;
    }

    public ItemsToNotifyAdapter(Context context)
    {
        m_context = context;
        this.m_layoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        List<Reminder> overdueReminders = DataManager.getInstance().getRemindersBySubject(Subject.Type.Overdue,true);
        m_reminderList.addAll(overdueReminders);
        List<Reminder> todayReminders = DataManager.getInstance().getRemindersBySubject(Subject.Type.Today, true);
        m_reminderList.addAll(todayReminders);
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
                viewHolder.PriorityColor = (ImageView)convertView.findViewById(R.id.priorityColorPanel);
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
                    ((Activity) m_context).startActivityForResult(intent, ConsistentParameter.REQUEST_CODE_MAINACTIVITY);
                }
            });

            viewHolder.Star.setBackgroundResource((true == reminder.getIsStar()) ? R.drawable.start_selected : R.drawable.start_normal);
            viewHolder.Star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean starToSet = !reminder.getIsStar();
                    reminder.setIsStar(starToSet);
                    if (!DataManager.getInstance().updateReminder(reminder))
                        reminder.setIsStar(!starToSet);
                    ItemsToNotifyAdapter.this.notifyDataSetChanged();
                }
            });

            viewHolder.ChkCompleted.setChecked(false);
            viewHolder.ChkCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reminder.setIsCompleted(true);
                    if (!DataManager.getInstance().updateReminder(reminder))
                        reminder.setIsCompleted(false);
                    else {
                        m_reminderList.remove(reminder);
                        ItemsToNotifyAdapter.this.notifyDataSetChanged();
                    }
                }
            });
            int iPriorityColor = ReminderUtility.getPriorityColorInt(reminder.getPriority(), m_context);
            ColorDrawable colorDrawable = new ColorDrawable(iPriorityColor);
            viewHolder.PriorityColor.setImageDrawable(colorDrawable);
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
        return convertView;
    }
}
