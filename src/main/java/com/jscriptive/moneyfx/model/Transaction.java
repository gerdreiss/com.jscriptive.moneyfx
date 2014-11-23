package com.jscriptive.moneyfx.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
@Document
public class Transaction {

    private static final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);

    @Id
    private String id;

    private Account account;

    private Category category;

    @Indexed
    private String concept;

    private LocalDate dtOp;

    private LocalDate dtVal;

    private BigDecimal amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public LocalDate getDtOp() {
        return dtOp;
    }

    public void setDtOp(LocalDate dtOp) {
        this.dtOp = dtOp;
    }

    public LocalDate getDtVal() {
        return dtVal;
    }

    public void setDtVal(LocalDate dtVal) {
        this.dtVal = dtVal;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getFormattedAmount() {
        return formatter.format(getAmount().doubleValue());
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;

        Transaction that = (Transaction) o;

        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (concept != null ? !concept.equals(that.concept) : that.concept != null) return false;
        if (dtOp != null ? !dtOp.equals(that.dtOp) : that.dtOp != null) return false;
        if (dtVal != null ? !dtVal.equals(that.dtVal) : that.dtVal != null) return false;
        //noinspection RedundantIfStatement
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = account != null ? account.hashCode() : 0;
        result = 31 * result + (concept != null ? concept.hashCode() : 0);
        result = 31 * result + (dtOp != null ? dtOp.hashCode() : 0);
        result = 31 * result + (dtVal != null ? dtVal.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Transaction{account=%s, concept='%s', dtOp=%s, dtVal=%s, amount=%s}", account, concept, dtOp, dtVal, getFormattedAmount());
    }
}
