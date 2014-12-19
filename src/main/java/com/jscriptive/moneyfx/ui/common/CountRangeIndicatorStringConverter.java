package com.jscriptive.moneyfx.ui.common;

import com.jscriptive.moneyfx.model.CountRange;
import javafx.util.StringConverter;

/**
 * Created by jscriptive.com on 19/12/14.
 */
public class CountRangeIndicatorStringConverter extends StringConverter<CountRange.CountRangeIndicator> {

    @Override
    public String toString(CountRange.CountRangeIndicator object) {
        return object.toString();
    }

    @Override
    public CountRange.CountRangeIndicator fromString(String string) {
        return CountRange.CountRangeIndicator.fromString(string);
    }
}
