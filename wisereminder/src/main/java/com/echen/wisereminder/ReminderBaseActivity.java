package com.echen.wisereminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.echen.androidcommon.DateTime;
import com.echen.androidcommon.Interface.IDateTimeEvent;
import com.echen.wisereminder.Adapter.IPropertySelectedEvent;
import com.echen.wisereminder.Adapter.ReminderPropertiesAdapter;
import com.echen.wisereminder.CustomControl.CategoryListDialog;
import com.echen.wisereminder.CustomControl.DateTimeTypeSelectDialog;
import com.echen.wisereminder.CustomControl.PriorityDialog;
import com.echen.wisereminder.CustomControl.PriorityPopupWindow;
import com.echen.wisereminder.Model.Category;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.Utility.ReminderUtility;
import com.shamanland.fab.FloatingActionButton;
import com.shamanland.fab.ShowHideOnScroll;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import be.webelite.ion.IconView;
import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.time.RadialPickerLayout;
import mirko.android.datetimepicker.time.TimePickerDialog;
import uicommon.customcontrol.Interface.IPopupWindowEvent;

/**
 * Created by echen on 2015/5/27.
 */
public abstract class ReminderBaseActivity extends Activity implements IPropertySelectedEvent {

    public String DATETIME_FORMAT_DATE = "yyyy-MM-dd";
    public String DATETIME_FORMAT_TIME = "HH:mm";

    //UI elements
    protected EditText m_edtReminderName = null;
    protected DateTime m_dueDateTime = null;
    protected DateTime m_alertDateTime = null;
//    protected Spinner m_spinnerCategory = null;


    protected Reminder m_reminder = null;
    protected List<Category> m_categories = new ArrayList<>();
    protected ReminderPropertiesAdapter m_propertiesAdapter = null;
    protected ReminderPropertiesAdapter.PropertyType m_currentProperty = ReminderPropertiesAdapter.PropertyType.None;

    protected DatePickerDialog m_datePickerDialog = null;
    protected TimePickerDialog m_timePickerDialog24h = null;
    protected PriorityDialog m_priorityDialog = null;
    protected DateTimeTypeSelectDialog m_dateOrTimePickerDialog = null;

    //new UI
    protected ReminderPropertiesAdapter propertiesAdapter;
    protected ListView m_propertyListView;
    protected TextView m_txtSelectedCategory;
    protected FloatingActionButton m_fab;
    protected IconView m_iconPriority = null;
    protected IconView m_iconBack = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.reminder_action_view);
        m_iconBack = (IconView) findViewById(R.id.iconBack);
        m_iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(ConsistentString.RESULT_BOOLEAN, false);
                Intent intent = getIntent();
                intent.putExtra(ConsistentString.BUNDLE_UNIT, bundle);
                setResult(ConsistentParameter.RESULT_CODE_REMINDEREDITIONACTIVITY, intent); //set resultCode
                finish();
            }
        });
        m_edtReminderName = (EditText) findViewById(R.id.edtTaskName);
//        m_edtDueDate = (EditText) findViewById(R.id.edtDueDate);
//        m_edtDueTime = (EditText) findViewById(R.id.edtDueTime);
//        m_btnAction = (Button) findViewById(R.id.btnTest);
//        m_iconPriority = (IconView)findViewById(R.id.iconPriority);
//        m_iconPriority.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPriorityPopupWindow(m_iconPriority);
//            }
//        });
        m_dueDateTime = new DateTime(m_reminder.getDueTime().getLocalCalendar());
        m_alertDateTime = new DateTime(m_reminder.getAlertTime().getLocalCalendar());

