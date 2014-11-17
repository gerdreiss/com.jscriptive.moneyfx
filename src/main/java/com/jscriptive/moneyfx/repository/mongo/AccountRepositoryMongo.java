package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class AccountRepositoryMongo implements AccountRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Account> findAll() {
        return mongoTemplate.findAll(Account.class);
    }

    @Override
    public List<Account> findByBank(Bank bank) {
        return mongoTemplate.find(new Query(where("bank").is(bank)), Account.class);
    }

    @Override
    public Account findByNumber(String number) {
        return mongoTemplate.findOne(new Query(where("number").is(number)), Account.class);
    }

    @Override
    public void insert(Account account) {
        mongoTemplate.insert(account);
    }
}
