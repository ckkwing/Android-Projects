package com.echen.wisereminder.Data;

import android.content.Context;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Model.Category;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.Model.Subject;
import com.echen.wisereminder.R;
import com.echen.wisereminder.Utility.SettingUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by echen on 2015/4/27.
 */
public class DataManager {
    private Context context = null;
    private volatile static DataManager instance;

    private com.echen.wisereminder.Database.DAL.Category categoryDAL = null;
    private com.echen.wisereminder.Database.DAL.Reminder reminderDAL = null;

    private List<Category> categories = new ArrayList<Category>();
    private List<Reminder> reminders = new ArrayList<>();
    private List<Subject> subjects = new ArrayList<>();

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
        this.context = context;
        categoryDAL = new com.echen.wisereminder.Database.DAL.Category(context);
        if (null == categoryDAL)
            throw new NullPointerException("CategoryDAL is NULL");
        reminderDAL = new com.echen.wisereminder.Database.DAL.Reminder(context);
        if (null == reminderDAL)
            throw new NullPointerException("ReminderDAL is NULL");

        //categoryDAL.clearCategories();
        if (SettingUtility.getInstance().getIsFirstUse())
        {
            addDefaultCategories();
            SettingUtility.getInstance().setIsFirstUse(false);
        }
        addDefaultSubjects();
        List<Category> categories = getCategories(true);
        for (Iterator<Subject> iterator = subjects.iterator(); iterator.hasNext();) {
            Subject subject = iterator.next();
            if (subject.getType() == Subject.Type.Categories)
            {
                for(Category category : categories)
                {
                    subject.getChildren().add(category);
                }
            }
//            else if (subject.getType() == Subject.Type.Star)
//                for(int i =0; i< 2; i++)
//                {
//                    subject.getChildren().add(categories.get(i));
//                }
        }
        return true;
    }

    public void uninit()
    {

    }

    private final String getString(int resId)
    {
        return context.getString(resId);
    }

    private void addDefaultSubjects()
    {
        Subject allSubject = new Subject(Subject.Type.All, getString(R.string.subject_all));
        Subject starSubject = new Subject(Subject.Type.Star, getString(R.string.subject_star));
        Subject overdueSubject = new Subject(Subject.Type.Overdue, getString(R.string.subject_overdue));
        Subject todaySubject = new Subject(Subject.Type.Today, getString(R.string.subject_today));
        Subject next7DaysSubject = new Subject(Subject.Type.Next7Days, getString(R.string.subject_next7days));
        Subject categoriesSubject = new Subject(Subject.Type.Categories, getString(R.string.subject_categories));

        subjects.add(allSubject);
        subjects.add(starSubject);
        subjects.add(overdueSubject);
        subjects.add(todaySubject);
        subjects.add(next7DaysSubject);
        subjects.add(categoriesSubject);
    }

    public List<Subject> getSubjects() { return this.subjects; }

    public void addDefaultCategories()
    {
        long retId = -1;
        /*Category allCategory = new Category(getString(R.string.category_all));
        allCategory.setIsDefault(true);
        allCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        allCategory.setColor("#FEEFD0C1");
        long retId = categoryDAL.addCategory(allCategory);
        if (retId <= 0)
            return;*/

        //Test
        for(int i =0; i< 5; i++)
        {
            Reminder testReminder = new Reminder("Reminder " + String.valueOf(i));
            testReminder.setOwnerId(retId);
            reminderDAL.addReminder(testReminder);
        }
        //Test

        Category workCategory = new Category(getString(R.string.category_work));
        workCategory.setIsDefault(true);
        workCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        workCategory.setColor("#FEB1B4E9");
        retId = categoryDAL.addCategory(workCategory);
        if (retId <= 0)
            return;

        Category homeCategory = new Category(getString(R.string.category_home));
        homeCategory.setIsDefault(true);
        homeCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        homeCategory.setColor("#FE8AF1A3");
        retId = categoryDAL.addCategory(homeCategory);
        if (retId <= 0)
            return;

        Category otherCategory = new Category(getString(R.string.category_other));
        otherCategory.setIsDefault(true);
        otherCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        otherCategory.setColor("#FEBA55EB");
        retId = categoryDAL.addCategory(otherCategory);
        if (retId <= 0)
            return;
    }

    public List<Category> getCategories(boolean isForce)
    {
        if (isForce)
        {
            categories = categoryDAL.getCategories();
        }
        return categories;
    }

    public List<Reminder> getReminders(boolean isForce)
    {
        if (isForce)
        {
            reminders = reminderDAL.getReminders();
        }
        return reminders;
    }

    public long addReminder(Reminder reminder)
    {
        return reminderDAL.addReminder(reminder);
    }

    public List<Reminder> getRemindersByCategoryID(long categoryID)
    {
        return reminderDAL.getRemindersByCategoryID(categoryID);
    }

    public Reminder getReminderByID(long id)
    {
        Reminder reminderByID = null;
        boolean isForceGet = false;
        if (this.reminders.size() == 0)
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
}
