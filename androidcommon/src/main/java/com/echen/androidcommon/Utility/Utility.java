package com.echen.androidcommon.Utility;

import junit.framework.Test;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by echen on 2015/1/26.
 */
public class Utility {

    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
//        Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //simple match
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//complicated match
        Matcher m = p.matcher(email);
        return m.matches();
    }

}
