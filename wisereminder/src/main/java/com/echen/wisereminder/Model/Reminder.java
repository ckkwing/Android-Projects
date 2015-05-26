package com.echen.wisereminder.Model;

import com.echen.wisereminder.Database.ReminderTable;

/**
 * Created by echen on 2015/5/15.
 */
public class Reminder {
    public enum ReminderType
    {
        Once,
        Everyday,
    }

    protected long id = -1;
    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    protected String name = "";
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    protected long ownerId = -1;
    public long getOwnerId() { return ownerId; }
    public void setOwnerId(long ownerId) { this.ownerId = ownerId; }

    protected long creationTime_UTC = 0;
    public long getCreationTime_UTC() {return creationTime_UTC;}
    public void setCreationTime_UTC(long creationTime_UTC) { this.creationTime_UTC = creationTime_UTC; }

    protected ReminderType reminderType = ReminderType.Once;
    public ReminderType getReminderType() { return this.reminderType; }
    public void setReminderType(ReminderType reminderType) { this.reminderType = reminderType; }

    public Reminder(){}

    public Reminder(String name)
    {
        this.name = name;
    }
}
