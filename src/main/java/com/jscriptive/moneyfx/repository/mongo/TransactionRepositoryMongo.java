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
import java.util.Collections;
import java.util.List;

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
    public List<Transaction> findByAccount(Account account) {
        return mongoTemplate.find(new Query(where("account").is(account)), Transaction.class);
    }

    @Override
    public List<Transaction> findByAccountAndYear(Account account, Integer year) {
        return mongoTemplate.find(new Query(where("account").is(account).and("dtOp.year").is(year)), Transaction.class);
    }

    @Override
    public List<Transaction> findByCategory(Category category) {
        return mongoTemplate.find(new Query(where("category").is(category)), Transaction.class);
    }
}
