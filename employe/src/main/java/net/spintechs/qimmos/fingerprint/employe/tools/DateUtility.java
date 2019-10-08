package net.spintechs.qimmos.fingerprint.employe.tools;

import android.text.format.DateFormat;

import java.util.Date;

public class DateUtility {

    public static String DAY_NUMBER = "dd";
    public static String DAY_STRING = "EEEE";
    public static String MONTH_STRING = "MMMMMMMMM";
    public static String YEAR = "yyyy";

    public static String getDayInt(Date date) {
        return (String) DateFormat.format(DAY_NUMBER, date);
    }

    public static String getDayString(Date date) {
        return (String) DateFormat.format(DAY_STRING, date);
    }

    public static String getMonthString(Date date) {
        return (String) DateFormat.format(MONTH_STRING, date);
    }

    public static String getYear(Date date) {
        return (String) DateFormat.format(YEAR, date);
    }

}
