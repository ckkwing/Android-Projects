package com.echen.wisereminder.Database;

import android.content.ContentValues;
import android.database.Cursor;

import com.echen.wisereminder.Model.Reminder;

/**
 * Created by echen on 2015/5/15.
 */
public class ReminderTable {
    // NAME OF THE TABLE
    public static final String TABLE_NAME = "reminder";

    // EACH CATEGORY HAS UNIQUE ID
    public static final String ID = "_id";
    // NAME OF THE ReminderItem
    public static final String NAME = "item_name";
    //ID TO CATEGORY OWNER (-1 is no parent)
    public static final String OWNER_ID = "owner_id";

    //0 is false, 1 is true
    public static final String STAR = "star";

    public static final String PRIORITY = "priority";

    public static final String DUE_TIME = "due_time_utc";

    public static final String CREATION_TIME = "creation_time_utc";

    public static final String MODIFIED_TIME = "modified_time_utc";

    public static final String ALERT_TIME = "alert_time_utc";

    //0 is in process, 1 is completed
    public static final String COMPLETED = "completed";

    public static boolean dbValueToIsStar(int star)
    {
        boolean isStar = false;
        if (1 == star)
            isStar = true;
        return isStar;
    }

    public static int isStarToDBValue(boolean star){
        int iStar = 0;
        if (star)
            iStar = 1;
        return iStar;
    }

    public static boolean dbValueToIsCompleted(int completed)
    {
        boolean isCompleted = false;
        if (1 == completed)
            isCompleted = true;
        return isCompleted;
    }


    public static int isCompletedToDBValue(boolean completed){
        int isCompleted = 0;
        if (completed)
            isCompleted = 1;
        return isCompleted;
    }

    public static String getCreateTableSqlString()
    {
        final String creation = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NAME + " TEXT," +
                OWNER_ID + " INTEGER," +
                STAR + " INTEGER," +
                PRIORITY + " INTEGER," +
                COMPLETED + " INTEGER," +
                DUE_TIME + " INTEGER," +
                CREATION_TIME + " INTEGER," +
                MODIFIED_TIME + " INTEGER," +
                ALERT_TIME + " INTEGER" +
                ");";
        return creation;
    }

    public static Reminder ConvertDBRowToReminder(Cursor cursor)
    {
        Reminder reminder = null;
        if (null == cursor)
            return reminder;
        reminder = new Reminder();
        reminder.setId(cursor.getInt(cursor.getColumnIndex(ReminderTable.ID)));
        reminder.setName(cursor.getString(cursor.getColumnIndex(ReminderTable.NAME)));
        reminder.setOwnerId(cursor.getInt(cursor.getColumnIndex(ReminderTable.OWNER_ID)));
        Integer isStar = cursor.getInt(cursor.getColumnIndex(ReminderTable.STAR));
        reminder.setIsStar(ReminderTable.dbValueToIsStar(isStar));
        Integer isCompleted = cursor.getInt(cursor.getColumnIndex(ReminderTable.COMPLETED));
        reminder.setIsCompleted(ReminderTable.dbValueToIsCompleted(isCompleted));
        reminder.setDueTime_UTC(cursor.getLong(cursor.getColumnIndex(ReminderTable.DUE_TIME)));
        reminder.setCreationTime_UTC(cursor.getLong(cursor.getColumnIndex(ReminderTable.CREATION_TIME)));
        Integer priority = cursor.getInt(cursor.getColumnIndex(ReminderTable.PRIORITY));
        reminder.setPriority(Reminder.Priority.values()[priority]);
        reminder.setAlertTime_UTC(cursor.getLong(cursor.getColumnIndex(ReminderTable.ALERT_TIME)));
        return reminder;
    }

    public static ContentValues ConverterReminderToDBRow(Reminder reminder)
    {
        ContentValues cv = new ContentValues();
        cv.put(ReminderTable.NAME, reminder.getName());
        cv.put(ReminderTable.OWNER_ID, reminder.getOwnerId());
        cv.put(ReminderTable.STAR, ReminderTable.isStarToDBValue(reminder.getIsStar()));
        cv.put(ReminderTable.COMPLETED, ReminderTable.isCompletedToDBValue(reminder.getIsCompleted()));
        cv.put(ReminderTable.CREATION_TIME, reminder.getCreationTime_UTC());
        cv.put(ReminderTable.DUE_TIME, reminder.getDueTime_UTC());
        cv.put(ReminderTable.PRIORITY, reminder.getPriority().ordinal());
        cv.put(ReminderTable.ALERT_TIME, reminder.getAlertTime_UTC());
        return cv;
    }
}
