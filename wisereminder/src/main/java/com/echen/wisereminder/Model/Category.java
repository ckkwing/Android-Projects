package com.echen.wisereminder.Model;

/**
 * Created by echen on 2015/4/22.
 */
public class Category implements IListItem {

    protected long id = -1;
    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    protected String name = "";
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    protected String color = "#FFFFFF";
    public String getColor(){return color;}
    public void setColor(String color){this.color = color;}

    protected boolean isDefault = false;
    public boolean getIsDefault() {return isDefault;}
    public void setIsDefault(boolean isDefault){this.isDefault = isDefault;}

    protected long creationTime_UTC = 0;
    public long getCreationTime_UTC() {return creationTime_UTC;}
    public void setCreationTime_UTC(long creationTime_UTC) { this.creationTime_UTC = creationTime_UTC; }


    public Category(){}

    public Category(String name)
    {
        this.name = name;
    }

    @Override
    public String getItemName() {
        return this.name;
    }

    @Override
    public String getItemColor() {
        return color;
    }
}
