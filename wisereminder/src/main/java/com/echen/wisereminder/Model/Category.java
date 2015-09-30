package com.echen.wisereminder.Model;

import com.echen.wisereminder.Data.DataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/4/22.
 */
public class Category implements IListItem, IReminderParent {

    protected long id = -1;
    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    protected String name = "";
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    protected List<IListItem> children = new ArrayList<>();
    public List<IListItem> getChildren() { return this.children; }
//    public void setChildren(List<IListItem> children) { this.children = children; }

    protected String color = "#FFFFFF";
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    protected boolean isDefault = false;
    public boolean getIsDefault() {return isDefault;}
    public void setIsDefault(boolean isDefault){this.isDefault = isDefault;}

    protected long creationTime_UTC = 0;
    public long getCreationTime_UTC() {return creationTime_UTC;}
    public void setCreationTime_UTC(long creationTime_UTC) { this.creationTime_UTC = creationTime_UTC; }

    @Override
    public List<Reminder> getReminders() {
        return DataManager.getInstance().getRemindersByCategoryID(id,false);
    }

    public Category(){}

    public Category(String name)
    {
        this.name = name;
    }


}
