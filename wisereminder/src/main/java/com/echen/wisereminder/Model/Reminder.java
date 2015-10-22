package com.echen.wisereminder.Model;

import android.util.Property;

import com.echen.androidcommon.DateTime;
import com.echen.wisereminder.Database.ReminderTable;

import java.util.Date;
import java.util.List;

/**
 * Created by echen on 2015/5/15.
 */
public class Reminder implements IListItem, Cloneable {
    public enum ReminderType {
        Once,
        Everyday,
    }

    public enum Priority {
        LEVEL1, //The highest
        LEVEL2,
        LEVEL3,
        LEVEL4,
    }

    protected long id = -1;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    protected String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<IListItem> getChildren() {
        return null;
    }

    protected long ownerId = -1;

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    protected boolean isStar = false;

    public boolean getIsStar() {
        return isStar;
    }

    public void setIsStar(boolean isStar) {
        this.isStar = isStar;
    }

    protected boolean isCompleted = false;

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    protected long dueTime_UTC = DateTime.minValue().toUTCLong();

    public long getDueTime_UTC() {
        return dueTime_UTC;
    }

    //return local time
    public DateTime getDueTime()
    {
        Date localDate = DateTime.getLocalTimeFromUTC(dueTime_UTC);
        return new DateTime(localDate);
    }

    public void setDueTime_UTC(long dueTime_UTC) {
        this.dueTime_UTC = dueTime_UTC;
    }

    protected long creationTime_UTC = DateTime.minValue().toUTCLong();

    //return local time
    public DateTime getCreationTime()
    {
        Date localDate = DateTime.getLocalTimeFromUTC(creationTime_UTC);
        return new DateTime(localDate);
    }

    public long getCreationTime_UTC() {
        return creationTime_UTC;
    }

    public void setCreationTime_UTC(long creationTime_UTC) {
        this.creationTime_UTC = creationTime_UTC;
    }

    protected long alertTime_UTC = DateTime.minValue().toUTCLong();

    public long getAlertTime_UTC() {
        return alertTime_UTC;
    }

    //return local time
    public DateTime getAlertTime()
    {
        Date localDate = DateTime.getLocalTimeFromUTC(alertTime_UTC);
        return new DateTime(localDate);
    }

    public void setAlertTime_UTC(long alertTime_UTC) {
        this.alertTime_UTC = alertTime_UTC;
    }

    protected ReminderType reminderType = ReminderType.Once;

    public ReminderType getReminderType() {
        return this.reminderType;
    }

    public void setReminderType(ReminderType reminderType) {
        this.reminderType = reminderType;
    }

    protected Priority priority = Priority.LEVEL4;

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority)
    {
        this.priority = priority;
    }

    public Reminder() {
    }

    public Reminder(String name) {
        this.name = name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Reminder clonedReminder = null;
        try
        {
            clonedReminder = (Reminder) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        return clonedReminder;
    }
}
