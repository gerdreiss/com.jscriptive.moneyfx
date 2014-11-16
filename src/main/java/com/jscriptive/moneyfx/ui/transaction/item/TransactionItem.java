package com.jscriptive.moneyfx.ui.transaction.item;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by jscriptive.com on 13/11/2014.
 */
public class TransactionItem {

    private StringProperty account;
    private StringProperty category;
    private StringProperty concept;
    private StringProperty dtOp;
    private StringProperty dtVal;
    private DoubleProperty amount;

    public TransactionItem(String account, String category, String concept, String dtOp, String dtVal, double amount) {
        this.account = new SimpleStringProperty(account);
        this.category = new SimpleStringProperty(category);
        this.concept = new SimpleStringProperty(concept);
        this.dtOp = new SimpleStringProperty(dtOp);
        this.dtVal = new SimpleStringProperty(dtVal);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public String getAccount() {
        return account.get();
    }

    public StringProperty accountProperty() {
        return account;
    }

    public void setAccount(String account) {
        this.account.set(account);
    }

    public String getConcept() {
        return concept.get();
    }

    public StringProperty conceptProperty() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept.set(concept);
    }

    public String getDtOp() {
        return dtOp.get();
    }

    public StringProperty dtOpProperty() {
        return dtOp;
    }

    public void setDtOp(String dtOp) {
        this.dtOp.set(dtOp);
    }

    public String getDtVal() {
        return dtVal.get();
    }

    public StringProperty dtValProperty() {
        return dtVal;
    }

    public void setDtVal(String dtVal) {
        this.dtVal.set(dtVal);
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

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public void setCategory(String category) {
        this.category.set(category);
    }
}
