package com.jscriptive.moneyfx.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static java.time.LocalTime.MIDNIGHT;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.ENGLISH;

/**
 * Created by jscriptive.com on 19/11/14.
 */
public class LocalDateUtils {

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final DateTimeFormatter DATE_FORMATTER = ofPattern(DATE_FORMAT);

    public static long toMillis(LocalDate ld) {
        return LocalDateTime.of(ld, MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate toLocalDate(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String getMonthLabel(int year, int month) {
        return Month.of(month).getDisplayName(SHORT, ENGLISH) + " " + year;
    }

}
