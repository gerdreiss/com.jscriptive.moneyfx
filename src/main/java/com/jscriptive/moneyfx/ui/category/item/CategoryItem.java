package com.jscriptive.moneyfx.ui.category.item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by jscriptive.com on 18/11/2014.
 */
public class CategoryItem {

    private final StringProperty name;
    private final StringProperty amount;
    private final StringProperty rule;

    public CategoryItem(String name, String amount, String rule) {
        this.name = new SimpleStringProperty(name);
        this.amount = new SimpleStringProperty(amount);
        this.rule = new SimpleStringProperty(rule);
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

    public String getAmount() {
        return amount.get();
    }

    public StringProperty amountProperty() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public String getRule() {
        return rule.get();
    }

    public StringProperty ruleProperty() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule.set(rule);
    }
}
