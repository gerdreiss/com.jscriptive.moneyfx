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
    private String bank;

    @Indexed
    private final String account;

    @Indexed
    private final String category;

    @Indexed
    private final String concept;

    private final ValueRange<LocalDate> dtOpRange;

    private final ValueRange<LocalDate> dtValRange;

    private final ValueRange<BigDecimal> amountRange;

    public TransactionFilter(String bank, String account, String category, String concept, ValueRange<LocalDate> dtOpRange, ValueRange<LocalDate> dtValRange, ValueRange<BigDecimal> amountRange) {
        this.bank = bank;
        this.account = account;
        this.category = category;
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

    public String getBank() {
        return bank;
    }

    public boolean filterByBank() {
        return getBank() != null;
    }

    public String getAccount() {
        return account;
    }

    public boolean filterByAccount() {
        return getAccount() != null;
    }

    public String getCategory() {
        return category;
    }

    public boolean filterByCategory() {
        return getCategory() != null;
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

        if (bank != null ? !bank.equals(that.bank) : that.bank != null) return false;
        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (amountRange != null ? !amountRange.equals(that.amountRange) : that.amountRange != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (concept != null ? !concept.equals(that.concept) : that.concept != null) return false;
        if (dtOpRange != null ? !dtOpRange.equals(that.dtOpRange) : that.dtOpRange != null) return false;
        if (dtValRange != null ? !dtValRange.equals(that.dtValRange) : that.dtValRange != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bank != null ? bank.hashCode() : 0;
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (concept != null ? concept.hashCode() : 0);
        result = 31 * result + (dtOpRange != null ? dtOpRange.hashCode() : 0);
        result = 31 * result + (dtValRange != null ? dtValRange.hashCode() : 0);
        result = 31 * result + (amountRange != null ? amountRange.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("TransactionFilter{bank=%s, account=%s, category=%s, concept='%s', dtOpRange=%s, dtValRange=%s, amountRange=%s}",
                bank, account, category, concept, dtOpRange, dtValRange, amountRange);
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
