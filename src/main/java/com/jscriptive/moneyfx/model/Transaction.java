package com.jscriptive.moneyfx.model;

import com.jscriptive.moneyfx.util.CurrencyFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.jscriptive.moneyfx.util.BigDecimalUtils.isEqual;
import static java.lang.String.format;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
@Document
public class Transaction {

    @Id
    private String id;
    @DBRef
    private Account account;
    @DBRef
    private Category category;
    @Indexed
    private String concept;

    private LocalDate dtOp;
    private LocalDate dtVal;

    private BigDecimal amount;

    private Boolean isTransfer;

    @Transient
    private BigDecimal accountBalance;

    public Transaction() {
    }

    public Transaction(Account account, Category category, String concept, LocalDate dtOp, LocalDate dtVal, BigDecimal amount) {
        setAccount(account);
        setCategory(category);
        setConcept(concept);
        setDtOp(dtOp);
        setDtVal(dtVal);
        setAmount(amount);
    }

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
        if (this.account != null) {
            setIsTransfer(this.account.getBank().isTransfer(this));
        }
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
        return CurrencyFormat.getInstance().format(getAmount().doubleValue());
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

    public Boolean getIsTransfer() {
        return isTransfer;
    }

    public void setIsTransfer(Boolean isTransfer) {
        this.isTransfer = isTransfer;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;

        Transaction that = (Transaction) o;

        if (StringUtils.equals(this.getId(), that.getId())) return true;
        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (concept != null ? !concept.equals(that.concept) : that.concept != null) return false;
        if (dtOp != null ? !dtOp.equals(that.dtOp) : that.dtOp != null) return false;
        if (dtVal != null ? !dtVal.equals(that.dtVal) : that.dtVal != null) return false;
        return isEqual(this.amount, that.amount);
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
        return format("Transaction{account=%s, concept='%s', dtOp=%s, dtVal=%s, amount=%s}", account, concept, dtOp, dtVal, getFormattedAmount());
    }
}
