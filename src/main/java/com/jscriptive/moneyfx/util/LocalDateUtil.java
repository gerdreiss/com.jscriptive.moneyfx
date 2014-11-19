package com.jscriptive.moneyfx.util;

import java.time.*;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Created by jscriptive.com on 19/11/14.
 */
public class LocalDateUtil {

    public static long toMillis(LocalDate ld) {
        return LocalDateTime.of(ld, LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate toLocalDate(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String getMonthLabel(int year, int month) {
        return Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year;
    }
}
