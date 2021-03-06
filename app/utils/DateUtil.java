package utils;


import models.MenuDay;
import models.MenuMonth;
import models.MenuMonthShort;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {


    public static SimpleDateFormat dateFormatDots = new SimpleDateFormat("dd.MM.yyyy");
    public static SimpleDateFormat dateFormatDash = new SimpleDateFormat("yyyy-MM-dd");


    public static String prettyDate(Date date) {
        return dateFormatDots.format(date);
    }


    public static String dayOfMonth(Date date) {
        Calendar cal = setupCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH) + "";
    }

    public static String dayOfWeek(Date date) {
        Calendar cal = setupCalendar();
        cal.setTime(date);
        return MenuDay.fromIndex(cal.get(Calendar.DAY_OF_WEEK) - 1) + "";
    }
    public static String dayOfWeek(int day) {
        return MenuDay.fromIndex(day) + "";
    }

    public static int weekOfYear(Date date) {
        Calendar cal = setupCalendar();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);

    }

    public static String monthOfYear(Date date) {
        Calendar cal = setupCalendar();
        cal.setTime(date);
        return MenuMonth.values()[cal.get(Calendar.MONTH)].name();
    }

    public static String monthOfYearShort(Date date) {
        Calendar cal = setupCalendar();
        cal.setTime(date);
        return MenuMonthShort.values()[cal.get(Calendar.MONTH)].name();
    }

    public static Date addOneDay(Date date) {
        return addDays(date, 1);
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = setupCalendar();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    public static int distance(Date from, Date to) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(from);
        calendar2.setTime(to);
        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return (int) diffDays;
    }

    private static Calendar setupCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        return cal;
    }

    public static Date getStartingDay() {

        Calendar cal = setupCalendar();
        clear(cal);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.WEEK_OF_YEAR, 1);

        return cal.getTime();
    }

    public static Date getStartingDayThisWeek(Date date) {

        Calendar cal = setupCalendar();
        cal.setTime(date);
        clear(cal);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        return cal.getTime();
    }

    public static Date getStartingDayForWeek(int uke) {
        Calendar cal = setupCalendar();
        cal.set(Calendar.WEEK_OF_YEAR, uke);

        clear(cal);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        return cal.getTime();
    }

    private static void clear(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
    }

}
