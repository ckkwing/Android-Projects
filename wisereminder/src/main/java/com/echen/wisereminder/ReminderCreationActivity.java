package com.echen.wisereminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.Utility.ReminderUtility;

/**
 * Created by echen on 2015/5/19.
 */
public class ReminderCreationActivity extends ReminderBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.reminder_creation);
        m_reminder = new Reminder();
        m_reminder.setPriority(Reminder.Priority.LEVEL4);
        m_iconPriority.setTextColor(ReminderUtility.getPriorityColorInt(m_reminder.getPriority(), this));
        updateDateTimeOnUI(m_dateTime);
    }

    @Override
    public void actionBtnOnClick(View view) {
        super.actionBtnOnClick(view);
        DataManager.getInstance().addReminder(m_reminder);
    }
}
