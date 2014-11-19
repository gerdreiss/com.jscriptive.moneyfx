package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class TransactionRepositoryMongo implements TransactionRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void insert(Transaction transaction) {
        mongoTemplate.insert(transaction);
    }

    @Override
    public void insert(Collection<Transaction> transactions) {
        mongoTemplate.insert(transactions);
    }

    @Override
    public List<Transaction> findAll() {
        return mongoTemplate.findAll(Transaction.class);
    }

    @Override
    public List<Transaction> findByCategory(Category category) {
        return mongoTemplate.find(new Query(where("category").is(category)), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccount(Account account) {
        return mongoTemplate.find(new Query(where("account.number").is(account.getNumber())), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccountAndYear(Account account, Integer year) {
        if (account == null) {
            return mongoTemplate.find(new Query(where("dtOp.year").is(year)), Transaction.class);
        } else {
            return mongoTemplate.find(new Query(where("account.number").is(account.getNumber()).and("dtOp.year").is(year)), Transaction.class);
        }
    }

    @Override
    public List<Transaction> findByAccountAndYearAndMonth(Account account, Integer year, Integer month) {
        if (account == null) {
            return mongoTemplate.find(new Query(where("dtOp.year").is(year).and("dtOp.month").is(month)), Transaction.class);
        } else {
            return mongoTemplate.find(new Query(where("account.number").is(account.getNumber()).and("dtOp.year").is(year).and("dtOp.month").is(month)), Transaction.class);
        }
    }

    @Override
    public List<Transaction> findIncomingByAccountAndYearAndMonth(Account account, Integer year, Integer month) {
        if (account == null) {
            return mongoTemplate.find(new Query(where("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").gte(ZERO)), Transaction.class);
        } else {
            return mongoTemplate.find(new Query(where("account.number").is(account.getNumber()).and("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").gte(ZERO)), Transaction.class);
        }
    }

    @Override
    public List<Transaction> findOutgoingByAccountAndYearAndMonth(Account account, Integer year, Integer month) {
        if (account == null) {
            return mongoTemplate.find(new Query(where("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").lt(ZERO)), Transaction.class);
        } else {
            return mongoTemplate.find(new Query(where("account.number").is(account.getNumber()).and("dtOp.year").is(year).and("dtOp.month").is(month).and("amount").lt(ZERO)), Transaction.class);
        }
    }
}
