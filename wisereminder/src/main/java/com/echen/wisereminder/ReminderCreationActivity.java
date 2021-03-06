package com.echen.wisereminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Category;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.Utility.CategoryUtility;

/**
 * Created by echen on 2015/5/19.
 */
public class ReminderCreationActivity extends ReminderBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m_reminder = new Reminder();
        m_reminder.setPriority(Reminder.Priority.LEVEL4);
        Category inboxCategory = CategoryUtility.getInboxCategory();
        if (null != inboxCategory)
            m_reminder.setOwnerId(inboxCategory.getId());
        super.onCreate(savedInstanceState);
//        setTitle(R.string.reminder_creation);

//        m_iconPriority.setTextColor(ReminderUtility.getPriorityColorInt(m_reminder.getPriority(), this));
//        updateDateTimeOnUI(m_dueDateTime);


    }

    @Override
    public void onAction() {
        super.onAction();
        m_reminder.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        long lRel = DataManager.getInstance().addReminder(m_reminder);
        if (lRel > 0)
        {
            //Set Alarm
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(ConsistentString.RESULT_BOOLEAN, (lRel >= 0) ? true : false);
        Intent intent = getIntent();
        intent.putExtra(ConsistentString.BUNDLE_UNIT, bundle);
        setResult(ConsistentParameter.RESULT_CODE_REMINDERCREATIONACTIVITY, intent); //set resultCode
        finish();
    }
}
