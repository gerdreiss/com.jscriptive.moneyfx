package com.jscriptive.moneyfx.ui.common;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;

/**
 * Created by jscriptive.com on 19/11/14.
 */
public class PercentageWidthTableColumn<S, T> extends javafx.scene.control.TableColumn<S, T> {

    private final DoubleProperty percentageWidth = new SimpleDoubleProperty(1);

    public PercentageWidthTableColumn() {
        tableViewProperty().addListener(new ChangeListener<TableView<S>>() {

            @Override
            public void changed(ObservableValue<? extends TableView<S>> ov, TableView<S> t, TableView<S> t1) {
                if (PercentageWidthTableColumn.this.prefWidthProperty().isBound()) {
                    PercentageWidthTableColumn.this.prefWidthProperty().unbind();
                }

                PercentageWidthTableColumn.this.prefWidthProperty().bind(t1.widthProperty().multiply(percentageWidth));
            }
        });
    }

    public final DoubleProperty percentageWidthProperty() {
        return this.percentageWidth;
    }

    public final double getPercentageWidth() {
        return this.percentageWidthProperty().get();
    }

    public final void setPercentageWidth(double value) throws IllegalArgumentException {
        if (value >= 0 && value <= 1) {
            this.percentageWidthProperty().set(value);
        } else {
            throw new IllegalArgumentException(String.format("The provided percentage width is not between 0.0 and 1.0. Value is: %1$s", value));
        }
    }
}