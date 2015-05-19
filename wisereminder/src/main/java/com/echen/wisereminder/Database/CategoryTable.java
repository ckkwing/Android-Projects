package com.echen.wisereminder.Database;

/**
 * Created by echen on 2015/4/29.
 */
public class CategoryTable {
    // NAME OF THE TABLE
    public static final String TABLE_NAME = "category";

    // EACH CATEGORY HAS UNIQUE ID
    public static final String ID = "_id";
    // NAME OF THE CATEGORY
    public static final String NAME = "category_name";

    //0 == default(system created),
    //1 == customer created
    public static final String FLAG = "flag";

    public static boolean flagToIsDefault(int flag)
    {
        boolean isDefault = false;
        if (0 == flag)
            isDefault = true;
        return isDefault;
    }

    public static int isDefaultToFlag(boolean isDefault)
    {
        int flag = 1;
        if (isDefault)
            flag = 0;
        return flag;
    }

    public static String getCreateTableSqlString()
    {
        final String creation = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NAME + " TEXT," +
                FLAG + " INT" +
                ");";
        return creation;
    }
}
