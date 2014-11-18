package com.jscriptive.moneyfx.ui.category.item;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Igor on 18/11/2014.
 */
public class CategoryItem {

    private final StringProperty name;
    private final DoubleProperty amount;

    public CategoryItem() {
        this("", 0.0);
    }

    public CategoryItem(String name, double amount) {
        this.name = new SimpleStringProperty(name);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getAmount() {
        return amount.get();
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }
}
