package vubq.warehouse_management.VT_EcoStorage.utils.dates;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * DateUtils - Utility class for handling date and time operations.
 */
public class DateUtils {

    public static final long SECOND_MILLIS = 1000L;
    public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final long YEAR_MILLIS = 365 * DAY_MILLIS;

    private static final Calendar calendar = new GregorianCalendar();

    /**
     * Formats a Date object using the given pattern.
     */
    public static String format(Date date, String pattern) {
        if (date == null || pattern == null) return null;
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * Formats a Timestamp using the given pattern.
     */
    public static String format(Timestamp timestamp, String pattern) {
        return timestamp == null ? null : format(new Date(timestamp.getTime()), pattern);
    }

    /**
     * Returns current time formatted with the given pattern.
     */
    public static String nowByFormat(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * Get milliseconds part of a Date object (0 - 999).
     */
    public static int getMillisecond(Date date) {
        if (date == null) return 0;
        calendar.setTime(date);
        return calendar.get(Calendar.MILLISECOND);
    }

    /**
     * Returns difference in hours between two Date objects.
     */
    public static float hoursDifference(Date date1, Date date2) {
        if (date1 == null || date2 == null) return 0f;
        long diffMillis = date1.getTime() - date2.getTime();
        return (float) diffMillis / HOUR_MILLIS;
    }

    /**
     * Converts LocalDate to Date.
     */
    public static Date asDate(LocalDate localDate) {
        return localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts LocalDateTime to Date.
     */
    public static Date asDate(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts Date to LocalDate.
     */
    public static LocalDate asLocalDate(Date date) {
        return date == null ? null : Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Converts Date to LocalDateTime.
     */
    public static LocalDateTime asLocalDateTime(Date date) {
        return date == null ? null : Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Returns current system time as Date.
     */
    public static Date getCurrentTime() {
        return new Date();
    }

    /**
     * Calculates difference in days between two dates.
     */
    public static int daysDiff(Date earlierDate, Date laterDate) {
        if (earlierDate == null || laterDate == null) return 0;
        long diffMillis = laterDate.getTime() - earlierDate.getTime();
        return (int) TimeUnit.MILLISECONDS.toDays(diffMillis);
    }

    /**
     * Checks if the given date is today.
     */
    public static boolean isToday(Date date) {
        if (date == null) return false;
        LocalDate inputDate = asLocalDate(date);
        return LocalDate.now().equals(inputDate);
    }

    /**
     * Adds days to a date.
     */
    public static Date addDays(Date date, int days) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * Adds months to a date.
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    /**
     * Returns the start of the day (00:00:00) for a given date.
     */
    public static Date startOfDay(Date date) {
        if (date == null) return null;
        LocalDateTime ldt = asLocalDate(date).atStartOfDay();
        return asDate(ldt);
    }

    /**
     * Returns the end of the day (23:59:59.999) for a given date.
     */
    public static Date endOfDay(Date date) {
        if (date == null) return null;
        LocalDateTime ldt = asLocalDate(date).atTime(23, 59, 59, 999_000_000);
        return asDate(ldt);
    }

    /**
     * Returns current ISO 8601 timestamp (e.g. 2025-06-19T16:00:00Z)
     */
    public static String nowIso() {
        return Instant.now().toString();
    }
}
