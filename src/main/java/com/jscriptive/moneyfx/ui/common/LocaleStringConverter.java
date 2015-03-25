package com.jscriptive.moneyfx.ui.common;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import static java.util.Locale.getAvailableLocales;

/**
 * Created by jscriptive.com on 28/11/14.
 */
public class LocaleStringConverter extends javafx.util.StringConverter<java.util.Locale> {

    @Override
    public String toString(Locale object) {
        return object.getCountry();
    }

    @Override
    public Locale fromString(String string) {
        Optional<Locale> result = Arrays.asList(getAvailableLocales()).parallelStream().filter(l -> l.getCountry().equals(string)).findFirst();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }
}
