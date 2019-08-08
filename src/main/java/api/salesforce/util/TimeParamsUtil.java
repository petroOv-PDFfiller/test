package api.salesforce.util;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;

public class TimeParamsUtil {

    public static String getFormattedDay(int day) {
        return DayOfWeek.of(day).getDisplayName(TextStyle.SHORT, Locale.UK).toUpperCase();
    }

    public static String getFormattedHours(int hours) {
        hours = hours == 0 ? 12 : hours;
        return String.format("%02d", hours);
    }

    public static String getFormattedMinutes(int minutes) {
        return String.format("%02d", minutes);
    }

    public static String getFormattedMeridiem(int meridiem) {
        return meridiem == Calendar.PM ? "PM" : "AM";
    }

    public static int getCalendarHours(String hours) {
        return Integer.parseInt(hours) == 12 ? 0 : Integer.parseInt(hours);
    }

    public static int getCalendarMinutes(String minutes) {
        return Integer.parseInt(minutes);
    }

    public static int getCalendarMeridiem(String meridiem) {
        return meridiem.equals("PM") ? 1 : 0;
    }
}
