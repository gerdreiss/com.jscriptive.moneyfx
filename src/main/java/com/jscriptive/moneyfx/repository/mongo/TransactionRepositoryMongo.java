package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.*;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

import static com.jscriptive.moneyfx.repository.mongo.util.CriteriaBuilder.by;
import static java.lang.Integer.compare;
import static java.math.BigDecimal.ZERO;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static org.apache.commons.lang3.math.NumberUtils.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Fields.field;
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
        return mongoTemplate.find(query(by(filter)).with(OPDATE_DESC), Transaction.class);
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
    public List<Transaction> findByAccountAndCategory(Account account, Category category) {
        return mongoTemplate.find(query(by(account).andOperator(by(category))).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<TransactionVolume> getYearlyIncomingVolumes() {
        return getTransactionVolumes(null, YEAR, INTEGER_ONE);
    }

    @Override
    public List<TransactionVolume> getYearlyOutgoingVolumes() {
        return getTransactionVolumes(null, YEAR, INTEGER_MINUS_ONE);
    }

    @Override
    public List<TransactionVolume> getYearlyIncomingVolumesOfAccount(Account account) {
        return getTransactionVolumes(account, YEAR, INTEGER_ONE);
    }

    @Override
    public List<TransactionVolume> getYearlyOutgoingVolumesOfAccount(Account account) {
        return getTransactionVolumes(account, YEAR, INTEGER_MINUS_ONE);
    }

    @Override
    public List<TransactionVolume> getMonthlyIncomingVolumes() {
        return getTransactionVolumes(null, MONTH_OF_YEAR, INTEGER_ONE);
    }

    @Override
    public List<TransactionVolume> getMonthlyOutgoingVolumes() {
        return getTransactionVolumes(null, MONTH_OF_YEAR, INTEGER_MINUS_ONE);
    }

    @Override
    public List<TransactionVolume> getMonthlyIncomingVolumesOfAccount(Account account) {
        return getTransactionVolumes(account, MONTH_OF_YEAR, INTEGER_ONE);
    }

    @Override
    public List<TransactionVolume> getMonthlyOutgoingVolumesOfAccount(Account account) {
        return getTransactionVolumes(account, MONTH_OF_YEAR, INTEGER_MINUS_ONE);
    }

    @Override
    public ValueRange<LocalDate> getTransactionOpDateRange() {
        if (countTransactions() == 0L) {
            return new ValueRange<>(null, null);
        }
        Transaction earliestTransaction = findEarliestTransaction();
        Transaction latestTransaction = findLatestTransaction();
        return new ValueRange<>(earliestTransaction.getDtOp(), latestTransaction.getDtOp());
    }

    @Override
    public ValueRange<LocalDate> getTransactionOpDateRangeForAccount(Account account) {
        if (countTransactionsOfAccount(account) == 0L) {
            return new ValueRange<>(null, null);
        }
        Transaction earliestTransaction = findEarliestTransactionOfAccount(account);
        Transaction latestTransaction = findLatestTransactionOfAccount(account);
        return new ValueRange<>(earliestTransaction.getDtOp(), latestTransaction.getDtOp());
    }

    private Transaction findEarliestTransaction() {
        return mongoTemplate.findOne(new Query().with(OPDATE_ASC), Transaction.class);
    }

    private Transaction findEarliestTransactionOfAccount(Account account) {
        return mongoTemplate.findOne(query(by(account)).with(OPDATE_ASC), Transaction.class);
    }

    private Transaction findLatestTransaction() {
        return mongoTemplate.findOne(new Query().with(OPDATE_DESC), Transaction.class);
    }

    private Transaction findLatestTransactionOfAccount(Account account) {
        return mongoTemplate.findOne(query(by(account)).with(OPDATE_DESC), Transaction.class);
    }

    private List<TransactionVolume> getTransactionVolumes(Account account, ChronoField chronoField, Integer zeroComparison) {
        MatchOperation match = match(getMatchCriteria(account, zeroComparison));
        GroupOperation group = group(getOpDateFields(chronoField)).sum("amount").as("volume");
        Aggregation aggregation;
        // When we aggregate for yearly volume, the workaround below is necessary because Spring Data MongoDB
        // doesn't map dtOp.year to year although we clearly indicate a field as such a mapping. Bug?
        if (chronoField == ChronoField.YEAR) {
            ProjectionOperation project = project().and("year").previousOperation().and("volume").as("volume");
            aggregation = newAggregation(Transaction.class, match, group, project);
        } else {
            aggregation = newAggregation(Transaction.class, match, group);
        }
        return mongoTemplate.aggregate(aggregation, Transaction.class, TransactionVolume.class).getMappedResults();
    }

    private Criteria getMatchCriteria(Account account, Integer zeroComparison) {
        // TODO match for non transfers
        Criteria match = (account == null) ? where("amount") : by(account).and("amount");
        int comparison = compare(zeroComparison, INTEGER_ZERO);
        if (comparison < 0) {
            match = match.lt(ZERO);
        } else if (comparison > 0) {
            match = match.gte(ZERO);
        } else {
            match = match.ne(ZERO);
        }
        return match;
    }

    private Fields getOpDateFields(ChronoField chronoField) {
        switch (chronoField) {
            case DAY_OF_YEAR:
            case DAY_OF_MONTH:
            case DAY_OF_WEEK:
                return Fields.from(field("year", "dtOp.year"), field("month", "dtOp.month"), field("day", "dtOp.day"));
            case MONTH_OF_YEAR:
                return Fields.from(field("year", "dtOp.year"), field("month", "dtOp.month"));
            //case YEAR:
            //case YEAR_OF_ERA:
            default:
                return Fields.from(field("year", "dtOp.year"));
        }
    }
}
