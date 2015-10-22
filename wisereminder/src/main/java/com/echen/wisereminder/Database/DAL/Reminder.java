package com.echen.wisereminder.Database.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Database.ReminderTable;
import com.echen.wisereminder.Database.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/5/15.
 */
public class Reminder {
    private SQLiteHelper helper;
    private SQLiteDatabase db;

    //SQL query
    private String QUERY_REMINDERS = "SELECT * FROM " + ReminderTable.TABLE_NAME + " WHERE " + ReminderTable.COMPLETED + "=0";
    private String QUERY_REMINDERS_BY_OWNER_ID = "SELECT * FROM " + ReminderTable.TABLE_NAME + " WHERE " + ReminderTable.OWNER_ID + "=%s AND " + ReminderTable.COMPLETED + "=0";
    private String QUERY_REMINDERS_STAR = "SELECT * FROM " + ReminderTable.TABLE_NAME + " WHERE " + ReminderTable.STAR + "=1 AND " + ReminderTable.COMPLETED + "=0";
    private String QUERY_REMINDERS_TODAY = "SELECT * FROM " + ReminderTable.TABLE_NAME + " WHERE " + ReminderTable.DUE_TIME + ">=%s AND " + ReminderTable.DUE_TIME + "<%s";
    private String QUERY_REMINDERS_OVERDUE = "SELECT * FROM " + ReminderTable.TABLE_NAME + " WHERE " + ReminderTable.DUE_TIME + ">=0 AND " + ReminderTable.DUE_TIME + "<%s";
    private String QUERY_REMINDERS_NEXT7DAYS = QUERY_REMINDERS_TODAY;

    public Reminder(Context context)
    {
        helper = SQLiteHelper.getInstance(context);
        db = helper.getWritableDatabase();
    }

    public long addReminder(com.echen.wisereminder.Model.Reminder reminder)
    {
        ContentValues cv = ReminderTable.ConverterReminderToDBRow(reminder);
        long result = db.insert(ReminderTable.TABLE_NAME, ReminderTable.NAME, cv);
        return result;
    }

    public long updateReminderByID(com.echen.wisereminder.Model.Reminder reminder)
    {
        ContentValues cv = ReminderTable.ConverterReminderToDBRow(reminder);
        return db.update(ReminderTable.TABLE_NAME,cv, ReminderTable.ID+"=?", new String[] {String.valueOf(reminder.getId())});
    }

    private List<com.echen.wisereminder.Model.Reminder> getRemindersByQuery(String query)
    {
        List<com.echen.wisereminder.Model.Reminder> reminders = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if (null != cursor)
        {
            while (cursor.moveToNext())
            {
                com.echen.wisereminder.Model.Reminder reminder = ReminderTable.ConvertDBRowToReminder(cursor);
                if (null != reminder)
                    reminders.add(reminder);
            }
            cursor.close();
        }
        return reminders;
    }

    public List<com.echen.wisereminder.Model.Reminder> getReminders()
    {
        return getRemindersByQuery(QUERY_REMINDERS);
    }

    public List<com.echen.wisereminder.Model.Reminder> getRemindersByCategoryID(long categoryID)
    {
        if (-1 == categoryID)
            return new ArrayList<>();
        String querySql = String.format(QUERY_REMINDERS_BY_OWNER_ID, categoryID);
        return getRemindersByQuery(querySql);
    }

    public List<com.echen.wisereminder.Model.Reminder> getStarReminders() {
        return getRemindersByQuery(QUERY_REMINDERS_STAR);
    }

    public List<com.echen.wisereminder.Model.Reminder> getTodayReminders()
    {
        DateTime today = DateTime.today();
        DateTime tomorrow = today.addDays(1);
        String querySql = String.format(QUERY_REMINDERS_TODAY, today.toUTCLong(), tomorrow.toUTCLong());
        return getRemindersByQuery(querySql);
    }

    public List<com.echen.wisereminder.Model.Reminder> getOverdueReminders()
    {
        DateTime today = DateTime.today();
        String querySql = String.format(QUERY_REMINDERS_OVERDUE, today.toUTCLong());
        return getRemindersByQuery(querySql);
    }

    public List<com.echen.wisereminder.Model.Reminder> getNext7DaysReminders()
    {
        DateTime today = DateTime.today();
        DateTime tomorrow = today.addDays(1);
        DateTime next7 = today.addDays(7);
        String querySql = String.format(QUERY_REMINDERS_NEXT7DAYS, tomorrow.toUTCLong(), next7.toUTCLong());
        return getRemindersByQuery(querySql);
    }

    public List<com.echen.wisereminder.Model.Reminder> getTodoList()
    {
        return new ArrayList<>();
    }
}
