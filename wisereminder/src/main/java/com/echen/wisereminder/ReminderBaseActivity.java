package com.echen.wisereminder;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import com.echen.androidcommon.DateTime;
import com.echen.androidcommon.Interface.IDateTimeEvent;
import com.echen.wisereminder.Adapter.CategoryListAdapter;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Category;
import com.echen.wisereminder.Model.Reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import be.webelite.ion.IconView;
import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.time.RadialPickerLayout;
import mirko.android.datetimepicker.time.TimePickerDialog;
import uicommon.customcontrol.CustomPopupWindow;

/**
 * Created by echen on 2015/5/27.
 */
public abstract class ReminderBaseActivity extends Activity {

    public String DATETIME_FORMAT_DATE = "yyyy-MM-dd";
    public String DATETIME_FORMAT_TIME = "HH:mm";

    //UI elements
    protected EditText m_edtInfo = null;
    protected EditText m_edtDueDate = null;
    protected EditText m_edtDueTime = null;
    protected Button m_btnAction = null;
    protected DateTime m_dateTime = null;
    protected Spinner m_spinnerCategory = null;
    protected IconView m_iconPriority = null;

    protected Reminder m_reminderToEdit = null;
    protected Category m_selectedCategory = null;
    protected List<Category> m_categories = new ArrayList<>();

    protected DatePickerDialog m_datePickerDialog = null;
    protected TimePickerDialog m_timePickerDialog24h = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_action_view);
        m_edtInfo = (EditText) findViewById(R.id.edtReminderInfo);
        m_edtDueDate = (EditText) findViewById(R.id.edtDueDate);
        m_edtDueTime = (EditText) findViewById(R.id.edtDueTime);
        m_btnAction = (Button) findViewById(R.id.btnTest);
        m_iconPriority = (IconView)findViewById(R.id.iconPriority);
        m_iconPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriorityPopupWindow(m_iconPriority);
            }
        });

        m_dateTime = new DateTime();
        m_dateTime.setDateTimeEvent(new IDateTimeEvent() {
            @Override
            public void DateTimeChanged(DateTime dateTime) {
                updateDateTimeOnUI(dateTime);
            }
        });
        createCategoryDropList();
        createDateTimePickerDialog();
    }

    protected void createCategoryDropList() {
        m_categories = DataManager.getInstance().getCategories(false);
        CategoryListAdapter adapter = new CategoryListAdapter(this, m_categories);
        m_spinnerCategory = (Spinner) findViewById(R.id.spnCategory);
        m_spinnerCategory.setAdapter(adapter);
        m_spinnerCategory.setOnItemSelectedListener(new SpinnerSelectedListener());
    }

    protected void createDateTimePickerDialog()
    {
        if (null == m_datePickerDialog)
        {
            m_datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                    m_dateTime.update(year, month, day);
                }

            }, m_dateTime.getLocalCalendar().get(Calendar.YEAR), m_dateTime.getLocalCalendar().get(Calendar.MONTH), m_dateTime.getLocalCalendar().get(Calendar.DAY_OF_MONTH));
        }
        if (null == m_timePickerDialog24h)
        {
            m_timePickerDialog24h = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(RadialPickerLayout view, int hourOfDay,
                                      int minute) {
                    m_dateTime.update(m_dateTime.getLocalCalendar().get(Calendar.YEAR),
                            m_dateTime.getLocalCalendar().get(Calendar.MONTH),
                            m_dateTime.getLocalCalendar().get(Calendar.DAY_OF_MONTH),
                            hourOfDay, minute);
                }
            }, m_dateTime.getLocalCalendar().get(Calendar.HOUR_OF_DAY), m_dateTime.getLocalCalendar().get(Calendar.MINUTE), true);
        }
    }

    private class SpinnerSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            m_selectedCategory = m_categories.get(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            m_selectedCategory = null;
        }
    }

    public void edtDueDateOnClick(View view) {
        if (null != m_datePickerDialog && !m_datePickerDialog.isVisible())
            m_datePickerDialog.show(getFragmentManager(), "");
    }

    public void edtDueTimeOnClick(View view) {
        if (null != m_timePickerDialog24h && !m_timePickerDialog24h.isVisible())
            m_timePickerDialog24h.show(getFragmentManager(), "");
    }

    public void actionBtnOnClick(View view)
    {
        //showPriorityPopupWindow(m_btnAction);
//        String ALARM_ACTION = ConsistentString.ACTION_BROADCAST_MAINSERVICE;
//        Intent intent = new Intent(ALARM_ACTION);
//        sendBroadcast(intent);

//

        showPriorityPopupWindow(view);
    }

    protected void showPriorityPopupWindow(View parent)
    {
        LayoutInflater mLayoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = mLayoutInflater.inflate(R.layout.priority_view, null);// R.layout.popΪ PopupWindow �Ĳ����ļ�

//        PopupWindow pop = new PopupWindow(contentView, getResources().getDimensionPixelSize(R.dimen.popup_priority_width), ViewGroup.LayoutParams.WRAP_CONTENT,true);
//        pop.setFocusable(true);
//        // ���������PopupWindow�ı����������ǵ���ⲿ������Back�����޷�dismiss����
//        // �Ҿ���������API��һ��bug
//        pop.setBackgroundDrawable(new BitmapDrawable());
//        pop.setAnimationStyle(R.style.popup_anim_style);
//        pop.showAsDropDown(parent);

        CustomPopupWindow.PopupWindowExtraParam param = new CustomPopupWindow.PopupWindowExtraParam();
        param.OffsetPoint.x = 30;
        param.ArrowUpResId = R.id.arrow_up;
        param.ArrowDownResId = R.id.arrow_down;
        CustomPopupWindow pop = new CustomPopupWindow(parent, contentView, param);
        pop.show();
    }

    protected void updateDateTimeOnUI(DateTime dateTime)
    {
        m_edtDueDate.setText(dateTime.toString(DATETIME_FORMAT_DATE));
        m_edtDueTime.setText(dateTime.toString(DATETIME_FORMAT_TIME));
    }
}
