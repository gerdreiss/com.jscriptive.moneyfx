package com.jscriptive.moneyfx.model;

import static com.jscriptive.moneyfx.model.CountRange.CountRangeIndicator.ALL;
import static com.jscriptive.moneyfx.model.CountRange.CountRangeIndicator.LAST;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.String.format;

/**
 * Created by jscriptive.com on 19/12/14.
 */
public class CountRange {

    public static CountRange DEFAULT_RANGE = new CountRange(LAST, 30);
    public static CountRange ALL_ELEMENTS = new CountRange(ALL);

    public static enum CountRangeIndicator {
        ALL("All elements"), FIRST("First"), LAST("Last");

        private final String label;

        private CountRangeIndicator(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }

        public static CountRangeIndicator fromString(String label) {
            for (CountRangeIndicator indicator : CountRangeIndicator.values()) {
                if (indicator.toString().equals(label)) {
                    return indicator;
                }
            }
            throw new IllegalArgumentException(format("'%s' is unknown count range indicator", label));
        }
    }

    private final CountRangeIndicator indicator;
    private final int count;

    public CountRange(CountRangeIndicator indicator) {
        this.indicator = indicator;
        this.count = MIN_VALUE;
    }

    public CountRange(CountRangeIndicator indicator, int count) {
        this.indicator = indicator;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public CountRangeIndicator getIndicator() {
        return indicator;
    }

    @Override
    public String toString() {
        return format("CountRange {indicator = %s, count = %d}", indicator, count);
    }
}
