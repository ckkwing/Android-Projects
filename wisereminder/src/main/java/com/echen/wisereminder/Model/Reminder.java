package com.echen.wisereminder.Model;

/**
 * Created by echen on 2015/5/15.
 */
public class Reminder {
    protected long id = -1;
    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    protected String name = "";
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public Reminder(){}

    public Reminder(String name)
    {
        this.name = name;
    }
}
