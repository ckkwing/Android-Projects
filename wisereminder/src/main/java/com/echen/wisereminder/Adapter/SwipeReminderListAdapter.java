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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.echen.androidcommon.DeviceHelper;
import com.echen.wisereminder.ConsistentParameter;
import com.echen.wisereminder.ConsistentString;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.R;
import com.echen.wisereminder.ReminderEditActivity;
import com.echen.wisereminder.Utility.ReminderUtility;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/11/6.
 */
public class SwipeReminderListAdapter extends BaseAdapter {

    private final String TAG = "SwipeReminderAdapter";
    protected Context m_context = null;
    protected List<Reminder> m_reminderList = new ArrayList<>();
    protected LayoutInflater m_layoutInflater = null;
    private SwipeListView m_SwipeListView ;
    private int m_deviceWidth = 80;

    public class ViewHolder
    {
        public long ID;
        public CheckBox ChkCompleted;
        public TextView Name;
        public ImageView Star;
        public ImageView PriorityColor;
        public Button BtnEdit;
        public Button BtnDelete;
    }

//    class ViewHolder{
//        TextView mFrontText ;
//        Button mBackEdit,mBackDelete ;
//    }

    public SwipeReminderListAdapter(Context context, List<Reminder> reminderList, SwipeListView swipeListView)
    {
        this.m_context = context;
        if (null == context)
            throw new NullPointerException("SwipeReminderListAdapter: Passed Context is NULL!");
        this.m_reminderList = reminderList;
        this.m_layoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.m_SwipeListView = swipeListView;
        m_deviceWidth = DeviceHelper.getDisplayMetrics(m_context).widthPixels;
    }

    public void updateSource(List<Reminder> reminderList)
    {
        this.m_reminderList = reminderList;
        SwipeReminderListAdapter.this.notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        try
        {
            if (m_reminderList.isEmpty())
                return convertView;
            ViewHolder viewHolder;
            if (null == convertView)
            {
                convertView = m_layoutInflater.inflate(R.layout.swipe_reminder_item_view, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.Name = (TextView)convertView.findViewById(R.id.txtReminderName);
                viewHolder.Star = (ImageView)convertView.findViewById(R.id.imgStarFlag);
                viewHolder.ChkCompleted = (CheckBox)convertView.findViewById(R.id.chkIsCompleted);
                viewHolder.PriorityColor = (ImageView)convertView.findViewById(R.id.priorityColorPanel);
                viewHolder.BtnEdit = (Button)convertView.findViewById(R.id.btnEdit);
                viewHolder.BtnDelete = (Button)convertView.findViewById(R.id.btnDelete);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            final Reminder reminder = m_reminderList.get(position);
            viewHolder.ID = reminder.getId();

            viewHolder.Name.setText(reminder.getName());
//            viewHolder.Name.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(m_context, ReminderEditActivity.class);
//                    intent.putExtra(ConsistentString.PARAM_REMINDER_ID, reminder.getId());
//                    ((Activity) m_context).startActivityForResult(intent, ConsistentParameter.REQUEST_CODE_MAINACTIVITY);
//                }
//            });

            viewHolder.Star.setBackgroundResource((true == reminder.getIsStar()) ? R.drawable.start_selected : R.drawable.start_normal);
            viewHolder.Star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean starToSet = !reminder.getIsStar();
                    reminder.setIsStar(starToSet);
                    if (!DataManager.getInstance().updateReminder(reminder))
                        reminder.setIsStar(!starToSet);
                    SwipeReminderListAdapter.this.notifyDataSetChanged();
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
                        SwipeReminderListAdapter.this.notifyDataSetChanged();
                    }
                }
            });
            int iPriorityColor = ReminderUtility.getPriorityColorInt(reminder.getPriority(), m_context);
            ColorDrawable colorDrawable = new ColorDrawable(iPriorityColor);
            viewHolder.PriorityColor.setImageDrawable(colorDrawable);

            viewHolder.BtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(m_context, ReminderEditActivity.class);
                    intent.putExtra(ConsistentString.PARAM_REMINDER_ID, reminder.getId());
                    ((Activity) m_context).startActivityForResult(intent, ConsistentParameter.REQUEST_CODE_MAINACTIVITY);
                }
            });

            viewHolder.BtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    long lRel = DataManager.getInstance().deleteReminder(reminder);
                    if (lRel > 0)
                    {
                        m_reminderList.remove(reminder);
                    }

                    //Close animation
                    m_SwipeListView.closeAnimate(position);
                    //Call dismiss function
                    m_SwipeListView.dismiss(position);
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
