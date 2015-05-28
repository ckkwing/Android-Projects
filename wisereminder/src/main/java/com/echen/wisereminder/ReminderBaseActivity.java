package com.echen.wisereminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by echen on 2015/5/27.
 */
public abstract class ReminderBaseActivity extends Activity {
    protected EditText edtTitle = null;
    protected long ownerID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_action_view);
        Intent intent = getIntent();
        ownerID = intent.getLongExtra(ConsistentString.PARAM_CATEGORY_ID, -1);
        edtTitle = (EditText)findViewById(R.id.txtReminderTitle);
    }

    public abstract void actionBtnOnClick(View view);


}
