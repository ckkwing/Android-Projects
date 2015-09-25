package com.echen.wisereminder.CustomControl;

import android.view.View;
import android.widget.LinearLayout;

import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.R;

import uicommon.customcontrol.BasePopupWindow;

/**
 * Created by echen on 2015/9/25.
 */
public class PriorityPopupWindow extends BasePopupWindow {

    public static class PriorityPopupWindowExtraParam extends PopupWindowExtraParam
    {
        public Reminder.Priority ReminderPriority = Reminder.Priority.LEVEL4;
        public PriorityPopupWindowExtraParam()
        {

        }

        public PriorityPopupWindowExtraParam(int arrowUpResId, int arrowDownResId) {
            this.arrowUpResId = arrowUpResId;
            this.arrowDownResId = arrowDownResId;
        }
    }

    private LinearLayout m_level1Wrapper = null;
    private LinearLayout m_level2Wrapper = null;
    private LinearLayout m_level3Wrapper = null;
    private LinearLayout m_level4Wrapper = null;
    protected PriorityPopupWindow(View anchor, PopupWindowExtraParam extraParam) {
        super(anchor, extraParam);
        initializeExtra();
    }

    public PriorityPopupWindow(View anchor, View contentView, PopupWindowExtraParam extraParam) {
        super(anchor, contentView, extraParam);
        initializeExtra();
    }

    public PriorityPopupWindow(View anchor, int resIdOfContentView, PopupWindowExtraParam extraParam) {
        super(anchor, resIdOfContentView, extraParam);
        initializeExtra();
    }


    protected void initializeExtra() {
        m_level1Wrapper = (LinearLayout)m_contentView.findViewById(R.id.level1Wrapper);
        m_level1Wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_window.dismiss();
                if (null != m_iPopupWindowEvent)
                    m_iPopupWindowEvent.WindowClosed(Reminder.Priority.LEVEL1);
            }
        });
        m_level2Wrapper = (LinearLayout)m_contentView.findViewById(R.id.level2Wrapper);
        m_level2Wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_window.dismiss();
                if (null != m_iPopupWindowEvent)
                    m_iPopupWindowEvent.WindowClosed(Reminder.Priority.LEVEL2);
            }
        });
        m_level3Wrapper = (LinearLayout)m_contentView.findViewById(R.id.level3Wrapper);
        m_level3Wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_window.dismiss();
                if (null != m_iPopupWindowEvent)
                    m_iPopupWindowEvent.WindowClosed(Reminder.Priority.LEVEL3);
            }
        });
        m_level4Wrapper = (LinearLayout)m_contentView.findViewById(R.id.level4Wrapper);
        m_level4Wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_window.dismiss();
                if (null != m_iPopupWindowEvent)
                    m_iPopupWindowEvent.WindowClosed(Reminder.Priority.LEVEL4);
            }
        });
        switch (((PriorityPopupWindowExtraParam)m_extraParam).ReminderPriority)
        {
            case LEVEL1:
                m_level1Wrapper.setBackgroundResource(R.drawable.border_default);
                break;
            case LEVEL2:
                m_level2Wrapper.setBackgroundResource(R.drawable.border_default);
                break;
            case LEVEL3:
                m_level3Wrapper.setBackgroundResource(R.drawable.border_default);
                break;
            case LEVEL4:
                m_level4Wrapper.setBackgroundResource(R.drawable.border_default);
                break;
        }
    }

}
