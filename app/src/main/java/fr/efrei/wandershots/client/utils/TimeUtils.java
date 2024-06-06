package fr.efrei.wandershots.client.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static long getElapsedTime(Date startTime) {
        return getElapsedTime(startTime.getTime());
    }

    public static long getElapsedTime(long startTimeMs) {
        return System.currentTimeMillis() - startTimeMs;
    }

    public static String formatTime(Date date) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }

    public static String formatTime(long timeMs) {
        long hours = (timeMs / 1000) / 3600;
        long minutes = ((timeMs / 1000) % 3600) / 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }

    public static String formatDateTime(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date);
    }
}
