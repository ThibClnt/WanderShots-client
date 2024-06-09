package fr.efrei.wandershots.client.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class provides utility methods for time.
 */
public class TimeUtils {
    /**
     * This method returns the elapsed time since the given start time, in milliseconds.
     */
    public static long getElapsedTime(Date startTime) {
        return getElapsedTime(startTime.getTime());
    }

    /**
     * This method returns the elapsed time since the given start time, in milliseconds.
     */
    public static long getElapsedTime(long startTimeMs) {
        return System.currentTimeMillis() - startTimeMs;
    }

    /**
     * This method formats the given date as a time string.
     */
    public static String formatTime(Date date) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }

    /**
     * This method formats the given duration in milliseconds as a time string.
     */
    public static String formatTime(long timeMs) {
        long hours = (timeMs / 1000) / 3600;
        long minutes = ((timeMs / 1000) % 3600) / 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }

    /**
     * This method formats the given date as a date and time string.
     */
    public static String formatDateTime(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date);
    }

    /**
     * This method returns the current date and time as a formatted string.
     */
    public static String getCurrentDateTimeString() {
        return formatDateTime(new Date());
    }
}
