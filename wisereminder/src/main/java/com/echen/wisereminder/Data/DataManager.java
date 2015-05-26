package com.echen.wisereminder.Data;

import android.content.Context;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Model.Category;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.R;
import com.echen.wisereminder.Utility.SettingUtility;

import java.util.ArrayList;
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
        return true;
    }

    public void uninit()
    {

    }

    private final String getString(int resId)
    {
        return context.getString(resId);
    }

    public void addDefaultCategories()
    {
        Category allCategory = new Category(getString(R.string.category_all));
        allCategory.setIsDefault(true);
        allCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        long retId = categoryDAL.addCategory(allCategory);
        if (retId <= 0)
            return;

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
        retId = categoryDAL.addCategory(workCategory);
        if (retId <= 0)
            return;

        Category homeCategory = new Category(getString(R.string.category_home));
        homeCategory.setIsDefault(true);
        homeCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
        retId = categoryDAL.addCategory(homeCategory);
        if (retId <= 0)
            return;

        Category otherCategory = new Category(getString(R.string.category_other));
        otherCategory.setIsDefault(true);
        otherCategory.setCreationTime_UTC(DateTime.getNowUTCTimeLong());
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

    public long addReminder(Reminder reminder)
    {
        return reminderDAL.addReminder(reminder);
    }

    public List<Reminder> getRemindersByCategoryID(long categoryID)
    {
        return reminderDAL.getRemindersByCategoryID(categoryID);
    }
}
