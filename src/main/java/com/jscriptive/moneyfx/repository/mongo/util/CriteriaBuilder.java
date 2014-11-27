package com.jscriptive.moneyfx.repository.mongo.util;

import com.jscriptive.moneyfx.model.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by jscriptive.com on 22/11/2014.
 */
public class CriteriaBuilder {

    /**
     * This method creates criteria for the given ID
     *
     * @param id The ID
     * @return The ID criteria
     */
    public static Criteria isId(String id) {
        Criteria c = new Criteria();
        c.and("_id").is(new ObjectId(id));
        return c;
    }

    /**
     * This method creates criteria for the given account for queries of objects that have a relation to max one account.
     *
     * @param account The account by which to query
     * @return The Criteria object
     */
    public static Criteria by(Account account) {
        Criteria c = new Criteria();
        c = c.and("account.$id").is(new ObjectId(account.getId()));
        return c;
    }

    /**
     * This method creates criteria for the given account for queries of accounts.
     *
     * @param account The account for which to query
     * @return The Criteria object
     */
    public static Criteria is(Account account) {
        Criteria c = new Criteria();
        if (StringUtils.isBlank(account.getId())) {
            c = c.and("bank.name").is(account.getBank().getName()).and("number").is(account.getNumber());
        } else {
            c = c.and("_id").is(new ObjectId(account.getId()));
        }
        return c;
    }

    /**
     * This method creates criteria for the given bank for queries of objects that have a relation to max one bank.
     *
     * @param bank The bank by which to query
     * @return The Criteria object
     */
    public static Criteria by(Bank bank) {
        Criteria c = new Criteria();
        c = c.and("bank.$id").is(new ObjectId(bank.getId()));
        return c;
    }

    /**
     * This method creates criteria for the given bank for queries of banks.
     *
     * @param bank The bank for which to query
     * @return The Criteria object
     */
    public static Criteria is(Bank bank) {
        Criteria c = new Criteria();
        if (StringUtils.isBlank(bank.getId())) {
            c = c.and("name").is(bank.getName());
        } else {
            c = c.and("_id").is(new ObjectId(bank.getId()));
        }
        return c;
    }

    /**
     * This method creates criteria for the given category for queries of objects that have a relation to max one category.
     *
     * @param category The category by which to query
     * @return The Criteria object
     */
    public static Criteria by(Category category) {
        Criteria c = new Criteria();
        c = c.and("category.$id").is(new ObjectId(category.getId()));
        return c;
    }

    /**
     * This method creates criteria for the given category for queries of categories.
     *
     * @param category The category for which to query
     * @return The Criteria object
     */
    public static Criteria is(Category category) {
        Criteria c = new Criteria();
        if (StringUtils.isBlank(category.getId())) {
            c = c.and("name").is(category.getName());
        } else {
            c = c.and("_id").is(new ObjectId(category.getId()));
        }
        return c;
    }

    /**
     * This method creates criteria for the given bank for queries of objects that have a relation to max one bank.
     *
     * @param filter The filter by which to query
     * @return The Criteria object
     */
    public static Criteria by(TransactionFilter filter) {
        Criteria criteria = new Criteria();
        if (filter == null) {
            return criteria;
        }
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
        return criteria;
    }

    private static Criteria addAccount(Criteria criteria, Account account) {
        return criteria.and("account.$id").is(new ObjectId(account.getId()));
    }

    private static Criteria addCategory(Criteria criteria, Category category) {
        return criteria.and("category.$id").is(new ObjectId(category.getId()));
    }

    private static Criteria addConcept(Criteria criteria, String concept) {
        return criteria.and("concept").regex(concept);
    }

    private static Criteria addDtOp(Criteria criteria, ValueRange<LocalDate> dtOpRange) {
        if (dtOpRange.hasFrom() && dtOpRange.hasTo()) {
            criteria = criteria.and("dtOp").gte(dtOpRange.from()).lte(dtOpRange.to());
        } else if (dtOpRange.hasFrom()) {
            criteria = criteria.and("dtOp").gte(dtOpRange.from());
        } else if (dtOpRange.hasTo()) {
            criteria = criteria.and("dtOp").lte(dtOpRange.to());
        }
        return criteria;
    }

    private static Criteria addDtVal(Criteria criteria, ValueRange<LocalDate> dtValRange) {
        if (dtValRange.hasFrom() && dtValRange.hasTo()) {
            criteria = criteria.and("dtVal").gte(dtValRange.from()).lte(dtValRange.to());
        } else if (dtValRange.hasFrom()) {
            criteria = criteria.and("dtVal").gte(dtValRange.from());
        } else if (dtValRange.hasTo()) {
            criteria = criteria.and("dtVal").lte(dtValRange.to());
        }
        return criteria;
    }

    private static Criteria addAmount(Criteria criteria, ValueRange<BigDecimal> amountRange) {
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
