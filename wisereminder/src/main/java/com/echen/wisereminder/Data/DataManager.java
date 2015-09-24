package com.echen.wisereminder.Data;

import android.content.Context;
import android.graphics.Color;

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

    public static DataManager getInstance()
    {
        if (null == instance)
        {
            synchronized (DataManager.class)
            {
                if (null == instance)
                {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    public boolean initiate(Context context)
    {
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
        if (SettingUtility.getInstance().getIsFirstUse())
        {
            addDefaultCategories();
            SettingUtility.getInstance().setIsFirstUse(false);
        }
        addDefaultSubjects();
        return true;
    }

    public void uninit()
    {

    }

    private final String getString(int resId)
    {
        return m_context.getString(resId);
    }

    private void addDefaultSubjects()
    {
        Subject allSubject = new Subject(Subject.Type.All, getString(R.string.subject_all));
        Subject starSubject = new Subject(Subject.Type.Star, getString(R.string.subject_star));
        Subject overdueSubject = new Subject(Subject.Type.Overdue, getString(R.string.subject_overdue));
        Subject todaySubject = new Subject(Subject.Type.Today, getString(R.string.subject_today));
        Subject next7DaysSubject = new Subject(Subject.Type.Next7Days, getString(R.string.subject_next7days));
        Subject categoriesSubject = new Subject(Subject.Type.Categories, getString(R.string.subject_categories));

        //Set children
        m_reminders = getReminders(true);
        m_categories = getCategories(true);

        for (Category category : m_categories) {
            if (null == category)
                continue;
            categoriesSubject.getChildren().add(category);
        }

//        for (Iterator<Reminder> iterator = m_reminders.iterator(); iterator.hasNext();) {
//            Reminder reminder = iterator.next();
//            if (null == reminder)
//                continue;
////            allSubject.getChildren().add(reminder);
////            if (reminder.getIsStar())
////                starSubject.getChildren().add(reminder);
////            for (Category category : m_categories)
////            {
////                if (reminder.getOwnerId() == category.getId())
////                    category.getChildren().add(reminder);
////            }
//        }

        m_subjects.add(allSubject);
        m_subjects.add(starSubject);
        m_subjects.add(overdueSubject);
        m_subjects.add(todaySubject);
        m_subjects.add(next7DaysSubject);
        m_subjects.add(categoriesSubject);
    }

    public List<Reminder> getRemindersBySubject(Subject.Type subjectType)
    {
        List<Reminder> reminders = new ArrayList<>();
        switch (subjectType)
        {
            case All:
                reminders = m_reminderDAL.getReminders();
                break;
            case Star:
                reminders = m_reminderDAL.getStarReminders();
                break;
            case Overdue:
                break;
            case Today:
                break;
            case Next7Days:
                break;
            case Categories:
                break;
        }
        return reminders;
    }

    public List<Subject> getM_subjects() { return this.m_subjects; }

    public void addDefaultCategories()
    {
        long retId = -1;

        //Test
        for(int i =0; i< 5; i++)
        {
            Reminder testReminder = new Reminder("Reminder " + String.valueOf(i));
            testReminder.setOwnerId(retId);
            m_reminderDAL.addReminder(testReminder);
        }
        //Test

        Category inboxCategory = new Category(getString(R.string.category_inbox));
        inboxCategory.setIsDefault(true);
        inboxCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        String strColor = String.format("#%06X", 0xFFFFFF & m_context.getResources().getColor(R.color.common_gray));
        inboxCategory.setColor(strColor);
        retId = m_categoryDAL.addCategory(inboxCategory);
        if (retId <= 0)
            return;

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

    public List<Category> getCategories(boolean isForce)
    {
        if (isForce)
        {
            m_categories = m_categoryDAL.getCategories();
        }
        return m_categories;
    }

    public List<Reminder> getReminders(boolean isForce)
    {
        if (isForce)
        {
            m_reminders = m_reminderDAL.getReminders();
        }
        return m_reminders;
    }

    public long addReminder(Reminder reminder)
    {
        return m_reminderDAL.addReminder(reminder);
    }

    public List<Reminder> getRemindersByCategoryID(long categoryID)
    {
        return m_reminderDAL.getRemindersByCategoryID(categoryID);
    }

    public Reminder getReminderByID(long id)
    {
        Reminder reminderByID = null;
        boolean isForceGet = false;
        if (this.m_reminders.size() == 0)
            isForceGet = true;
        List<Reminder> reminders = getReminders(isForceGet);
        for(Reminder reminder : reminders)
        {
            if (id == reminder.getId())
            {
                reminderByID = reminder;
                break;
            }
        }
        return reminderByID;
    }

    public boolean updateReminderByID(Reminder reminder)
    {
        long lRel = m_reminderDAL.updateReminderByID(reminder);
        if (1 == lRel)
            return true;
        return false;
    }
}
