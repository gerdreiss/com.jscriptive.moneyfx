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

    private static class SingletonHolder {
        public static final CurrencyFormat instance = new CurrencyFormat();
    }

    public static CurrencyFormat getInstance() {
        return SingletonHolder.instance;
    }

    private final BigDecimalValidator validator;

    private CurrencyFormat() {
        validator = CurrencyValidator.getInstance();
    }

    public String format(BigDecimal value) {
        return validator.format(value, LOCALE);
    }

    public String format(double value) {
        return validator.format(value, LOCALE);
    }

    public String format(long value) {
        return validator.format(value, LOCALE);
    }

    public BigDecimal parse(String value) {
        BigDecimal parsed = validator.validate(value, LOCALE);
        if (parsed == null) {
            parsed = NumberUtils.createBigDecimal(value);
        }
        return parsed;
    }

    public boolean isValid(String value) {
        return validator.isValid(value, LOCALE) || NumberUtils.isNumber(value);
    }

}
