package com.echen.wisereminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by echen on 2015/5/27.
 */
public abstract class ReminderBaseActivity extends Activity {
    protected EditText edtTitle = null;
    protected Button btnAction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_action_view);
        edtTitle = (EditText)findViewById(R.id.txtReminderTitle);
        btnAction = (Button)findViewById(R.id.btn_action);
    }

    public abstract void actionBtnOnClick(View view);
}
