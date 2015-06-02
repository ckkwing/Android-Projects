package com.echen.wisereminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Reminder;

/**
 * Created by echen on 2015/5/19.
 */
public class ReminderCreationActivity extends ReminderBaseActivity {

    private long ownerID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ownerID = intent.getLongExtra(ConsistentString.PARAM_CATEGORY_ID, -1);
        btnAction.setText(R.string.common_save);
    }

    @Override
    public void actionBtnOnClick(View view) {
        if (-1 == ownerID)
            return;
        Reminder reminder = new Reminder(edtTitle.getText().toString());
        reminder.setOwnerId(ownerID);
        reminder.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        DataManager.getInstance().addReminder(reminder);
    }
}
