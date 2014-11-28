package com.jscriptive.moneyfx.ui.common;

import javafx.util.StringConverter;

import java.time.LocalDate;

import static com.jscriptive.moneyfx.util.LocalDateUtils.DATE_FORMATTER;
import static java.time.LocalDate.parse;

/**
 * Created by jscriptive.com on 28/11/14.
 */
public class LocalDateStringConverter extends StringConverter<LocalDate> {

    @Override
    public String toString(LocalDate date) {
        if (date != null) {
            return DATE_FORMATTER.format(date);
        } else {
            return "";
        }
    }

    @Override
    public LocalDate fromString(String string) {
        if (string != null && !string.isEmpty()) {
            return parse(string, DATE_FORMATTER);
        } else {
            return null;
        }
    }
}
