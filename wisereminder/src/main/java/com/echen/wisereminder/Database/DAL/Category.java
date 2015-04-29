package com.echen.wisereminder.Database.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.echen.wisereminder.Database.CategorySQLiteHelper;
import com.echen.wisereminder.Database.CategoryTable;

import java.util.List;

/**
 * Created by echen on 2015/4/29.
 */
public class Category {
    private CategorySQLiteHelper helper;
    private SQLiteDatabase db;
    public Category(Context context)
    {
        helper = new CategorySQLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public long AddCategory(com.echen.wisereminder.Model.Category category)
    {
        // CREATE A CONTENTVALUE OBJECT
        ContentValues cv = new ContentValues();
        cv.put(CategoryTable.NAME, category.getName());
        // RETRIEVE WRITEABLE DATABASE AND INSERT
        long result = db.insert(CategoryTable.TABLE_NAME,
                CategoryTable.NAME, cv);
        return result;
    }

//    public List<com.echen.wisereminder.Model.Category> GetCategories()
//    {
//
//    }
}
