package com.echen.wisereminder.CustomControl;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.R;

/**
 * Created by echen on 2015/10/15.
 */
public class DateTimeTypeSelectDialog extends DialogFragment {
    private static final String TAG = "DateTimeTypeSelectDialog";
    private OnDateOrTimeSelectedListener m_callback = null;
    private TextView m_selectDate = null;
    private TextView m_selectTime = null;

    public enum Type
    {
        Date,
        Time
    }
    public interface OnDateOrTimeSelectedListener{
        void OnDateOrTimeSelected(Type type);
    }

    public DateTimeTypeSelectDialog() {
        // Empty constructor required for dialog fragment.
    }

    public static DateTimeTypeSelectDialog newInstance(OnDateOrTimeSelectedListener callBack) {
        DateTimeTypeSelectDialog ret = new DateTimeTypeSelectDialog();
        ret.initialize(callBack);
        return ret;
    }

    public void initialize(OnDateOrTimeSelectedListener callBack) {
        m_callback = callBack;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = getActivity();
//        activity.getWindow().setSoftInputMode(
//        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //如果setCancelable()中参数为true，若点击dialog覆盖不到的activity的空白或者按返回键，则进行cancel，状态检测依次onCancel()和onDismiss()。如参数为false，则按空白处或返回键无反应。缺省为true
        setCancelable(true);
//        //可以设置dialog的显示风格，如style为STYLE_NO_TITLE，将被显示title。遗憾的是，我没有在DialogFragment中找到设置title内容的方法。theme为0，表示由系统选择合适的theme。
//        int style = DialogFragment.STYLE_NO_NORMAL, theme = 0;
//        setStyle(style,theme);
        if (savedInstanceState != null) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Remove title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //1. Create view
        View view = inflater.inflate(R.layout.dialog_date_time_selector, container,false);
        initializeExtra(view);
        return view;
    }

    protected void initializeExtra(View view)
    {
        m_selectDate = (TextView)view.findViewById(R.id.txtSelectDate);
        m_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != m_callback)
                    m_callback.OnDateOrTimeSelected(Type.Date);
                dismiss();
            }
        });

        m_selectTime = (TextView)view.findViewById(R.id.txtSelectTime);
        m_selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != m_callback)
                    m_callback.OnDateOrTimeSelected(Type.Time);
                dismiss();
            }
        });
    }
}
