package com.echen.wisereminder.Data;

import android.content.Context;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Model.Category;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.Model.Subject;
import com.echen.wisereminder.R;
import com.echen.wisereminder.Utility.SettingUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/4/27.
 */
public class DataManager {
    private Context m_context = null;
    private volatile static DataManager instance;

    private com.echen.wisereminder.Database.DAL.Category m_categoryDAL = null;
    private com.echen.wisereminder.Database.DAL.Reminder m_reminderDAL = null;

    private List<Category> m_categories = new ArrayList<Category>();
    private List<Reminder> m_reminders = new ArrayList<>();
    private List<Subject> m_subjects = new ArrayList<>();

    public static DataManager getInstance() {
        if (null == instance) {
            synchronized (DataManager.class) {
                if (null == instance) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    public boolean initiate(Context context) {
        if (null == context)
            return false;
        this.m_context = context;
        m_categoryDAL = new com.echen.wisereminder.Database.DAL.Category(context);
        if (null == m_categoryDAL)
            throw new NullPointerException("CategoryDAL is NULL");
        m_reminderDAL = new com.echen.wisereminder.Database.DAL.Reminder(context);
        if (null == m_reminderDAL)
            throw new NullPointerException("ReminderDAL is NULL");

        //m_categoryDAL.clearCategories();
        if (SettingUtility.getInstance().getIsFirstUse()) {
            addDefaultCategories();
            SettingUtility.getInstance().setIsFirstUse(false);
        }
        addDefaultSubjects();
        return true;
    }

    public void uninit() {

    }

    private final String getString(int resId) {
        return m_context.getString(resId);
    }

    private void addDefaultSubjects() {
        Subject allSubject = new Subject(Subject.Type.All, getString(R.string.subject_all));
        Subject starSubject = new Subject(Subject.Type.Star, getString(R.string.subject_star));
        Subject overdueSubject = new Subject(Subject.Type.Overdue, getString(R.string.subject_overdue));
        Subject todaySubject = new Subject(Subject.Type.Today, getString(R.string.subject_today));
        Subject next7DaysSubject = new Subject(Subject.Type.Next7Days, getString(R.string.subject_next7days));
        Subject categoriesSubject = new Subject(Subject.Type.Categories, getString(R.string.subject_categories));

        //Set children
        reloadAll();

        for (Category category : m_categories) {
            if (null == category)
                continue;
            categoriesSubject.getChildren().add(category);
        }

        m_subjects.add(allSubject);
        m_subjects.add(starSubject);
        m_subjects.add(overdueSubject);
        m_subjects.add(todaySubject);
        m_subjects.add(next7DaysSubject);
        m_subjects.add(categoriesSubject);
    }

    public void reloadAll() {
        m_reminders = getReminders(true);
        m_categories = getCategories(true);
    }

    public List<Reminder> getRemindersBySubject(Subject.Type subjectType, boolean isForce) {
        List<Reminder> reminders = new ArrayList<>();
        switch (subjectType) {
            case All: {
                if (isForce)
                    reminders = m_reminderDAL.getReminders();
                else {
                    reminders.addAll(m_reminders);
                }
            }
            break;
            case Star: {
                if (isForce)
                    reminders = m_reminderDAL.getStarReminders();
                else {
                    for (Reminder reminder : m_reminders) {
                        if (null == reminder)
                            continue;
                        if (reminder.getIsStar())
                            reminders.add(reminder);
                    }
                }
            }
            break;
            case Overdue: {
                if (isForce)
                    reminders = m_reminderDAL.getOverdueReminders();
                else {
                    DateTime today = DateTime.today();
                    for (Reminder reminder : m_reminders) {
                        if (null == reminder)
                            continue;
                        DateTime dateTime = reminder.getDueTime();
//                        DateTime dateNow = DateTime.now();
//                        dateNow.update(dateNow.getYear(), dateNow.getMonth(), dateNow.getDay(), dateNow.getHour(), dateNow.getMinute(), dateNow.getSecond());
//                        if (dateTime.toUTCLong() > 0 && dateTime.toUTCLong() < dateNow.toUTCLong())
//                        {
//                            reminders.add(reminder);
//                        }

                        if (dateTime.toUTCLong() > 0 && dateTime.toUTCLong() < today.toUTCLong()) {
                            reminders.add(reminder);
                        }
                    }
                }
            }
            break;
            case Today: {
                if (isForce)
                    reminders = m_reminderDAL.getTodayReminders();
                else {
                    for (Reminder reminder : m_reminders) {
                        if (null == reminder)
                            continue;
                        DateTime dateTime = reminder.getDueTime();
                        DateTime dateNow = DateTime.now();
                        if (dateTime.getDay() == dateNow.getDay() &&
                                dateTime.getMonth() == dateNow.getMonth() &&
                                dateTime.getYear() == dateNow.getYear()) {
                            reminders.add(reminder);
                        }
                    }
                }
            }
            break;
            case Next7Days:
                if (isForce)
                    reminders = m_reminderDAL.getNext7DaysReminders();
                else {
                    DateTime today = DateTime.today();
                    DateTime tomorrow = today.addDays(1);
                    DateTime next7Days = today.addDays(7);
                    for (Reminder reminder : m_reminders) {
                        if (null == reminder)
                            continue;
                        DateTime dateTime = reminder.getDueTime();
                        if (dateTime.toUTCLong() >= tomorrow.toUTCLong() && dateTime.toUTCLong() < next7Days.toUTCLong()) {
                            reminders.add(reminder);
                        }
                    }
                }
                break;
            case Categories: {
                //Expand folder in getView
            }
            break;
        }
        return reminders;
    }

    public List<Subject> getSubjects() {
        return this.m_subjects;
    }

    public void addDefaultCategories() {
        long retId = -1;

        Category inboxCategory = new Category(getString(R.string.category_inbox));
        inboxCategory.setIsDefault(true);
        inboxCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        String strColor = String.format("#%06X", 0xFFFFFF & m_context.getResources().getColor(R.color.common_gray));
        inboxCategory.setColor(strColor);
        retId = m_categoryDAL.addCategory(inboxCategory);
        if (retId <= 0)
            return;

        //Test
        for (int i = 0; i < 5; i++) {
            Reminder testReminder = new Reminder("Reminder " + String.valueOf(i));
            testReminder.setOwnerId(retId);
            m_reminderDAL.addReminder(testReminder);
        }
        //Test

        Category workCategory = new Category(getString(R.string.category_work));
        workCategory.setIsDefault(true);
        workCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        workCategory.setColor("#FEB1B4E9");
        retId = m_categoryDAL.addCategory(workCategory);
        if (retId <= 0)
            return;
        Reminder testReminder1 = new Reminder("Reminder Work1");
        testReminder1.setOwnerId(retId);
        Reminder testReminder2 = new Reminder("Reminder Work2");
        testReminder2.setOwnerId(retId);
        m_reminderDAL.addReminder(testReminder1);
        m_reminderDAL.addReminder(testReminder2);

        Category homeCategory = new Category(getString(R.string.category_home));
        homeCategory.setIsDefault(true);
        homeCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        homeCategory.setColor("#FE8AF1A3");
        retId = m_categoryDAL.addCategory(homeCategory);
        if (retId <= 0)
            return;
        Reminder testHomeReminder1 = new Reminder("Reminder Home1");
        testHomeReminder1.setOwnerId(retId);
        m_reminderDAL.addReminder(testHomeReminder1);

        Category otherCategory = new Category(getString(R.string.category_other));
        otherCategory.setIsDefault(true);
        otherCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        otherCategory.setColor("#FEBA55EB");
        retId = m_categoryDAL.addCategory(otherCategory);
        if (retId <= 0)
            return;
    }

    public List<Category> getCategories(boolean isForce) {
        if (isForce) {
            m_categories = m_categoryDAL.getCategories();
        }
        return m_categories;
    }

    public List<Reminder> getReminders(boolean isForce) {
        if (isForce) {
            m_reminders = m_reminderDAL.getReminders();
        }
        return m_reminders;
    }

    public long addReminder(Reminder reminder) {
        long lRel = m_reminderDAL.addReminder(reminder);
        if (lRel >= 0) {
            reminder.setId(lRel);
            m_reminders.add(reminder);
        }
        return lRel;
    }

    public long deleteReminder(Reminder reminder)
    {
        long lRel = m_reminderDAL.deleteReminder(reminder);
        if (lRel >= 0) {
            m_reminders.remove(reminder);
        }
        return lRel;
    }

    public List<Reminder> getRemindersByCategoryID(long categoryID, boolean isForce) {
        List<Reminder> reminders = new ArrayList<>();
        if (isForce) {
            reminders = m_reminderDAL.getRemindersByCategoryID(categoryID);
        } else {
            for (Reminder reminder : m_reminders) {
                if (null == reminder)
                    continue;
                if (categoryID == reminder.getOwnerId())
                    reminders.add(reminder);
            }
        }
        return reminders;
    }

    public Reminder getReminderByID(long id, boolean isForce) {
        Reminder reminderByID = null;
        List<Reminder> reminders = getReminders(isForce);
        for (Reminder reminder : reminders) {
            if (id == reminder.getId()) {
                reminderByID = reminder;
                break;
            }
        }
        return reminderByID;
    }

    public boolean updateReminder(Reminder reminder) {
        long lRel = m_reminderDAL.updateReminderByID(reminder);
        if (1 == lRel) {
            Reminder updateReminder = null;
            for (Reminder existReminder : m_reminders)
            {
                if (reminder.getId() == existReminder.getId())
                {
                    updateReminder = existReminder;
                    break;
                }
            }
            if (null != updateReminder)
            {
                m_reminders.set(m_reminders.indexOf(updateReminder), reminder);
            }
            return true;
        }
        return false;
    }
}
