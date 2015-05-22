package com.echen.androidcommon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by echen on 2015/5/20.
 */
public class DateTime {
    public static String DATETIMEFORMAT = "yyyy-MM-dd HH:mm";
    public static String getNowUTCTimeStr(String format) {
        String strFormat = (null == format || format.isEmpty())?DATETIMEFORMAT:format;
        DateFormat dateFormat = new SimpleDateFormat(strFormat) ;
        StringBuffer UTCTimeBuffer = new StringBuffer();
//        // 1、get local time
//        Calendar cal = Calendar.getInstance() ;
//        // 2、get time offset：
//        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
//        // 3、取得夏令时差：
//        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
//        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
//        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        Calendar cal = getUTCCalendarFromLocal(new Date());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        UTCTimeBuffer.append(year).append("-").append(month).append("-").append(day) ;
        UTCTimeBuffer.append(" ").append(hour).append(":").append(minute) ;
        try{
            dateFormat.parse(UTCTimeBuffer.toString()) ;
            return UTCTimeBuffer.toString() ;
        }catch(ParseException e)
        {
            e.printStackTrace() ;
        }
        return null ;
    }

    public static long getNowUTCTimeLong() {
        Calendar cal = getUTCCalendarFromLocal(new Date());
        return cal.getTime().getTime();
    }

    public static Date getNowUTCDate()
    {
        Calendar cal = getUTCCalendarFromLocal(new Date());
        return cal.getTime();
    }

    public static Calendar getUTCCalendarFromLocal(Date date)
    {
        Calendar calendar = Calendar.getInstance() ;
        if (null == date)
            date = new Date();
        calendar.setTime(date);
        // 2、get time offset：
        int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        calendar.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return calendar;
    }

    public static Date getLocalTimeFromUTC(long milliseconds)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        // 2、get time offset：
        int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
        // 4、从UTC时间里加上这些差量，即可以取得本地时间：
        calendar.add(java.util.Calendar.MILLISECOND, (zoneOffset + dstOffset));
        return calendar.getTime();
    }

    public static String getLocalTimeStrFromUTC(long milliseconds)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIMEFORMAT);
        return sdf.format(getLocalTimeFromUTC(milliseconds));
    }
}
