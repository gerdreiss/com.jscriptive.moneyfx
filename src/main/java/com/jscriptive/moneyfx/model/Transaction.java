package com.jscriptive.moneyfx.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
@Document
public class Transaction {

    private static String REPO_NAME = "transactions";

    @Id
    private Long id;
    private Account account;
    private String concept;
    private LocalDate dtOp;
    private LocalDate dtVal;
    private BigDecimal amount;
    private Category category;

    public Transaction() {
    }

    public Transaction(Account account, String concept, LocalDate dtOp, LocalDate dtVal, BigDecimal amount) {
        this(account, Category.DEFAULT, concept, dtOp, dtVal, amount);
    }

    public Transaction(Account account, Category category, String concept, LocalDate dtOp, LocalDate dtVal, BigDecimal amount) {
        this();
        setAccount(account);
        setCategory(category);
        setConcept(concept);
        setDtOp(dtOp);
        setDtVal(dtVal);
        setAmount(amount);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        if (!super.equals(o)) return false;

        Transaction that = (Transaction) o;

        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (concept != null ? !concept.equals(that.concept) : that.concept != null) return false;
        if (dtOp != null ? !dtOp.equals(that.dtOp) : that.dtOp != null) return false;
        if (dtVal != null ? !dtVal.equals(that.dtVal) : that.dtVal != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (concept != null ? concept.hashCode() : 0);
        result = 31 * result + (dtOp != null ? dtOp.hashCode() : 0);
        result = 31 * result + (dtVal != null ? dtVal.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Transaction{account=%s, category='%s', concept='%s', dtOp=%s, dtVal=%s, amount=%s}", account, category, concept, dtOp, dtVal, amount);
    }
}
