package com.echen.androidcommon;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by echen on 2015/10/30.
 */
public class DeviceHelper {

    public static DisplayMetrics getDisplayMetrics(Context context)
    {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(displayMetrics);
        return displayMetrics;
    }

}
