package com.echen.wisereminder.Data;

import android.content.Context;

import com.echen.wisereminder.Database.CategorySQLiteHelper;
import com.echen.wisereminder.Model.Category;
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
//        categoryDAL.clearCategories();
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
        long retId = categoryDAL.addCategory(allCategory);
        if (retId <= 0)
            return;
        allCategory.setId(retId);

        Category workCategory = new Category(getString(R.string.category_work));
        retId = categoryDAL.addCategory(workCategory);
        if (retId <= 0)
            return;
        workCategory.setId(retId);

        Category homeCategory = new Category(getString(R.string.category_home));
        retId = categoryDAL.addCategory(homeCategory);
        if (retId <= 0)
            return;
        homeCategory.setId(retId);

        Category otherCategory = new Category(getString(R.string.category_other));
        retId = categoryDAL.addCategory(otherCategory);
        if (retId <= 0)
            return;
        otherCategory.setId(retId);
    }

    public List<Category> getCategories(boolean isForce)
    {
        if (isForce)
        {
            categories = categoryDAL.getCategories();
        }
        return categories;
    }
}
