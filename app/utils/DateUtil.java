package utils;


import models.MenuDay;
import models.MenuMonth;
import models.MenuMonthShort;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {


    public static String dayOfMonth(Date date)
    {
        Calendar cal = setupCalendar();
        cal.setTime(date);
       return cal.get(Calendar.DAY_OF_MONTH) + "";
    }

    public static String dayOfWeek(Date date)
    {
        Calendar cal = setupCalendar();
        cal.setTime(date);
       return MenuDay.fromIndex(cal.get(Calendar.DAY_OF_WEEK)-1) + "";
    }


    public static String monthOfYear(Date date)
    {
        Calendar cal = setupCalendar();
        cal.setTime(date);
        return MenuMonth.values()[cal.get(Calendar.MONTH)].name();
    }
    public static String monthOfYearShort(Date date)
    {
        Calendar cal = setupCalendar();
        cal.setTime(date);
        return MenuMonthShort.values()[cal.get(Calendar.MONTH)].name();
    }

    public static Date addOneDay(Date date)
    {
        return addDays(date, 1);
    }
    public static Date addDays(Date date, int days)
    {
        Calendar cal = setupCalendar();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    public static int distance(Date from, Date to)
    {
        Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTime(from);
            calendar2.setTime(to);
            long milliseconds1 = calendar1.getTimeInMillis();
            long milliseconds2 = calendar2.getTimeInMillis();
            long diff = milliseconds2 - milliseconds1;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            return (int)diffDays ;
    }

    private static Calendar setupCalendar(){
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        return cal;
    }
}
