package com.jscriptive.moneyfx.util;

import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;

import static java.util.Locale.GERMANY;

/**
 * Created by jscriptive.com on 23/11/14.
 */
public class CurrencyFormat {
    private static CurrencyFormat instance;

    public static CurrencyFormat getInstance() {
        if (instance == null) {
            instance = new CurrencyFormat();
        }
        return instance;
    }

    private final BigDecimalValidator validator;

    private CurrencyFormat() {
        validator = CurrencyValidator.getInstance();
    }

    public String format(double value) {
        return validator.format(value, GERMANY);
    }

    public String format(long value) {
        return validator.format(value, GERMANY);
    }

    public double parse(String value) {
        return validator.validate(value, GERMANY).doubleValue();
    }

    public boolean isValid(String value) {
        return validator.isValid(value, GERMANY);
    }

}
