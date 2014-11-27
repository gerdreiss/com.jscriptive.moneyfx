package com.jscriptive.moneyfx.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by jscriptive.com on 20/11/2014.
 */
@Document
public class TransactionFilter {

    @Id
    private String id;
    @DBRef
    private Account account;
    @DBRef
    private Category category;
    @Indexed
    private String concept;

    private ValueRange<LocalDate> dtOpRange;
    private ValueRange<LocalDate> dtValRange;
    private ValueRange<BigDecimal> amountRange;

    public TransactionFilter() {
    }

    public TransactionFilter(Account account, Category category, String concept, ValueRange<LocalDate> dtOpRange, ValueRange<LocalDate> dtValRange, ValueRange<BigDecimal> amountRange) {
        this.setAccount(account);
        this.setCategory(category);
        this.setConcept(concept);
        this.setDtOpRange(dtOpRange);
        this.setDtValRange(dtValRange);
        this.setAmountRange(amountRange);
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
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public ValueRange<LocalDate> getDtOpRange() {
        return dtOpRange;
    }

    public void setDtOpRange(ValueRange<LocalDate> dtOpRange) {
        this.dtOpRange = dtOpRange;
    }

    public ValueRange<LocalDate> getDtValRange() {
        return dtValRange;
    }

    public void setDtValRange(ValueRange<LocalDate> dtValRange) {
        this.dtValRange = dtValRange;
    }

    public ValueRange<BigDecimal> getAmountRange() {
        return amountRange;
    }

    public void setAmountRange(ValueRange<BigDecimal> amountRange) {
        this.amountRange = amountRange;
    }

    public boolean filterByAccount() {
        return getAccount() != null;
    }

    public boolean filterByCategory() {
        return getCategory() != null;
    }

    public boolean filterByConcept() {
        return StringUtils.isNotBlank(getConcept());
    }

    public boolean filterByDtOp() {
        return getDtOpRange() != null && (getDtOpRange().hasFrom() || dtOpRange.hasTo());
    }

    public boolean filterByDtVal() {
        return getDtValRange() != null && (getDtValRange().hasFrom() || getDtValRange().hasTo());
    }

    public boolean filterByAmount() {
        return getAmountRange() != null && (getAmountRange().hasFrom() || getAmountRange().hasTo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionFilter that = (TransactionFilter) o;

        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (amountRange != null ? !amountRange.equals(that.amountRange) : that.amountRange != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (concept != null ? !concept.equals(that.concept) : that.concept != null) return false;
        if (dtOpRange != null ? !dtOpRange.equals(that.dtOpRange) : that.dtOpRange != null) return false;
        //noinspection RedundantIfStatement
        if (dtValRange != null ? !dtValRange.equals(that.dtValRange) : that.dtValRange != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = account != null ? account.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (concept != null ? concept.hashCode() : 0);
        result = 31 * result + (dtOpRange != null ? dtOpRange.hashCode() : 0);
        result = 31 * result + (dtValRange != null ? dtValRange.hashCode() : 0);
        result = 31 * result + (amountRange != null ? amountRange.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("TransactionFilter{account=%s, category=%s, concept='%s', dtOpRange=%s, dtValRange=%s, amountRange=%s}",
                account, category, concept, dtOpRange, dtValRange, amountRange);
    }

    public String toPresentableString() {
        StringBuilder sb = new StringBuilder();
        if (filterByAccount()) {
            sb.append("account: \"").append(getAccount().getBank().getName()).append(getAccount().getLastFourDigits()).append("\"; ");
        }
        if (filterByCategory()) {
            sb.append("category: \"").append(getCategory().getName()).append("\"; ");
        }
        if (filterByConcept()) {
            sb.append("concept: \"").append(getConcept()).append("\"; ");
        }
        if (filterByDtOp()) {
            sb.append("op date: ").append(getDtOpRange().toPresentableString()).append("; ");
        }
        if (filterByDtVal()) {
            sb.append("val date: ").append(getDtValRange().toPresentableString()).append("; ");
        }
        if (filterByAmount()) {
            sb.append("amount: ").append(getAmountRange().toPresentableString()).append("; ");
        }
        return sb.toString();
    }

}
