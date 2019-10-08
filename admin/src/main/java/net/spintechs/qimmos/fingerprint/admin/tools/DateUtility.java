package net.spintechs.qimmos.fingerprint.admin.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtility {

    final static String  DATE_FORMAT  = "dd-MM-yyyy kk:mm:ss";

    public static Date stringToDateTime(String date) {
        Date dt = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false);
            dt = sdf.parse(date);
            return dt;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date" + e.getMessage());
        }
        return dt;
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }
}
