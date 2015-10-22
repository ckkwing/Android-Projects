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
        //���setCancelable()�в���Ϊtrue�������dialog���ǲ�����activity�Ŀհ׻��߰����ؼ��������cancel��״̬�������onCancel()��onDismiss()�������Ϊfalse���򰴿հ״��򷵻ؼ��޷�Ӧ��ȱʡΪtrue
        setCancelable(true);
//        //��������dialog����ʾ�����styleΪSTYLE_NO_TITLE��������ʾtitle���ź����ǣ���û����DialogFragment���ҵ�����title���ݵķ�����themeΪ0����ʾ��ϵͳѡ����ʵ�theme��
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