//        createCategoryDropList();
//        createDateTimePickerDialog();
        createPriorityDialog();
        createDateOrTimePickerDialog();

        m_propertyListView = (ListView) findViewById(R.id.propertyList);
        m_txtSelectedCategory = (TextView) findViewById(R.id.txtSelectedCategory);
        m_txtSelectedCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryListDialog categoryListDialog = CategoryListDialog.newInstance(new CategoryListDialog.CategorySelectedListener() {
                    @Override
                    public void onCategorySelected(Category category) {
                        updateCategory(category);
                    }
                });
                categoryListDialog.show(getFragmentManager(), "");
            }
        });
        m_fab = (FloatingActionButton) findViewById(R.id.fab);
        m_fab.setSize(FloatingActionButton.SIZE_MINI);
        m_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "action clicked", Toast.LENGTH_SHORT).show();
                if (TextUtils.isEmpty(m_edtReminderName.getText()))
                {
                    Toast.makeText(v.getContext(), getString(R.string.error_task_name_empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                onAction();
            }
        });
        updateCategory(ReminderUtility.getOwner(m_reminder));
        createProperties();
    }

//    protected void createCategoryDropList() {
//        m_categories = DataManager.getInstance().getCategories(false);
//        CategoryListAdapter adapter = new CategoryListAdapter(this, m_categories);
//        m_spinnerCategory = (Spinner) findViewById(R.id.spnCategory);
//        m_spinnerCategory.setAdapter(adapter);
//        m_spinnerCategory.setOnItemSelectedListener(new SpinnerSelectedListener());
//    }

    protected void createProperties() {
        m_propertiesAdapter = new ReminderPropertiesAdapter(this, m_reminder, this);
        m_propertyListView.setAdapter(m_propertiesAdapter);
        m_propertyListView.setOnTouchListener(new ShowHideOnScroll(m_fab));
    }

    protected void updateCategory(Category category) {
        m_reminder.setOwnerId(category.getId());
        m_txtSelectedCategory.setText(category.getName());
    }

    protected void createPriorityDialog() {
        if (null == m_priorityDialog) {
            m_priorityDialog = PriorityDialog.newInstance(new PriorityDialog.OnPriorityChangedListener() {
                @Override
                public void OnPriorityChanged(PriorityDialog dialog, Reminder.Priority priority) {
                    m_currentProperty = ReminderPropertiesAdapter.PropertyType.None;
                    m_reminder.setPriority(priority);
                    m_propertiesAdapter.updateProperty();
                }
            }, m_reminder.getPriority());
        }
    }

    protected void createDateOrTimePickerDialog() {
        if (null == m_dateOrTimePickerDialog) {
            m_dateOrTimePickerDialog = DateTimeTypeSelectDialog.newInstance(new DateTimeTypeSelectDialog.OnDateOrTimeSelectedListener() {
                @Override
                public void OnDateOrTimeSelected(DateTimeTypeSelectDialog.Type type) {
                    switch (type) {
                        case Date:
                            showDatePickerDialog(ReminderPropertiesAdapter.PropertyType.AlertTime);
                            break;
                        case Time:
                            showTimePickerDialog(ReminderPropertiesAdapter.PropertyType.AlertTime);
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onPropertySelected(ReminderPropertiesAdapter.PropertyType type) {
//        Toast.makeText(this, type.toString(), Toast.LENGTH_SHORT).show();
        switch (type) {
            case DueTime:
                showDatePickerDialog(ReminderPropertiesAdapter.PropertyType.DueTime);
                break;
            case Priority: {
                if (null != m_priorityDialog) {
                    m_priorityDialog.updatePriority(m_reminder.getPriority());
                    m_priorityDialog.show(getFragmentManager(), "");
                }
            }
            break;
            case AlertTime: {
                if (null != m_dateOrTimePickerDialog)
                    m_dateOrTimePickerDialog.show(getFragmentManager(), "");
            }
            break;
        }
        m_currentProperty = type;
    }

    private class SpinnerSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            m_selectedCategory = m_categories.get(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
//            m_selectedCategory = null;
        }
    }

    public void onAction() {
        //showPriorityPopupWindow(m_btnAction);
//        String ALARM_ACTION = ConsistentString.ACTION_BROADCAST_MAINSERVICE;
//        Intent intent = new Intent(ALARM_ACTION);
//        sendBroadcast(intent);

//        showPriorityPopupWindow(view);

        m_reminder.setName(m_edtReminderName.getText().toString());
        m_reminder.setDueTime_UTC(m_dueDateTime.toUTCLong());
        m_reminder.setAlertTime_UTC(m_alertDateTime.toUTCLong());
    }

    protected void showPriorityPopupWindow(View parent) {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = mLayoutInflater.inflate(R.layout.priority_view, null);// R.layout.popΪ PopupWindow �Ĳ����ļ�
        PriorityPopupWindow.PriorityPopupWindowExtraParam param = new PriorityPopupWindow.PriorityPopupWindowExtraParam(R.id.arrow_up, R.id.arrow_down);
        param.OffsetPoint.x = 30;
        param.PopupArrowMargin = getResources().getDimensionPixelSize(R.dimen.popup_arrow_margin);
        param.ReminderPriority = m_reminder.getPriority();
        PriorityPopupWindow pop = new PriorityPopupWindow(parent, contentView, param);
        pop.setPopupWindowEvent(new IPopupWindowEvent() {
            @Override
            public void WindowClosed(Object obj) {
                if (null == obj)
                    return;
                if (obj instanceof Reminder.Priority) {
                    Reminder.Priority priority = (Reminder.Priority) obj;
                    if (null != m_reminder) {
                        m_reminder.setPriority(priority);
                        m_iconPriority.setTextColor(ReminderUtility.getPriorityColorInt(m_reminder.getPriority(), ReminderBaseActivity.this));
                    }
                }
            }
        });
        pop.show();
    }

    protected void showDatePickerDialog(final ReminderPropertiesAdapter.PropertyType type) {
        final DateTime dateTime = (type == ReminderPropertiesAdapter.PropertyType.DueTime) ? m_dueDateTime : m_alertDateTime;
        DateTime defaultTime = dateTime;
        if (null == defaultTime || defaultTime.equals(DateTime.minValue()))
            defaultTime = DateTime.now();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                dateTime.update(year, month, day);
                switch (type) {
                    case DueTime:
                        m_reminder.setDueTime_UTC(dateTime.toUTCLong());
                        break;
                    case AlertTime:
                        m_reminder.setAlertTime_UTC(dateTime.toUTCLong());
                        break;
                }

                m_propertiesAdapter.updateProperty();
            }

        }, defaultTime.getLocalCalendar().get(Calendar.YEAR), defaultTime.getLocalCalendar().get(Calendar.MONTH), defaultTime.getLocalCalendar().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show(getFragmentManager(), "");
    }

    protected void showTimePickerDialog(final ReminderPropertiesAdapter.PropertyType type) {
        final DateTime dateTime = (type == ReminderPropertiesAdapter.PropertyType.DueTime) ? m_dueDateTime : m_alertDateTime;
        DateTime defaultTime = dateTime;
        if (null == defaultTime || defaultTime.equals(DateTime.minValue()))
            defaultTime = DateTime.now();
        TimePickerDialog timePickerDialog24h = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay,
                                  int minute) {
                dateTime.update(dateTime.getLocalCalendar().get(Calendar.YEAR),
                        dateTime.getLocalCalendar().get(Calendar.MONTH),
                        dateTime.getLocalCalendar().get(Calendar.DAY_OF_MONTH),
                        hourOfDay, minute);
                switch (type) {
                    case DueTime:
                        m_reminder.setDueTime_UTC(dateTime.toUTCLong());
                        break;
                    case AlertTime:
                        m_reminder.setAlertTime_UTC(dateTime.toUTCLong());
                        break;
                }
                m_propertiesAdapter.updateProperty();
            }
        }, defaultTime.getLocalCalendar().get(Calendar.HOUR_OF_DAY), defaultTime.getLocalCalendar().get(Calendar.MINUTE), true);
        timePickerDialog24h.show(getFragmentManager(), "");
    }
}
