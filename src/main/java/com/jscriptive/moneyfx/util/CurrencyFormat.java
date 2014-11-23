package com.jscriptive.moneyfx.util;

import java.text.NumberFormat;

import static java.util.Locale.GERMANY;

/**
 * Created by igorreiss on 23/11/14.
 */
public class CurrencyFormat {
    private static CurrencyFormat instance;

    public static CurrencyFormat getInstance() {
        if (instance == null) {
            instance = new CurrencyFormat();
        }
        return instance;
    }

    private final NumberFormat formatter;

    private CurrencyFormat() {
        formatter = NumberFormat.getCurrencyInstance(GERMANY);
    }

    public String format(double number) {
        return formatter.format(number);
    }

    public String format(long number) {
        return formatter.format(number);
    }

}
