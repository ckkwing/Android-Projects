package com.echen.wisereminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Reminder;

/**
 * Created by echen on 2015/5/28.
 */
public class ReminderEditActivity extends ReminderBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        long reminderID = intent.getLongExtra(ConsistentString.PARAM_REMINDER_ID, -1);
        if (-1 != reminderID)
        {
            Reminder reminder;
            reminder = DataManager.getInstance().getReminderByID(reminderID,false);
            try {
                m_reminder = (Reminder)reminder.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
//        setTitle(R.string.reminder_edition);

        m_edtReminderName.setText(m_reminder.getName());
//        long utcDueTime = m_reminder.getDueTime_UTC();
//        Date localDate = DateTime.getLocalTimeFromUTC(utcDueTime);
//        m_dueDateTime.update(localDate);
//        m_iconPriority.setTextColor(ReminderUtility.getPriorityColorInt(m_reminder.getPriority(), this));
//        updateDateTimeOnUI(m_dueDateTime);
    }

    @Override
    public void onAction() {
        super.onAction();
        boolean bRel = DataManager.getInstance().updateReminder(m_reminder);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ConsistentString.RESULT_BOOLEAN, bRel);
        Intent intent = getIntent();
        intent.putExtra(ConsistentString.BUNDLE_UNIT, bundle);
        setResult(ConsistentParameter.RESULT_CODE_REMINDEREDITIONACTIVITY, intent); //set resultCode
        finish();
    }
}
