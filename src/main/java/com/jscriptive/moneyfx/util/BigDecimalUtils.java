package com.jscriptive.moneyfx.util;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

/**
 * Created by jscriptive.com on 27/11/2014.
 */
public class BigDecimalUtils {

    public static boolean isEqual(BigDecimal b1, BigDecimal b2) {
        if (b1 == null && b2 == null) {
            return true;
        }
        if (b1 == null || b2 == null) {
            return false;
        }

        return b1.setScale(2, HALF_UP).equals(b2.setScale(2, HALF_UP));
    }
}
