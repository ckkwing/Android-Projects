package com.echen.wisereminder.Model;

/**
 * Created by echen on 2015/4/22.
 */
public class Category {

    protected int id = -1;
    public int getId(){return id;}
    public void setId(int id){this.id = id;}

    protected String name = "";
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public Category(){}
}
