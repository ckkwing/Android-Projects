package com.echen.wisereminder.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.R;

import java.util.ArrayList;
import java.util.List;

import be.webelite.ion.Icon;
import be.webelite.ion.IconView;

/**
 * Created by echen on 2015/10/10.
 */
public class ReminderPropertiesAdapter extends BaseAdapter {

    public class ViewHolder {
        IconView iconView;
        TextView txtTitle;
        TextView txtPropertyString;
    }

    class PropertyItem {
        Icon Icon;
        String Title;
        String PropertyString;
    }

    private final String TAG = "PropertiesAdapter";
    private Context m_context;
    private Reminder m_reminder;
    private LayoutInflater m_layoutInflater;
    private List<PropertyItem> m_propertyItemList = new ArrayList<>();
    private IPropertySelectedEvent m_iPropertySelectedEvent = null;

    public ReminderPropertiesAdapter(Context context, Reminder reminder) {
        if (null == context)
            throw new NullPointerException("ReminderPropertiesAdapter: Passed Context is NULL!");
        if (null == reminder)
            throw new NullPointerException("ReminderPropertiesAdapter: Passed Reminder is NULL!");
        this.m_context = context;
        this.m_reminder = reminder;
        this.m_layoutInflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ComposePropertyList();
    }

    public ReminderPropertiesAdapter(Context context, Reminder reminder, IPropertySelectedEvent propertySelectedEvent)
    {
        this(context, reminder);
        this.m_iPropertySelectedEvent = propertySelectedEvent;
    }

    @Override
    public int getCount() {
        return m_propertyItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return m_propertyItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (m_propertyItemList.isEmpty())
                return convertView;
            final ViewHolder viewHolder;
            if (null == convertView) {
                convertView = m_layoutInflater.inflate(R.layout.reminder_property_view, null);
                viewHolder = new ViewHolder();
                viewHolder.iconView = (IconView) convertView.findViewById(R.id.icon);
                viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtPropertyTitle);
                viewHolder.txtPropertyString = (TextView) convertView.findViewById(R.id.txtPropertyContent);
                convertView.setTag(viewHolder);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (m_iPropertySelectedEvent != null)
                            m_iPropertySelectedEvent.onPropertySelected(viewHolder.txtTitle.getText().toString());
                    }
                });
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final PropertyItem propertyItem = m_propertyItemList.get(position);
            viewHolder.iconView.setIcon(propertyItem.Icon);
            viewHolder.txtTitle.setText(propertyItem.Title);
            viewHolder.txtPropertyString.setText(propertyItem.PropertyString);


        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return convertView;
    }

    private void ComposePropertyList() {
        DateTime dueTime = m_reminder.getDueTime();
        PropertyItem dueTimeProperty = new PropertyItem();
        dueTimeProperty.Icon = Icon.ion_android_calendar;
        dueTimeProperty.Title = m_context.getString(R.string.reminder_due_date);
        dueTimeProperty.PropertyString = dueTime.toString();
        m_propertyItemList.add(dueTimeProperty);

        PropertyItem priorityProperty = new PropertyItem();
        priorityProperty.Icon = Icon.ion_flag;
        priorityProperty.Title = m_context.getString(R.string.priority);
        switch (m_reminder.getPriority()) {
            case LEVEL1:
                priorityProperty.PropertyString = m_context.getString(R.string.priority1);
                break;
            case LEVEL2:
                priorityProperty.PropertyString = m_context.getString(R.string.priority2);
                break;
            case LEVEL3:
                priorityProperty.PropertyString = m_context.getString(R.string.priority3);
                break;
            case LEVEL4:
                priorityProperty.PropertyString = m_context.getString(R.string.priority4);
                break;
        }
        m_propertyItemList.add(priorityProperty);

        PropertyItem alertTimeProperty = new PropertyItem();
        DateTime alertTime = m_reminder.getAlertTime();
        alertTimeProperty.Icon = Icon.ion_android_alarm;
        alertTimeProperty.Title = m_context.getString(R.string.reminder_alert_date);
        if (0 == alertTime.toUTCLong()) {
        } else {
            alertTimeProperty.PropertyString = alertTime.toString();
        }
        m_propertyItemList.add(alertTimeProperty);
    }

}
