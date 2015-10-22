package com.echen.wisereminder.CustomControl;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.R;

/**
 * Created by echen on 2015/10/14.
 */
public class PriorityDialog extends DialogFragment {

    private static final String TAG = "PriorityDialog";
    private static final String KEY_SELECTED_PRIORITY = "selected_priority";

    private Reminder.Priority m_reminderPriority = Reminder.Priority.LEVEL4;
    private LinearLayout m_level1Wrapper = null;
    private LinearLayout m_level2Wrapper = null;
    private LinearLayout m_level3Wrapper = null;
    private LinearLayout m_level4Wrapper = null;
    private OnPriorityChangedListener m_CallBack;

    public interface OnPriorityChangedListener {

        void OnPriorityChanged(PriorityDialog dialog, Reminder.Priority priority);
    }

    public PriorityDialog() {
        // Empty constructor required for dialog fragment.
    }

    public static PriorityDialog newInstance(OnPriorityChangedListener callBack, Reminder.Priority priority) {
        PriorityDialog ret = new PriorityDialog();
        ret.initialize(callBack, priority);
        return ret;
    }

    public void initialize(OnPriorityChangedListener callBack, Reminder.Priority priority) {
        m_CallBack = callBack;
        m_reminderPriority = priority;
    }

    public void updatePriority(Reminder.Priority priority)
    {
        m_reminderPriority = priority;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = getActivity();
//        activity.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //如果setCancelable()中参数为true，若点击dialog覆盖不到的activity的空白或者按返回键，则进行cancel，状态检测依次onCancel()和onDismiss()。如参数为false，则按空白处或返回键无反应。缺省为true
        setCancelable(true);
//        //可以设置dialog的显示风格，如style为STYLE_NO_TITLE，将被显示title。遗憾的是，我没有在DialogFragment中找到设置title内容的方法。theme为0，表示由系统选择合适的theme。
//        int style = DialogFragment.STYLE_NO_NORMAL, theme = 0;
//        setStyle(style,theme);
        if (savedInstanceState != null) {
            int iPriority = savedInstanceState.getInt(KEY_SELECTED_PRIORITY);
            m_reminderPriority = Reminder.Priority.values()[iPriority];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Remove title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //1. Create view
        View view = inflater.inflate(R.layout.dialog_priority, container,false);
        initializeExtra(view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_PRIORITY, m_reminderPriority.ordinal());
    }

    protected void initializeExtra(View view) {
        m_level1Wrapper = (LinearLayout)view.findViewById(R.id.level1Wrapper);
        m_level1Wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != m_CallBack)
                    m_CallBack.OnPriorityChanged(PriorityDialog.this, Reminder.Priority.LEVEL1);
            }
        });
        m_level2Wrapper = (LinearLayout)view.findViewById(R.id.level2Wrapper);
        m_level2Wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != m_CallBack)
                    m_CallBack.OnPriorityChanged(PriorityDialog.this, Reminder.Priority.LEVEL2);
            }
        });
        m_level3Wrapper = (LinearLayout)view.findViewById(R.id.level3Wrapper);
        m_level3Wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != m_CallBack)
                    m_CallBack.OnPriorityChanged(PriorityDialog.this, Reminder.Priority.LEVEL3);
            }
        });
        m_level4Wrapper = (LinearLayout)view.findViewById(R.id.level4Wrapper);
        m_level4Wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != m_CallBack)
                    m_CallBack.OnPriorityChanged(PriorityDialog.this, Reminder.Priority.LEVEL4);
            }
        });
        switch (m_reminderPriority)
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
