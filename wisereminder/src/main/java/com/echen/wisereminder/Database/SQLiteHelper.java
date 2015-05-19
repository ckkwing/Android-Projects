package com.echen.wisereminder.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by echen on 2015/5/18.
 */
public class SQLiteHelper extends BaseSQLiteHelper {
    private volatile static SQLiteHelper instance = null;

    public static SQLiteHelper getInstance(Context context)
    {
        if (null == instance)
        {
            synchronized (SQLiteHelper.class)
            {
                if (null == instance)
                {
                    instance = new SQLiteHelper(context);
                }
            }
        }
        return instance;
    }

    private SQLiteHelper(Context context) {
        super(context);
    }
}
