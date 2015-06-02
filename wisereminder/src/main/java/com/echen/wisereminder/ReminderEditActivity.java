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

    private Reminder reminderToEdit = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        long reminderID = intent.getLongExtra(ConsistentString.PARAM_REMINDER_ID, -1);
        if (-1 != reminderID)
        {
            reminderToEdit = DataManager.getInstance().getReminderByID(reminderID);
        }
        edtTitle.setText(reminderToEdit.getName());
        btnAction.setText(R.string.common_edit);
    }

    @Override
    public void actionBtnOnClick(View view) {

    }
}
