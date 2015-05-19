package com.echen.wisereminder.Database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.echen.androidcommon.Model.File;

/**
 * Created by echen on 2015/4/28.
 */
public abstract class BaseSQLiteHelper extends SQLiteOpenHelper {
    protected static final String DATABASE_NAME = "wr_data.db";
    protected static final int DATABASE_VERSION = 1;

    public BaseSQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE CATEGORY TABLE
//        db.execSQL("CREATE TABLE " + CategoryTable.TABLE_NAME +
//                " (" + CategoryTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + CategoryTable.NAME + " TEXT);");

        db.execSQL(CategoryTable.getCreateTableSqlString());
        db.execSQL(ReminderTable.getCreateTableSqlString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("LOG_TAG", "Upgrading database from version "
                + oldVersion + " to " + newVersion + ",which will destroy all old data");
        // KILL PREVIOUS TABLES IF UPGRADED
        db.execSQL("DROP TABLE IF EXISTS " + CategoryTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReminderTable.TABLE_NAME);
        // CREATE NEW INSTANCE OF SCHEMA
        onCreate(db);
    }
}
