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
public class ReminderCreationActivity extends Activity {
    private EditText edtTitle = null;
    private long ownerID = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_creation_view);
        Intent intent = getIntent();
        ownerID = intent.getLongExtra(ConsistentString.PARAM_CATEGORY_ID, -1);
        edtTitle = (EditText)findViewById(R.id.txtReminderTitle);
    }

    public void saveBtnOnClick(View view)
    {
        if (-1 == ownerID)
            return;
        Reminder reminder = new Reminder(edtTitle.getText().toString());
        reminder.setOwnerId(ownerID);
        reminder.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        DataManager.getInstance().addReminder(reminder);
    }
}
