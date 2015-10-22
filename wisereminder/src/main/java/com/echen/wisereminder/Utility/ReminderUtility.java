package com.echen.wisereminder.Utility;


import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Category;
import com.echen.wisereminder.Model.Reminder;
import com.echen.wisereminder.R;

/**
 * Created by echen on 2015/9/25.
 */
public class ReminderUtility {
    public static int getPriorityColorInt(Reminder.Priority priority, Context context)
    {
        int iColor = Color.parseColor("#ffffff");
        if (null == context)
            throw new IllegalArgumentException("Context mustn't be null.");
        switch (priority)
        {
            case LEVEL1:
                iColor = context.getResources().getColor(R.color.priority_level1);
                break;
            case LEVEL2:
                iColor = context.getResources().getColor(R.color.priority_level2);
                break;
            case LEVEL3:
                iColor = context.getResources().getColor(R.color.priority_level3);
                break;
            case LEVEL4:
                iColor = context.getResources().getColor(R.color.priority_level4);
                break;
        }
        return iColor;
    }

    public static String getPriorityString(Reminder.Priority priority, Context context)
    {
        String str = "";
        if (null == context)
            throw new IllegalArgumentException("Context mustn't be null.");
        switch (priority)
        {
            case LEVEL1:
                str = context.getString(R.string.priority1);
                break;
            case LEVEL2:
                str = context.getString(R.string.priority2);
                break;
            case LEVEL3:
                str = context.getString(R.string.priority3);
                break;
            case LEVEL4:
                str = context.getString(R.string.priority4);
                break;
        }
        return str;
    }

    public static Category getOwner(Reminder reminder)
    {
        Category category = null;
        if (null == reminder)
            return category;

        for(Category item : DataManager.getInstance().getCategories(false))
        {
            if (reminder.getOwnerId() == item.getId())
            {
                category = item;
                break;
            }
        }
        return category;
    }


}
