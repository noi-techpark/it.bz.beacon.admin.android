package it.bz.beacon.adminapp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    public static Date dateStringToDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date dateTimeStringToDate(String dateTimeString) {
        if (dateTimeString == null) {
            return null;
        }
        DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(format.parse(dateTimeString));

            Calendar timeCalendar = Calendar.getInstance();
            String time = dateTimeString.substring(dateTimeString.length() - 5, dateTimeString.length());
            timeCalendar.setTime(timeFormat.parse(time));

            dateCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            dateCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

            return dateCalendar.getTime();
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToDateString(Date date) {
        if (date == null) {
            return "-";
        }
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return format.format(date);
    }

    public static String dateToDateTimeString(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return String.format(Locale.getDefault(), "%s %s", format.format(date), timeFormat.format(date));
    }
}
