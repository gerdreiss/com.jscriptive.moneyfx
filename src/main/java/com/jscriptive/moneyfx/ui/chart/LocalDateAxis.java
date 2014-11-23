/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013, Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jscriptive.moneyfx.ui.chart;

import com.sun.javafx.charts.ChartLayoutAnimator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.chart.Axis;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.jscriptive.moneyfx.util.LocalDateUtil.toLocalDate;
import static com.jscriptive.moneyfx.util.LocalDateUtil.toMillis;

/**
 * An axis that displays date and time values.
 * <p/>
 * Tick labels are usually automatically set and calculated depending on the range unless you explicitly {@linkplain #setTickLabelFormatter(javafx.util.StringConverter) set an formatter}.
 * <p/>
 * You also have the chance to specify fix lower and upper bounds, otherwise they are calculated by your data.
 * <p/>
 * <p/>
 * <h3>Screenshots</h3>
 * <p>
 * Displaying date values, ranging over several months:</p>
 * <img src="doc-files/DateAxisMonths.png" alt="DateAxisMonths" />
 * <p>
 * Displaying date values, ranging only over a few hours:</p>
 * <img src="doc-files/DateAxisHours.png" alt="DateAxisHours" />
 * <p>
 * <p>
 * <h3>Sample Usage</h3>
 * <pre>
 * {@code
 * ObservableList<XYChart.Series<LocalDate, Number>> series = FXCollections.observableArrayList();
 *
 * ObservableList<XYChart.Data<LocalDate, Number>> series1Data = FXCollections.observableArrayList();
 * series1Data.add(new XYChart.Data<LocalDate, Number>(new GregorianCalendar(2012, 11, 15).getTime(), 2));
 * series1Data.add(new XYChart.Data<LocalDate, Number>(new GregorianCalendar(2014, 5, 3).getTime(), 4));
 *
 * ObservableList<XYChart.Data<LocalDate, Number>> series2Data = FXCollections.observableArrayList();
 * series2Data.add(new XYChart.Data<LocalDate, Number>(new GregorianCalendar(2014, 0, 13).getTime(), 8));
 * series2Data.add(new XYChart.Data<LocalDate, Number>(new GregorianCalendar(2014, 7, 27).getTime(), 4));
 *
 * series.add(new XYChart.Series<>("Series1", series1Data));
 * series.add(new XYChart.Series<>("Series2", series2Data));
 *
 * NumberAxis numberAxis = new NumberAxis();
 * LocalDateAxis dateAxis = new LocalDateAxis();
 * LineChart<LocalDate, Number> lineChart = new LineChart<>(dateAxis, numberAxis, series);
 * }
 * </pre>
 *
 * @author Christian Schudt
 * @author Diego Cirujano
 * @author jscriptive.com -> changed to LocalDate Axos
 */
public final class LocalDateAxis extends Axis<LocalDate> {

    /**
     * These property are used for animation.
     */
    private final LongProperty currentLowerBound = new SimpleLongProperty(this, "currentLowerBound");

    private final LongProperty currentUpperBound = new SimpleLongProperty(this, "currentUpperBound");

