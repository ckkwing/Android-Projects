package com.echen.wisereminder;

import android.content.Intent;
import android.os.Bundle;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Data.DataManager;

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
            m_reminderToEdit = DataManager.getInstance().getReminderByID(reminderID);
        }
        m_edtInfo.setText(m_reminderToEdit.getName());
        long utcDueTime = m_reminderToEdit.getDueTime_UTC();
        Date localDate = DateTime.getLocalTimeFromUTC(utcDueTime);
        m_dateTime.update(localDate);
        updateDateTimeOnUI(m_dateTime);
//        m_btnAction.setText(R.string.common_edit);
    }

//    @Override
//    public void actionBtnOnClick(View view) {
//
//    }
}
