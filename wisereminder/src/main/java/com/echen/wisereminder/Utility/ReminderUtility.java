package com.echen.wisereminder.Utility;


import android.app.Application;
import android.content.Context;
import android.graphics.Color;

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
}
