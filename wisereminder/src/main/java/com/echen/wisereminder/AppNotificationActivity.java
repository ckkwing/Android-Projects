package com.echen.wisereminder;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.echen.wisereminder.Adapter.ItemsToNotifyAdapter;

/**
 * Created by echen on 2015/10/22.
 */
public class AppNotificationActivity extends Activity {

    private ListView m_lstNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appnotification_view);
        initiateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSources();
    }

    private void initiateUI()
    {
        TextView lstHeaderView = new TextView(this);
        lstHeaderView.setText(getString(R.string.notification_to_do));
        m_lstNotification = (ListView)findViewById(R.id.lstNotification);
        m_lstNotification.addHeaderView(lstHeaderView);
        ItemsToNotifyAdapter adapter = new ItemsToNotifyAdapter(this);
        m_lstNotification.setAdapter(adapter);
    }

    private void updateSources()
    {
        ItemsToNotifyAdapter adapter = new ItemsToNotifyAdapter(this);
        m_lstNotification.setAdapter(adapter);
    }
}
