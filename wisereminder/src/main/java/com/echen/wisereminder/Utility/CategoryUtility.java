package com.echen.wisereminder.Utility;

import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Category;

/**
 * Created by echen on 2015/10/10.
 */
public class CategoryUtility {
    public static Category getInboxCategory()
    {
        Category category = null;
        for(Category existCategory : DataManager.getInstance().getCategories(false))
        {
            if (!existCategory.getIsDefault())
                continue;
            if (existCategory.getName().equals("Inbox"))
            {
                category = existCategory;
                break;
            }
        }
        return category;
    }
}
