package com.echen.wisereminder.Database.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Database.ReminderTable;
import com.echen.wisereminder.Database.SQLiteHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by echen on 2015/5/15.
 */
public class Reminder {
    private SQLiteHelper helper;
    private SQLiteDatabase db;

    //SQL query
    private String QUERY_ALL_REMINDERS = "SELECT * FROM " + ReminderTable.TABLE_NAME;
    private String QUERY_REMINDERS_BY_OWNER_ID = "SELECT * FROM " + ReminderTable.TABLE_NAME + " WHERE " + ReminderTable.OWNER_ID + "=%s";

    public Reminder(Context context)
    {
        helper = SQLiteHelper.getInstance(context);
        db = helper.getWritableDatabase();
    }

    public long addReminder(com.echen.wisereminder.Model.Reminder reminder)
    {
        // CREATE A CONTENTVALUE OBJECT
        ContentValues cv = new ContentValues();
        cv.put(ReminderTable.NAME, reminder.getName());
        cv.put(ReminderTable.OWNER_ID, reminder.getOwnerId());
        cv.put(ReminderTable.CREATION_TIME, DateTime.getNowUTCTimeStr(null));
        // RETRIEVE WRITEABLE DATABASE AND INSERT
        long result = db.insert(ReminderTable.TABLE_NAME,ReminderTable.NAME, cv);
        return result;
    }

    public List<com.echen.wisereminder.Model.Reminder> getRemindersByCategoryID(long categoryID)
    {
        List<com.echen.wisereminder.Model.Reminder> reminders = new ArrayList<>();
        if (-1 != categoryID)
        {
            String querySql = String.format(QUERY_REMINDERS_BY_OWNER_ID, categoryID);
            Cursor cursor = db.rawQuery(querySql, null);
            if (null != cursor)
            {
                while (cursor.moveToNext())
                {
                    com.echen.wisereminder.Model.Reminder reminder = new com.echen.wisereminder.Model.Reminder();
                    reminder.setId(cursor.getInt(cursor.getColumnIndex(ReminderTable.ID)));
                    reminder.setName(cursor.getString(cursor.getColumnIndex(ReminderTable.NAME)));
                    reminder.setOwnerId(categoryID);
                    reminders.add(reminder);
                }
                cursor.close();
            }
        }
        return reminders;
    }
}
