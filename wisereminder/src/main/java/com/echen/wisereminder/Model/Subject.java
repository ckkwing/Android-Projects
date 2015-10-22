package com.echen.wisereminder.Model;

import com.echen.wisereminder.Data.DataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/6/3.
 */
public class Subject implements IListItem, IReminderParent {
    public Subject() {
        super();
    }

    @Override
    public List<Reminder> getReminders() {
        return DataManager.getInstance().getRemindersBySubject(type,true);
    }

    public enum Type
    {
        Unknown,
        All,
        Star,
        Overdue,
        Today,
        Next7Days,
        Categories,
    }

    public Subject(Type type, String name)
    {
        this.type = type;
        this.name = name;
    }

    protected String name = "";
    @Override
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    protected List<IListItem> children = new ArrayList<>();
    public List<IListItem> getChildren()
    {
        return this.children;
    }
//    public void setChildren(List<IListItem> children) { this.children = children; }

    protected String color = "#FFFFFF";
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    protected Type type = Type.Unknown;
    public Type getType(){return type;}
    public void setType(Type type){this.type = type;}
}
