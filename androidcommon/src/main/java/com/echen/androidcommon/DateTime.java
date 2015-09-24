package com.echen.androidcommon;

import com.echen.androidcommon.Interface.IDateTimeEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by echen on 2015/5/20.
 */
public class DateTime {

    public static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    private IDateTimeEvent m_iDateTimeEvent = null;
    private Calendar m_localCalendar = null;

    public Calendar getLocalCalendar() {
        return m_localCalendar;
    }

    public DateTime() {
        m_localCalendar = Calendar.getInstance();
    }

    public DateTime(Calendar calendar) {
        m_localCalendar = calendar;
    }

    public DateTime(Date date) {
        m_localCalendar = Calendar.getInstance();
        m_localCalendar.setTime(date);
    }

    public void setDateTimeEvent(IDateTimeEvent event)
    {
        this.m_iDateTimeEvent = event;
    }

    public void update(int year, int month, int day)
    {
        m_localCalendar.set(year,month,day);
        if (null != m_iDateTimeEvent)
            m_iDateTimeEvent.DateTimeChanged(this);
    }

    public void update(int year, int month, int day, int hourOfDay, int minute)
    {
        m_localCalendar.set(year,month,day, hourOfDay, minute);
        if (null != m_iDateTimeEvent)
            m_iDateTimeEvent.DateTimeChanged(this);
    }

    public void update(Date date)
    {
        m_localCalendar.setTime(date);
        if (null != m_iDateTimeEvent)
            m_iDateTimeEvent.DateTimeChanged(this);
    }

    @Override
    public String toString() {
        return toString(DATETIME_FORMAT);
    }

        public String toString(String format) {
        String strFormat = (null == format || format.isEmpty()) ? DATETIME_FORMAT : format;
        DateFormat dateFormat = new SimpleDateFormat(strFormat);
        return dateFormat.format(m_localCalendar.getTime());
    }

    public static String getNowUTCTimeStr(String format) {
        String strFormat = (null == format || format.isEmpty()) ? DATETIME_FORMAT : format;
        DateFormat dateFormat = new SimpleDateFormat(strFormat);
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
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        UTCTimeBuffer.append(year).append("-").append(month).append("-").append(day);
        UTCTimeBuffer.append(" ").append(hour).append(":").append(minute);
        try {
            dateFormat.parse(UTCTimeBuffer.toString());
            return UTCTimeBuffer.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getNowUTCTimeLong() {
        Calendar cal = getUTCCalendarFromLocal(new Date());
        return cal.getTime().getTime();
    }

    public static Date getNowLocalDate() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    public static Date getNowUTCDate() {
        Calendar cal = getUTCCalendarFromLocal(new Date());
        return cal.getTime();
    }

    public static Calendar getUTCCalendarFromLocal(Date date) {
        Calendar calendar = Calendar.getInstance();
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

    public static Date getLocalTimeFromUTC(long milliseconds) {
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

    public static String getLocalTimeStrFromUTC(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
        return sdf.format(getLocalTimeFromUTC(milliseconds));
    }
}
