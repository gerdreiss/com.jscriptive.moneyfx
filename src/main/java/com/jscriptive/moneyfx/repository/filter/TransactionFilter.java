package com.jscriptive.moneyfx.repository.filter;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by jscriptive.com on 20/11/2014.
 */
public class TransactionFilter {

    private final Account account;
    private final Category category;
    private final String concept;
    private final ValueRange<LocalDate> dtOpRange;
    private final ValueRange<LocalDate> dtValRange;
    private final ValueRange<BigDecimal> amountRange;

    public TransactionFilter(Account account, Category category, String concept, ValueRange<LocalDate> dtOpRange, ValueRange<LocalDate> dtValRange, ValueRange<BigDecimal> amountRange) {
        this.account = account;
        this.category = category;
        this.concept = concept;
        this.dtOpRange = dtOpRange;
        this.dtValRange = dtValRange;
        this.amountRange = amountRange;
    }

    public Account getAccount() {
        return account;
    }

    public boolean filterByAccount() {
        return getAccount() != null;
    }

    public Category getCategory() {
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
    }
}
