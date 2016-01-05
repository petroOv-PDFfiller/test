package utils;

import ru.yandex.qatools.allure.annotations.Step;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vladyslav on 26.06.2015.
 */
public class TimeMan {

    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    public static String getCurrentDate(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static String format(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    @Step("Sleep for {0} seconds")
    public static void sleep(int seconds) {
        Logger.info("Sleeping for [" + seconds + "] seconds");
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("Sleep for {0} minutes")
    public static void sleep(double minutes) {
        Logger.info("Sleeping for [" + minutes + "] minutes");
        try {
            Thread.sleep((long)(minutes * 60 * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("Sleep for {0} milliseconds")
    public static void sleep(long millis) {
        Logger.info("Sleeping for [" + millis + "] milliseconds");
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* Parsing date from string */
    public static Calendar parse(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        Logger.info("Parsing date from string: '" + date + "'");
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
            Logger.error("Wrong date: '" + date + "'");
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        return calendar;
    }

    public static long getDateDiff(Date first, Date second, TimeUnit timeUnit) {
        long diffInMillies = second.getTime() - first.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
