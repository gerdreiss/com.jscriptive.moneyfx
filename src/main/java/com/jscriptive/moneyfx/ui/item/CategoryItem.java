package com.jscriptive.moneyfx.ui.item;

import com.jscriptive.moneyfx.model.Category;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;

/**
 * Created by jscriptive.com on 18/11/2014.
 */
public class CategoryItem implements UIItem {

    private final StringProperty name;
    private final ObjectProperty<BigDecimal> amount;
    private final StringProperty rule;

    public CategoryItem(Category category) {
        this(category, null);
    }

    public CategoryItem(Category category, BigDecimal amount) {
        this(category.getName(), amount, category.getFilterRule() == null ? null : category.getFilterRule().toPresentableString());
    }

    public CategoryItem(String name, BigDecimal amount, String rule) {
        this.name = new SimpleStringProperty(name);
        this.amount = new SimpleObjectProperty(amount);
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

    public BigDecimal getAmount() {
        return amount.get();
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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
