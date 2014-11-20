package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.repository.filter.TransactionFilter;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by jscriptive.com on 20/11/14.
 */
public class FilterQueryBuilder {

    public static Query build(TransactionFilter filter) {
        Criteria criteria = new Criteria();
        if (filter.filterByAccount()) {
            criteria = addAccount(criteria, filter.getAccount());
        }
        if (filter.filterByCategory()) {
            criteria = addCategory(criteria, filter.getCategory());
        }
        if (filter.filterByConcept()) {
            criteria = addConcept(criteria, filter.getConcept());
        }
        if (filter.filterByDtOp()) {
            criteria = addDtOp(criteria, filter.getDtOpRange());
        }
        if (filter.filterByDtVal()) {
            criteria = addDtVal(criteria, filter.getDtValRange());
        }
        if (filter.filterByAmount()) {
            criteria = addAmount(criteria, filter.getAmountRange());
        }
        return new Query(criteria);
    }

    private static Criteria addAccount(Criteria criteria, Account account) {
        if (account.getId() != null) {
            criteria = criteria.and("account._id").is(new ObjectId(account.getId()));
            return criteria;
        }
        if (account.getBank() != null) {
            if (account.getBank().getId() != null) {
                criteria = criteria.and("account.bank._id").is(new ObjectId(account.getBank().getId()));
            } else if (StringUtils.isNotBlank(account.getBank().getName())) {
                criteria = criteria.and("account.bank.name").is(account.getBank().getName());
            }
        }
        if (StringUtils.isNotBlank(account.getNumber())) {
            criteria = criteria.and("account.number").is(account.getNumber());
        }
        if (StringUtils.isNotBlank(account.getName())) {
            criteria = criteria.and("account.name").is(account.getName());
        }
        return criteria;
    }


    private static Criteria addCategory(Criteria criteria, Category category) {
        if (category.getId() != null) {
            criteria = criteria.and("category._id").is(new ObjectId(category.getId()));
            return criteria;
        }
        if (StringUtils.isNotBlank(category.getName())) {
            criteria = criteria.and("category.name").is(category.getName());
        }
        return criteria;
    }

    private static Criteria addConcept(Criteria criteria, String concept) {
        return criteria.and("concept").regex(concept);
    }

    private static Criteria addDtOp(Criteria criteria, TransactionFilter.ValueRange<LocalDate> dtOpRange) {
        if (dtOpRange.hasFrom() && dtOpRange.hasTo()) {
            criteria = criteria.and("dtOp").gte(dtOpRange.from()).lte(dtOpRange.to());
        } else if (dtOpRange.hasFrom()) {
            criteria = criteria.and("dtOp").gte(dtOpRange.from());
        } else if (dtOpRange.hasTo()) {
            criteria = criteria.and("dtOp").lte(dtOpRange.to());
        }
        return criteria;
    }

    private static Criteria addDtVal(Criteria criteria, TransactionFilter.ValueRange<LocalDate> dtValRange) {
        if (dtValRange.hasFrom() && dtValRange.hasTo()) {
            criteria = criteria.and("dtVal").gte(dtValRange.from()).lte(dtValRange.to());
        } else if (dtValRange.hasFrom()) {
            criteria = criteria.and("dtVal").gte(dtValRange.from());
        } else if (dtValRange.hasTo()) {
            criteria = criteria.and("dtVal").lte(dtValRange.to());
        }
        return criteria;
    }

    private static Criteria addAmount(Criteria criteria, TransactionFilter.ValueRange<BigDecimal> amountRange) {
        if (amountRange.hasFrom() && amountRange.hasTo()) {
            criteria = criteria.and("amount").gte(amountRange.from()).lte(amountRange.to());
        } else if (amountRange.hasFrom()) {
            criteria = criteria.and("amount").gte(amountRange.from());
        } else if (amountRange.hasTo()) {
            criteria = criteria.and("amount").lte(amountRange.to());
        }
        return criteria;
    }
}
