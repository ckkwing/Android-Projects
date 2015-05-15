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
    //ID TO CATEGORY OWNER 1-1
    public static final String OWNER_ID = "owner_id";
}