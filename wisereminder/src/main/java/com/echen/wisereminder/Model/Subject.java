package com.echen.wisereminder.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/6/3.
 */
public class Subject {
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
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    protected Type type = Type.Unknown;
    public Type getType(){return type;}
    public void setType(Type type){this.type = type;}

    protected List<IChildSubject> children = new ArrayList<>();
    public List<IChildSubject> getChildren() { return this.children; }
    public void setChildren(List<IChildSubject> children) { this.children = children; }
}
