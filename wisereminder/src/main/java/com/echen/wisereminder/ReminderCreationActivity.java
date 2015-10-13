package com.echen.wisereminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Category;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.Utility.CategoryUtility;
import com.echen.wisereminder.Utility.ReminderUtility;

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
//        updateDateTimeOnUI(m_dateTime);


    }

    @Override
    public void actionBtnOnClick(View view) {
        super.actionBtnOnClick(view);
        long lRel = DataManager.getInstance().addReminder(m_reminder);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ConsistentString.RESULT_BOOLEAN, (lRel >= 0) ? true : false);
        Intent intent = getIntent();
        intent.putExtra(ConsistentString.BUNDLE_UNIT, bundle);
        setResult(ConsistentParameter.RESULT_CODE_REMINDERCREATIONACTIVITY, intent); //set resultCode
        finish();
    }
}
