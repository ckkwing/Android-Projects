package com.echen.wisereminder.Database.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.echen.wisereminder.Database.CategorySQLiteHelper;
import com.echen.wisereminder.Database.CategoryTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/4/29.
 */
public class Category {
    private CategorySQLiteHelper helper;
    private SQLiteDatabase db;

    //SQL query
    private String QUERY_CATEGORIES = "SELECT * FROM " + CategoryTable.TABLE_NAME;

    public Category(Context context)
    {
        helper = new CategorySQLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public void close()
    {
        if (null != db)
        {
            db.close();
        }
    }

    public long addCategory(com.echen.wisereminder.Model.Category category)
    {
        // CREATE A CONTENTVALUE OBJECT
        ContentValues cv = new ContentValues();
        cv.put(CategoryTable.NAME, category.getName());
        // RETRIEVE WRITEABLE DATABASE AND INSERT
        long result = db.insert(CategoryTable.TABLE_NAME,
                CategoryTable.NAME, cv);
        return result;
    }

    public List<com.echen.wisereminder.Model.Category> getCategories()
    {
        List<com.echen.wisereminder.Model.Category> categories = new ArrayList<com.echen.wisereminder.Model.Category>();
        Cursor cursor = db.rawQuery(QUERY_CATEGORIES, null);
        if (null != cursor)
        {
            while (cursor.moveToNext())
            {
                com.echen.wisereminder.Model.Category category = new com.echen.wisereminder.Model.Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(CategoryTable.ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(CategoryTable.NAME)));
                categories.add(category);
            }
            cursor.close();
        }
        return categories;
    }

    public void clearCategory(com.echen.wisereminder.Model.Category category)
    {
        if (null == category)
            return;
        db.execSQL("DELETE FROM " + CategoryTable.TABLE_NAME + " WHERE " + CategoryTable.ID + "=" + String.valueOf(category.getId()));
    }

    public void clearCategories()
    {
        db.execSQL("DELETE FROM " + CategoryTable.TABLE_NAME);
    }
}
