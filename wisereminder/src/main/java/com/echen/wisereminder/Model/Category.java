package com.echen.wisereminder.Model;

/**
 * Created by echen on 2015/4/22.
 */
public class Category {

    protected long id = -1;
    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    protected String name = "";
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    protected boolean isDefault = false;
    public boolean getIsDefault() {return isDefault;}
    public void setIsDefault(boolean isDefault){this.isDefault = isDefault;}


    public Category(){}

    public Category(String name)
    {
        this.name = name;
    }
}
