package com.jscriptive.moneyfx.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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

    @Indexed
    private String bankName;

    @Indexed
    private final String accountNumber;

    @Indexed
    private final String categoryName;

    @Indexed
    private final String concept;

    private final ValueRange<LocalDate> dtOpRange;

    private final ValueRange<LocalDate> dtValRange;

    private final ValueRange<BigDecimal> amountRange;

    public TransactionFilter(String bankName, String accountNumber, String categoryName, String concept, ValueRange<LocalDate> dtOpRange, ValueRange<LocalDate> dtValRange, ValueRange<BigDecimal> amountRange) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.categoryName = categoryName;
        this.concept = concept;
        this.dtOpRange = dtOpRange;
        this.dtValRange = dtValRange;
        this.amountRange = amountRange;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public boolean filterByBank() {
        return getBankName() != null;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean filterByAccount() {
        return getAccountNumber() != null;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean filterByCategory() {
        return getCategoryName() != null;
    }

    public String getConcept() {
        return concept;
    }

    public boolean filterByConcept() {
        return StringUtils.isNotBlank(getConcept());
    }

    public ValueRange<LocalDate> getDtOpRange() {
        return dtOpRange;
    }

    public boolean filterByDtOp() {
        return getDtOpRange() != null && (getDtOpRange().hasFrom() || dtOpRange.hasTo());
    }

    public ValueRange<LocalDate> getDtValRange() {
        return dtValRange;
    }

    public boolean filterByDtVal() {
        return getDtValRange() != null && (getDtValRange().hasFrom() || getDtValRange().hasTo());
    }

    public ValueRange<BigDecimal> getAmountRange() {
        return amountRange;
    }

    public boolean filterByAmount() {
        return getAmountRange() != null && (getAmountRange().hasFrom() || getAmountRange().hasTo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionFilter)) return false;

        TransactionFilter that = (TransactionFilter) o;

        if (bankName != null ? !bankName.equals(that.bankName) : that.bankName != null) return false;
        if (accountNumber != null ? !accountNumber.equals(that.accountNumber) : that.accountNumber != null)
            return false;
        if (amountRange != null ? !amountRange.equals(that.amountRange) : that.amountRange != null) return false;
        if (categoryName != null ? !categoryName.equals(that.categoryName) : that.categoryName != null) return false;
        if (concept != null ? !concept.equals(that.concept) : that.concept != null) return false;
        if (dtOpRange != null ? !dtOpRange.equals(that.dtOpRange) : that.dtOpRange != null) return false;
        //noinspection RedundantIfStatement
        if (dtValRange != null ? !dtValRange.equals(that.dtValRange) : that.dtValRange != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bankName != null ? bankName.hashCode() : 0;
        result = 31 * result + (accountNumber != null ? accountNumber.hashCode() : 0);
        result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
        result = 31 * result + (concept != null ? concept.hashCode() : 0);
        result = 31 * result + (dtOpRange != null ? dtOpRange.hashCode() : 0);
        result = 31 * result + (dtValRange != null ? dtValRange.hashCode() : 0);
        result = 31 * result + (amountRange != null ? amountRange.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("TransactionFilter {bankName=%s, accountNumber=%s, categoryName=%s, concept='%s', dtOpRange=%s, dtValRange=%s, amountRange=%s}",
                bankName, accountNumber, categoryName, concept, dtOpRange, dtValRange, amountRange);
    }

    public static class ValueRange<T> {
        private final T from;
        private final T to;

        public ValueRange(T from, T to) {
            this.from = from;
            this.to = to;
        }

        public T from() {
            return from;
        }

        public boolean hasFrom() {
            return from() != null;
        }

        public T to() {
            return to;
        }

        public boolean hasTo() {
            return to() != null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ValueRange)) return false;

            ValueRange that = (ValueRange) o;

            if (from != null ? !from.equals(that.from) : that.from != null) return false;
            //noinspection RedundantIfStatement
            if (to != null ? !to.equals(that.to) : that.to != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return String.format("ValueRange{from=%s, to=%s}", from, to);
        }
    }
}
