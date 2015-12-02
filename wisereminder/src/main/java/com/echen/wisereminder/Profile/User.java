package com.echen.wisereminder.Profile;

import android.provider.ContactsContract;
import android.text.TextUtils;

import com.echen.androidcommon.Utility.Utility;

import java.security.InvalidParameterException;

/**
 * Created by echen on 2015/10/27.
 */
public class User {
    private String name = "";

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String email = "";

    public String getEmail() {
        return this.email;
    }

    public boolean setEmail(String email) {
        if (!Utility.isEmail(email))
            return false;
        this.email = email;
        return true;
    }

    public User(String name, String email)
    {
        this.name = name;
        if (!setEmail(email))
            throw new InvalidParameterException("Invalid email format");
    }
}
