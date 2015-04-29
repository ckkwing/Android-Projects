package com.echen.wisereminder.Data;

import android.content.Context;

import com.echen.wisereminder.Model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/4/27.
 */
public class DataManager {
    private Context context = null;
    private volatile static DataManager instance;

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
        return true;
    }

    public void uninit()
    {

    }

    public List<Category> getCategories()
    {
        Category categoryWork = new Category();
        categoryWork.setId(1);
        categoryWork.setName("Work");
        Category categoryTravel = new Category();
        categoryTravel.setId(2);
        categoryTravel.setName("Travel");
        Category categoryTODO = new Category();
        categoryTODO.setId(3);
        categoryTODO.setName("TODO");
        List<Category> categoryList = new ArrayList<Category>();
        categoryList.add(categoryWork);
        categoryList.add(categoryTravel);
        categoryList.add(categoryTODO);
        return categoryList;
    }
}
