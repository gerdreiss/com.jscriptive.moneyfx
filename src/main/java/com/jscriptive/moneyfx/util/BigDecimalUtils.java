package com.jscriptive.moneyfx.util;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.MathContext.DECIMAL32;
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

        return b1.round(DECIMAL32).equals(b2.round(DECIMAL32));
    }
}
