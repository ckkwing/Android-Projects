package com.echen.arthur;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by echen on 2015/4/13.
 */
public class ArthurMainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arthur_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
