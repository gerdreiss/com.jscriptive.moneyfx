package com.jscriptive.moneyfx.ui.common;

import javafx.util.StringConverter;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

/**
 * Created by jscriptive.com on 07/12/14.
 */
public class YearStringConverter extends StringConverter<Integer> {

    @Override
    public String toString(Integer object) {
        return INTEGER_ZERO.equals(object) ? "All years" : object.toString();
    }

    @Override
    public Integer fromString(String string) {
        return "All years".equals(string) ? INTEGER_ZERO : parseInt(string);
    }
}
