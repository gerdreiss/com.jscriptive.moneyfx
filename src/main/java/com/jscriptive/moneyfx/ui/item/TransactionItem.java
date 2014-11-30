package com.jscriptive.moneyfx.ui.item;

import com.jscriptive.moneyfx.model.Transaction;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by jscriptive.com on 13/11/2014.
 */
public class TransactionItem implements UIItem {

    private final StringProperty account;
    private final StringProperty category;
    private final StringProperty concept;
    private final ObjectProperty<LocalDate> dtOp;
    private final ObjectProperty<LocalDate> dtVal;
    private final ObjectProperty<BigDecimal> amount;

    public TransactionItem(Transaction trx) {
        this(trx.getAccount().toPresentableString(), trx.getCategory().getName(), trx.getConcept(), trx.getDtOp(), trx.getDtVal(), trx.getAmount());
    }

    public TransactionItem(String account, String category, String concept, LocalDate dtOp, LocalDate dtVal, BigDecimal amount) {
        this.account = new SimpleStringProperty(account);
        this.category = new SimpleStringProperty(category);
        this.concept = new SimpleStringProperty(concept);
        this.dtOp = new SimpleObjectProperty<>(dtOp);
        this.dtVal = new SimpleObjectProperty<>(dtVal);
        this.amount = new SimpleObjectProperty<>(amount);
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

    public LocalDate getDtOp() {
        return dtOp.get();
    }

    public ObjectProperty<LocalDate> dtOpProperty() {
        return dtOp;
    }

    public void setDtOp(LocalDate dtOp) {
        this.dtOp.set(dtOp);
    }

    public LocalDate getDtVal() {
        return dtVal.get();
    }

    public ObjectProperty<LocalDate> dtValProperty() {
        return dtVal;
    }

    public void setDtVal(LocalDate dtVal) {
        this.dtVal.set(dtVal);
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