    private final ObjectProperty<StringConverter<LocalDate>> tickLabelFormatter = new ObjectPropertyBase<StringConverter<LocalDate>>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return LocalDateAxis.this;
        }

        @Override
        public String getName() {
            return "tickLabelFormatter";
        }
    };

    /**
     * Stores the min and max date of the list of dates which is used.
     * If {@link #autoRanging} is true, these values are used as lower and upper bounds.
     */
    private LocalDate minDate, maxDate;

    private final ObjectProperty<LocalDate> lowerBound = new ObjectPropertyBase<LocalDate>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return LocalDateAxis.this;
        }

        @Override
        public String getName() {
            return "lowerBound";
        }
    };

    private final ObjectProperty<LocalDate> upperBound = new ObjectPropertyBase<LocalDate>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return LocalDateAxis.this;
        }

        @Override
        public String getName() {
            return "upperBound";
        }
    };

    private final ChartLayoutAnimator animator = new ChartLayoutAnimator(this);

    private Object currentAnimationID;

    private Interval actualInterval = Interval.DECADE;

    /**
     * Default constructor. By default the lower and upper bound are calculated by the data.
     */
    public LocalDateAxis() {
    }

    /**
     * Constructs a date axis with fix lower and upper bounds.
     *
     * @param lowerBound The lower bound.
     * @param upperBound The upper bound.
     */
    public LocalDateAxis(LocalDate lowerBound, LocalDate upperBound) {
        this();
        setAutoRanging(false);
        setLowerBound(lowerBound);
        setUpperBound(upperBound);
    }

    /**
     * Constructs a date axis with a label and fix lower and upper bounds.
     *
     * @param axisLabel  The label for the axis.
     * @param lowerBound The lower bound.
     * @param upperBound The upper bound.
     */
    public LocalDateAxis(String axisLabel, LocalDate lowerBound, LocalDate upperBound) {
        this(lowerBound, upperBound);
        setLabel(axisLabel);
    }

    @Override
    public void invalidateRange(List<LocalDate> list) {
        super.invalidateRange(list);

        Collections.sort(list);
        if (list.isEmpty()) {
            minDate = maxDate = LocalDate.now();
        } else if (list.size() == 1) {
            minDate = maxDate = list.get(0);
        } else if (list.size() > 1) {
            minDate = list.get(0);
            maxDate = list.get(list.size() - 1);
        }
    }

    @Override
    protected Object autoRange(double length) {
        if (isAutoRanging()) {
            return new Object[]{minDate, maxDate};
        } else {
            if (getLowerBound() == null || getUpperBound() == null) {
                throw new IllegalArgumentException("If autoRanging is false, a lower and upper bound must be set.");
            }
            return getRange();
        }
    }

    @Override
    protected void setRange(Object range, boolean animating) {
        Object[] r = (Object[]) range;
        LocalDate oldLowerBound = getLowerBound();
        LocalDate oldUpperBound = getUpperBound();
        LocalDate lower = (LocalDate) r[0];
        LocalDate upper = (LocalDate) r[1];
        setLowerBound(lower);
        setUpperBound(upper);

        if (animating) {
//            final Timeline timeline = new Timeline();
//            timeline.setAutoReverse(false);
//            timeline.setCycleCount(1);
//            final AnimationTimer timer = new AnimationTimer() {
//                @Override
//                public void handle(long l) {
//                    requestAxisLayout();
//                }
//            };
//            timer.start();
//
//            timeline.setOnFinished(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent actionEvent) {
//                    timer.stop();
//                    requestAxisLayout();
//                }
//            });
//
//            KeyValue keyValue = new KeyValue(currentLowerBound, lower.getTime());
//            KeyValue keyValue2 = new KeyValue(currentUpperBound, upper.getTime());
//
//            timeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO,
//                    new KeyValue(currentLowerBound, oldLowerBound.getTime()),
//                    new KeyValue(currentUpperBound, oldUpperBound.getTime())),
//                    new KeyFrame(Duration.millis(3000), keyValue, keyValue2));
//            timeline.play();


            animator.stop(currentAnimationID);
            currentAnimationID = animator.animate(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(currentLowerBound, toMillis(oldLowerBound)),
                            new KeyValue(currentUpperBound, toMillis(oldUpperBound))
                    ),
                    new KeyFrame(Duration.millis(700),
                            new KeyValue(currentLowerBound, toMillis(lower)),
                            new KeyValue(currentUpperBound, toMillis(upper))
                    )
            );

        } else {
            currentLowerBound.set(toMillis(getLowerBound()));
            currentUpperBound.set(toMillis(getUpperBound()));
        }
    }

    @Override
    protected Object getRange() {
        return new Object[]{getLowerBound(), getUpperBound()};
    }

    @Override
    public double getZeroPosition() {
        return 0;
    }

    @Override
    public double getDisplayPosition(LocalDate date) {
        final double length = getSide().isHorizontal() ? getWidth() : getHeight();

        // Get the difference between the max and min date.
        double diff = currentUpperBound.get() - currentLowerBound.get();

        // Get the actual range of the visible area.
        // The minimal date should start at the zero position, that's why we subtract it.
        double range = length - getZeroPosition();

        // Then get the difference from the actual date to the min date and divide it by the total difference.
        // We get a value between 0 and 1, if the date is within the min and max date.
        double d = (toMillis(date) - currentLowerBound.get()) / diff;

        // Multiply this percent value with the range and add the zero offset.
        if (getSide().isVertical()) {
            return getHeight() - d * range + getZeroPosition();
        } else {
            return d * range + getZeroPosition();
        }
    }

    @Override
    public LocalDate getValueForDisplay(double displayPosition) {
        final double length = getSide().isHorizontal() ? getWidth() : getHeight();

        // Get the difference between the max and min date.
        double diff = currentUpperBound.get() - currentLowerBound.get();

        // Get the actual range of the visible area.
        // The minimal date should start at the zero position, that's why we subtract it.
        double range = length - getZeroPosition();

        if (getSide().isVertical()) {
            // displayPosition = getHeight() - ((date - lowerBound) / diff) * range + getZero
            // date = displayPosition - getZero - getHeight())/range * diff + lowerBound
            return toLocalDate((long) ((displayPosition - getZeroPosition() - getHeight()) / -range * diff + currentLowerBound.get()));
        } else {
            // displayPosition = ((date - lowerBound) / diff) * range + getZero
            // date = displayPosition - getZero)/range * diff + lowerBound
            return toLocalDate((long) ((displayPosition - getZeroPosition()) / range * diff + currentLowerBound.get()));
        }
    }

    @Override
    public boolean isValueOnAxis(LocalDate date) {
        return toMillis(date) > currentLowerBound.get() && toMillis(date) < currentUpperBound.get();
    }

    @Override
    public double toNumericValue(LocalDate date) {
        return toMillis(date);
    }

    @Override
    public LocalDate toRealValue(double v) {
        return toLocalDate((long) v);
    }

    @Override
    protected List<LocalDate> calculateTickValues(double v, Object range) {
        Object[] r = (Object[]) range;
        LocalDate lower = (LocalDate) r[0];
        LocalDate upper = (LocalDate) r[1];

        List<LocalDate> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // The preferred gap which should be between two tick marks.
        double averageTickGap = 100;
        double averageTicks = v / averageTickGap;

        List<LocalDate> previousDateList = new ArrayList<>();

        Interval previousInterval = Interval.values()[0];

        // Starting with the greatest interval, add one of each calendar unit.
        for (Interval interval : Interval.values()) {
            // Reset the calendar.
            calendar.setTimeInMillis(toMillis(lower));
            // Clear the list.
            dateList.clear();
            previousDateList.clear();
            actualInterval = interval;

            // Loop as long we exceeded the upper bound.
            while (calendar.getTime().getTime() <= toMillis(upper)) {
                dateList.add(toLocalDate(calendar.getTimeInMillis()));
                calendar.add(interval.interval, interval.amount);
            }
            // Then check the size of the list. If it is greater than the amount of ticks, take that list.
            if (dateList.size() > averageTicks) {
                calendar.setTimeInMillis(toMillis(lower));
                // Recheck if the previous interval is better suited.
                while (calendar.getTime().getTime() <= toMillis(upper)) {
                    previousDateList.add(toLocalDate(calendar.getTimeInMillis()));
                    calendar.add(previousInterval.interval, previousInterval.amount);
                }
                break;
            }

            previousInterval = interval;
        }
        if (previousDateList.size() - averageTicks > averageTicks - dateList.size()) {
            dateList = previousDateList;
            actualInterval = previousInterval;
        }

        // At last add the upper bound.
        dateList.add(upper);

        List<LocalDate> evenDateList = makeDatesEven(dateList, calendar);
        // If there are at least three dates, check if the gap between the lower date and the second date is at least half the gap of the second and third date.
        // Do the same for the upper bound.
        // If gaps between dates are to small, remove one of them.
        // This can occur, e.g. if the lower bound is 25.12.2013 and years are shown. Then the next year shown would be 2014 (01.01.2014) which would be too narrow to 25.12.2013.
        if (evenDateList.size() > 2) {

            LocalDate secondDate = evenDateList.get(1);
            LocalDate thirdDate = evenDateList.get(2);
            LocalDate lastDate = evenDateList.get(dateList.size() - 2);
            LocalDate previousLastDate = evenDateList.get(dateList.size() - 3);

            // If the second date is too near by the lower bound, remove it.
            if (toMillis(secondDate) - toMillis(lower) < (toMillis(thirdDate) - toMillis(secondDate)) / 2) {
                evenDateList.remove(secondDate);
            }

            // If difference from the upper bound to the last date is less than the half of the difference of the previous two dates,
            // we better remove the last date, as it comes to close to the upper bound.
            if (toMillis(upper) - toMillis(lastDate) < (toMillis(lastDate) - toMillis(previousLastDate)) / 2) {
                evenDateList.remove(lastDate);
            }
        }

        return evenDateList;
    }

    @Override
    protected void layoutChildren() {
        if (!isAutoRanging()) {
            currentLowerBound.set(toMillis(getLowerBound()));
            currentUpperBound.set(toMillis(getUpperBound()));
        }
        super.layoutChildren();
    }

    @Override
    protected String getTickMarkLabel(LocalDate date) {

        StringConverter<LocalDate> converter = getTickLabelFormatter();
        if (converter != null) {
            return converter.toString(date);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(toMillis(date));

        DateTimeFormatter formatter;

        if (actualInterval.interval == Calendar.YEAR && calendar.get(Calendar.MONTH) == 0 && calendar.get(Calendar.DATE) == 1) {
            formatter = DateTimeFormatter.ofPattern("yyyy");
        } else if (actualInterval.interval == Calendar.MONTH && calendar.get(Calendar.DATE) == 1) {
            formatter = DateTimeFormatter.ofPattern("MMM yy");
        } else {
            formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        }
        return date.format(formatter);
    }

    /**
     * Makes dates even, in the sense of that years always begin in January, months always begin on the 1st and days always at midnight.
     *
     * @param dates The list of dates.
     * @return The new list of dates.
     */
    private List<LocalDate> makeDatesEven(List<LocalDate> dates, Calendar calendar) {
        // If the dates contain more dates than just the lower and upper bounds, make the dates in between even.
        if (dates.size() > 2) {
            List<LocalDate> evenDates = new ArrayList<>();

            // For each interval, modify the date slightly by a few millis, to make sure they are different days.
            // This is because Axis stores each value and won't update the tick labels, if the value is already known.
            // This happens if you display days and then add a date many years in the future the tick label will still be displayed as day.
            for (int i = 0; i < dates.size(); i++) {
                calendar.setTimeInMillis(toMillis(dates.get(i)));
                switch (actualInterval.interval) {
                    case Calendar.YEAR:
                        // If its not the first or last date (lower and upper bound), make the year begin with first month and let the months begin with first day.
                        if (i != 0 && i != dates.size() - 1) {
                            calendar.set(Calendar.MONTH, 0);
                            calendar.set(Calendar.DATE, 1);
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 6);
                        break;
                    case Calendar.MONTH:
                        // If its not the first or last date (lower and upper bound), make the months begin with first day.
                        if (i != 0 && i != dates.size() - 1) {
                            calendar.set(Calendar.DATE, 1);
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 5);
                        break;
                    case Calendar.WEEK_OF_YEAR:
                        // Make weeks begin with first day of week?
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 4);
                        break;
                    case Calendar.DATE:
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 3);
                        break;
                    case Calendar.HOUR:
                        if (i != 0 && i != dates.size() - 1) {
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                        }
                        calendar.set(Calendar.MILLISECOND, 2);
                        break;
                    case Calendar.MINUTE:
                        if (i != 0 && i != dates.size() - 1) {
                            calendar.set(Calendar.SECOND, 0);
                        }
                        calendar.set(Calendar.MILLISECOND, 1);
                        break;
                    case Calendar.SECOND:
                        calendar.set(Calendar.MILLISECOND, 0);
                        break;

                }
                evenDates.add(toLocalDate(calendar.getTimeInMillis()));
            }

            return evenDates;
        } else {
            return dates;
        }
    }

    /**
     * Gets the lower bound of the axis.
     *
     * @return The property.
     * @see #getLowerBound()
     * @see #setLowerBound(java.time.LocalDate)
     */
    public final ObjectProperty<LocalDate> lowerBoundProperty() {
        return lowerBound;
    }

    /**
     * Gets the lower bound of the axis.
     *
     * @return The lower bound.
     * @see #lowerBoundProperty()
     */
    public final LocalDate getLowerBound() {
        return lowerBound.get();
    }

    /**
     * Sets the lower bound of the axis.
     *
     * @param date The lower bound date.
     * @see #lowerBoundProperty()
     */
    public final void setLowerBound(LocalDate date) {
        lowerBound.set(date);
    }

    /**
     * Gets the upper bound of the axis.
     *
     * @return The property.
     * @see #getUpperBound() ()
     * @see #setUpperBound(java.time.LocalDate)
     */
    public final ObjectProperty<LocalDate> upperBoundProperty() {
        return upperBound;
    }

    /**
     * Gets the upper bound of the axis.
     *
     * @return The upper bound.
     * @see #upperBoundProperty()
     */
    public final LocalDate getUpperBound() {
        return upperBound.get();
    }

    /**
     * Sets the upper bound of the axis.
     *
     * @param date The upper bound date.
     * @see #upperBoundProperty() ()
     */
    public final void setUpperBound(LocalDate date) {
        upperBound.set(date);
    }

    /**
     * Gets the tick label formatter for the ticks.
     *
     * @return The converter.
     */
    public final StringConverter<LocalDate> getTickLabelFormatter() {
        return tickLabelFormatter.getValue();
    }

    /**
     * Sets the tick label formatter for the ticks.
     *
     * @param value The converter.
     */
    public final void setTickLabelFormatter(StringConverter<LocalDate> value) {
        tickLabelFormatter.setValue(value);
    }

    /**
     * Gets the tick label formatter for the ticks.
     *
     * @return The property.
     */
    public final ObjectProperty<StringConverter<LocalDate>> tickLabelFormatterProperty() {
        return tickLabelFormatter;
    }

    /**
     * The intervals, which are used for the tick labels. Beginning with the largest interval, the axis tries to calculate the tick values for this interval.
     * If a smaller interval is better suited for, that one is taken.
     */
    private enum Interval {
        DECADE(Calendar.YEAR, 10),
        YEAR(Calendar.YEAR, 1),
        MONTH_6(Calendar.MONTH, 6),
        MONTH_3(Calendar.MONTH, 3),
        MONTH_1(Calendar.MONTH, 1),
        WEEK(Calendar.WEEK_OF_YEAR, 1),
        DAY(Calendar.DATE, 1),
        HOUR_12(Calendar.HOUR, 12),
        HOUR_6(Calendar.HOUR, 6),
        HOUR_3(Calendar.HOUR, 3),
        HOUR_1(Calendar.HOUR, 1),
        MINUTE_15(Calendar.MINUTE, 15),
        MINUTE_5(Calendar.MINUTE, 5),
        MINUTE_1(Calendar.MINUTE, 1),
        SECOND_15(Calendar.SECOND, 15),
        SECOND_5(Calendar.SECOND, 5),
        SECOND_1(Calendar.SECOND, 1),
        MILLISECOND(Calendar.MILLISECOND, 1);

        private final int amount;

        private final int interval;

        private Interval(int interval, int amount) {
            this.interval = interval;
            this.amount = amount;
        }
    }

}