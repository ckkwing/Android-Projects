package com.example.echen.myapplication.Model;

/**
 * Created by echen on 2015/1/21.
 */
public class People {

    private String name;
    public String getName()
    {
        return name;
    }

    public void setName(String value)
    {
        name = value;
    }

    private int age = 0;
    public int getAge()
    {
        return age;
    }

    public void setAge(int value)
    {
        age = value;
    }

    public People()
    {

    }

    public String toString()
    {
        return "My name is " + getName() + ", I'm " + Integer.toString(getAge()) + " years old.";
    }
}
