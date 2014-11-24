package com.jscriptive.moneyfx.util;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;

import java.math.BigDecimal;
import java.util.Locale;

import static java.util.Locale.GERMANY;

/**
 * Created by jscriptive.com on 23/11/14.
 */
public class CurrencyFormat {

    private static final Locale LOCALE = GERMANY;

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
        return validator.format(value, LOCALE);
    }

    public String format(long value) {
        return validator.format(value, LOCALE);
    }

    public double parse(String value) {
        BigDecimal parsed = validator.validate(value, LOCALE);
        if (parsed == null) {
            parsed = NumberUtils.createBigDecimal(value);
        }
        return parsed.doubleValue();
    }

    public boolean isValid(String value) {
        return validator.isValid(value, LOCALE) || NumberUtils.isNumber(value);
    }

}
