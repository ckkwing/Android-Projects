package com.echen.wisereminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Utility.ReminderUtility;

import java.util.Date;

/**
 * Created by echen on 2015/5/28.
 */
public class ReminderEditActivity extends ReminderBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.reminder_edition);
        Intent intent = getIntent();
        long reminderID = intent.getLongExtra(ConsistentString.PARAM_REMINDER_ID, -1);
        if (-1 != reminderID)
        {
            m_reminder = DataManager.getInstance().getReminderByID(reminderID);
        }
        m_edtInfo.setText(m_reminder.getName());
        long utcDueTime = m_reminder.getDueTime_UTC();
        Date localDate = DateTime.getLocalTimeFromUTC(utcDueTime);
        m_dateTime.update(localDate);
        m_iconPriority.setTextColor(ReminderUtility.getPriorityColorInt(m_reminder.getPriority(), this));
        updateDateTimeOnUI(m_dateTime);
    }

    @Override
    public void actionBtnOnClick(View view) {
        super.actionBtnOnClick(view);
        DataManager.getInstance().updateReminder(m_reminder);
    }
}
