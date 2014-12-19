package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.exception.BusinessException;
import com.jscriptive.moneyfx.model.*;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import static com.jscriptive.moneyfx.model.CountRange.CountRangeIndicator.FIRST;
import static com.jscriptive.moneyfx.model.CountRange.CountRangeIndicator.LAST;
import static com.jscriptive.moneyfx.repository.mongo.util.CriteriaBuilder.by;
import static java.lang.Boolean.TRUE;
import static java.lang.Integer.compare;
import static java.math.BigDecimal.ZERO;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.math.NumberUtils.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Fields.field;
import static org.springframework.data.mongodb.core.aggregation.Fields.from;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class TransactionRepositoryMongo extends AbstractRepositoryMongo<Transaction> implements TransactionRepository {

    private static final Sort OPDATE_ASC = new Sort(ASC, "dtOp");
    private static final Sort OPDATE_DESC = new Sort(DESC, "dtOp");


    @Override
    public void removeByAccount(Account account) {
        mongoTemplate.remove(query(by(account)), Transaction.class);
    }

    @Override
    public long countTransactions() {
        return mongoTemplate.count(null, Transaction.class);
    }

    @Override
    public long countTransactionsOfAccount(Account account) {
        return mongoTemplate.count(query(by(account)), Transaction.class);
    }

    @Override
    public List<Transaction> findAll() {
        return mongoTemplate.find(new Query().with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<Transaction> filterAll(TransactionFilter filter) {
        Query q = query(by(filter));
        if (filter.filterByCountRange()) {
            if (filter.getCountRange().getIndicator() == LAST) {
                q = q.with(OPDATE_DESC);
            } else if (filter.getCountRange().getIndicator() == FIRST) {
                q = q.with(OPDATE_ASC);
            }
            if (filter.getCountRange().getCount() > 0) {
                q = q.limit(filter.getCountRange().getCount());
            }
        }
        return mongoTemplate.find(q, Transaction.class);
    }

    @Override
    public List<Transaction> findByCategory(Category category) {
        return mongoTemplate.find(query(by(category)).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccount(Account account) {
        return mongoTemplate.find(query(by(account)).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccountAndYear(Account account, Integer year) {
        return mongoTemplate.find(query(by(account).and("dtOp.year").is(year)).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccountAndCategory(Account account, Category category) {
        return mongoTemplate.find(query(by(account).andOperator(by(category))).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<TransactionVolume> getYearlyIncomingVolumes(boolean includeTransfers) {
        return getTransactionVolumes(null, false, YEAR, INTEGER_ONE, includeTransfers);
    }

    @Override
    public List<TransactionVolume> getYearlyOutgoingVolumes(boolean includeTransfers) {
        return getTransactionVolumes(null, false, YEAR, INTEGER_MINUS_ONE, includeTransfers);
    }

    @Override
    public List<TransactionVolume> getYearlyIncomingVolumesOfAccount(Account account, boolean includeTransfers) {
        return getTransactionVolumes(account, false, YEAR, INTEGER_ONE, includeTransfers);
    }

    @Override
    public List<TransactionVolume> getYearlyOutgoingVolumesOfAccount(Account account, boolean includeTransfers) {
        return getTransactionVolumes(account, false, YEAR, INTEGER_MINUS_ONE, includeTransfers);
    }

    @Override
    public List<TransactionVolume> getMonthlyIncomingVolumes(boolean includeTransfers) {
        return getTransactionVolumes(null, false, MONTH_OF_YEAR, INTEGER_ONE, includeTransfers);
    }

    @Override
    public List<TransactionVolume> getMonthlyOutgoingVolumes(boolean includeTransfers) {
        return getTransactionVolumes(null, false, MONTH_OF_YEAR, INTEGER_MINUS_ONE, includeTransfers);
    }

    @Override
    public List<TransactionVolume> getMonthlyIncomingVolumesOfAccount(Account account, boolean includeTransfers) {
        return getTransactionVolumes(account, false, MONTH_OF_YEAR, INTEGER_ONE, includeTransfers);
    }

    @Override
    public List<TransactionVolume> getMonthlyOutgoingVolumesOfAccount(Account account, boolean includeTransfers) {
        return getTransactionVolumes(account, false, MONTH_OF_YEAR, INTEGER_MINUS_ONE, includeTransfers);
    }

    @Override
    public List<TransactionVolume> getCategoryVolumes(boolean includeTransfers) {
        return getTransactionVolumes(null, true, null, INTEGER_ZERO, includeTransfers);
    }

    @Override
    public List<TransactionVolume> getAccountCategoryVolumes(Account account, boolean includeTransfers) {
        return getTransactionVolumes(account, true, null, INTEGER_ZERO, includeTransfers);
    }

    @Override
    public List<TransactionVolume> getYearlyCategoryVolumes(boolean includeTransfers) {
        return getTransactionVolumes(null, true, YEAR, INTEGER_ZERO, includeTransfers);
    }

    @Override
    public List<TransactionVolume> getYearlyAccountCategoryVolumes(Account account, boolean includeTransfers) {
        return getTransactionVolumes(account, true, YEAR, INTEGER_ZERO, includeTransfers);
    }

    @Override
    public ValueRange<LocalDate> getTransactionOpDateRange() {
        return getTransactionOpDateRangeForYear(INTEGER_ZERO);
    }

    @Override
    public ValueRange<LocalDate> getTransactionOpDateRangeForYear(Integer year) {
        if (countTransactions() == 0L) {
            return new ValueRange<>(null, null);
        }
        Transaction earliestTransaction = findEarliestTransaction(year);
        Transaction latestTransaction = findLatestTransaction(year);
        return new ValueRange<>(earliestTransaction.getDtOp(), latestTransaction.getDtOp());
    }

    @Override
    public ValueRange<LocalDate> getTransactionOpDateRangeForAccount(Account account) {
        return getTransactionOpDateRangeForAccountAndYear(account, INTEGER_ZERO);
    }

    @Override
    public ValueRange<LocalDate> getTransactionOpDateRangeForAccountAndYear(Account account, Integer year) {
        if (countTransactionsOfAccount(account) == 0L) {
            return new ValueRange<>(null, null);
        }
        Transaction earliestTransaction = findEarliestTransactionOfAccount(account, year);
        Transaction latestTransaction = findLatestTransactionOfAccount(account, year);
        return new ValueRange<>(earliestTransaction.getDtOp(), latestTransaction.getDtOp());
    }

    private Transaction findEarliestTransaction(Integer year) {
        if (INTEGER_ZERO.equals(year)) {
            return mongoTemplate.findOne(new Query().with(OPDATE_ASC), Transaction.class);
        }
        return mongoTemplate.findOne(query(where("dtOp.year").is(year)).with(OPDATE_ASC), Transaction.class);
    }

    private Transaction findEarliestTransactionOfAccount(Account account, Integer year) {
        if (INTEGER_ZERO.equals(year)) {
            return mongoTemplate.findOne(query(by(account)).with(OPDATE_ASC), Transaction.class);
        }
        return mongoTemplate.findOne(query(by(account).and("dtOp.year").is(year)).with(OPDATE_ASC), Transaction.class);
    }

    private Transaction findLatestTransaction(Integer year) {
        if (INTEGER_ZERO.equals(year)) {
            return mongoTemplate.findOne(new Query().with(OPDATE_DESC), Transaction.class);
        }
        return mongoTemplate.findOne(query(where("dtOp.year").is(year)).with(OPDATE_DESC), Transaction.class);
    }

    private Transaction findLatestTransactionOfAccount(Account account, Integer year) {
        if (INTEGER_ZERO.equals(year)) {
            return mongoTemplate.findOne(query(by(account)).with(OPDATE_DESC), Transaction.class);
        }
        return mongoTemplate.findOne(query(by(account).and("dtOp.year").is(year)).with(OPDATE_DESC), Transaction.class);
    }

    private List<TransactionVolume> getTransactionVolumes(Account account, boolean groupByCategory, ChronoField chronoField, Integer zeroComparison, boolean transfers) {
        MatchOperation match = match(getMatchCriteria(account, zeroComparison, transfers));
        GroupOperation group = group(getGroupFields(groupByCategory, chronoField)).sum("amount").as("volume");
        Aggregation aggregation;
        // When we aggregate by one grouping field, the workaround below is necessary because Spring Data MongoDB
        // doesn't map the one field although we clearly indicate a field as such a mapping. Bug?
        if (groupByCategory && chronoField == null) {
            ProjectionOperation project = project().and("category").previousOperation().and("volume").as("volume");
            aggregation = newAggregation(Transaction.class, match, group, project);
        } else if (!groupByCategory && chronoField == YEAR) {
            ProjectionOperation project = project().and("year").previousOperation().and("volume").as("volume");
            aggregation = newAggregation(Transaction.class, match, group, project);
        } else {
            aggregation = newAggregation(Transaction.class, match, group);
        }
        return mongoTemplate.aggregate(aggregation, Transaction.class, TransactionVolume.class).getMappedResults();
    }

    private Criteria getMatchCriteria(Account account, Integer zeroComparison, boolean transfers) {
        Criteria match = account == null ? where("amount") : by(account).and("amount");
        int comparison = compare(zeroComparison, INTEGER_ZERO);
        if (comparison < 0) {
            match = match.lt(ZERO);
        } else if (comparison > 0) {
            match = match.gte(ZERO);
        } else {
            match = match.ne(ZERO);
        }
        if (transfers) {
            return match;
        }
        return match.and("isTransfer").ne(TRUE);
    }

    private Fields getGroupFields(boolean groupByCategory, ChronoField chronoField) {
        if (!groupByCategory && chronoField == null) {
            throw new BusinessException("When querying for volumes either category, or chronological field, or both have to be used as grouping fields");
        }
        List<Field> fields = new ArrayList<>();
        if (groupByCategory) {
            fields.add(field("category", "category"));
        }
        if (chronoField != null) {
            switch (chronoField) {
                case DAY_OF_YEAR:
                case DAY_OF_MONTH:
                case DAY_OF_WEEK:
                    fields.addAll(asList(field("year", "dtOp.year"), field("month", "dtOp.month"), field("day", "dtOp.day")));
                    break;
                case MONTH_OF_YEAR:
                    fields.addAll(asList(field("year", "dtOp.year"), field("month", "dtOp.month")));
                    break;
                case YEAR:
                case YEAR_OF_ERA:
                    fields.add(field("year", "dtOp.year"));
                    break;
                default:
                    break;
            }
        }
        return from(fields.toArray(new Field[fields.size()]));
    }
}
