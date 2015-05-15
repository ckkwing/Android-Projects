package com.echen.wisereminder.Model;

import java.util.List;

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


    public Category(){}

    public Category(String name)
    {
        this.name = name;
    }
}
