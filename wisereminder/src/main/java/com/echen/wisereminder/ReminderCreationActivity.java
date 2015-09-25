package com.echen.wisereminder;

import android.content.Intent;
import android.os.Bundle;

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
//        m_btnAction.setText(R.string.common_save);
//        Calendar cal = Calendar.getInstance();
//        m_datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

//    @Override
//    public void actionBtnOnClick(View view) {
//        Reminder reminder = new Reminder(m_edtTitle.getText().toString());
//        reminder.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
//        DataManager.getInstance().addReminder(reminder);
//    }
}
