package com.echen.wisereminder.Model.Task;

import android.content.Context;

/**
 * Created by echen on 2015/10/19.
 */
public class TaskFactory {
    public static Task createTask(TaskType taskType, Context context)
    {
        Task task = null;
        switch (taskType)
        {
            case CheckReminder:
                task = new CheckAllAlertReminderTask(context);
                break;
        }
        return task;
    }
}
