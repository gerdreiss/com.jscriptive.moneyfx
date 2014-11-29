package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.*;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.jscriptive.moneyfx.repository.mongo.util.CriteriaBuilder.by;
import static java.math.BigDecimal.ZERO;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class TransactionRepositoryMongo implements TransactionRepository {

    private static final Sort OPDATE_ASC = new Sort(ASC, "dtOp");
    private static final Sort OPDATE_DESC = new Sort(DESC, "dtOp");

    @Autowired
    private MongoTemplate mongoTemplate;

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
    public List<Transaction> findIncomingByAccountAndYearAndMonth(Account account, Integer year, Integer month) {
        return mongoTemplate.find(query(by(account).and("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").gte(ZERO)).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<Transaction> findOutgoingByAccountAndYearAndMonth(Account account, Integer year, Integer month) {
        return mongoTemplate.find(query(by(account).and("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").lt(ZERO)).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<Transaction> findIncomingByYearAndMonth(Integer year, Integer month) {
        return mongoTemplate.find(query(where("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").gte(ZERO)).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<Transaction> findOutgoingByYearAndMonth(Integer year, Integer month) {
        return mongoTemplate.find(query(where("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").lt(ZERO)).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<Transaction> findIncomingByAccountAndYear(Account account, Integer year) {
        return mongoTemplate.find(query(by(account).and("dtOp.year").is(year).and("amount").gte(ZERO)).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<Transaction> findOutgoingByAccountAndYear(Account account, Integer year) {
        return mongoTemplate.find(query(by(account).and("dtOp.year").is(year).and("amount").lt(ZERO)).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<Transaction> findIncomingByYear(Integer year) {
        return mongoTemplate.find(query(where("dtOp.year").is(year).and("amount").gte(ZERO)).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public List<Transaction> findOutgoingByYear(Integer year) {
        return mongoTemplate.find(query(where("dtOp.year").is(year).and("amount").lt(ZERO)).with(OPDATE_DESC), Transaction.class);
    }

    @Override
    public void save(Transaction transaction) {
        mongoTemplate.save(transaction);
    }

    @Override
    public void remove(Transaction trx) {
        mongoTemplate.remove(trx);
    }

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
}
