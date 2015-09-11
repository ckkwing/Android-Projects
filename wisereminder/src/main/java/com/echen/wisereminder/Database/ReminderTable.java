package com.echen.wisereminder.Database;

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

    public static final String CREATION_TIME = "creation_time_utc";

    public static final String MODIFIED_TIME = "modified_time_utc";

    public static String getCreateTableSqlString()
    {
        final String creation = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NAME + " TEXT," +
                OWNER_ID + " INTEGER," +
                CREATION_TIME + " INTEGER," +
                MODIFIED_TIME + " INTEGER" +
                ");";
        return creation;
    }
}
