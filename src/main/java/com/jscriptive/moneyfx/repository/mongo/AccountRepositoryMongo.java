package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.jscriptive.moneyfx.repository.mongo.util.CriteriaBuilder.by;
import static com.jscriptive.moneyfx.repository.mongo.util.CriteriaBuilder.is;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


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
        return mongoTemplate.find(query(by(bank)), Account.class);
    }

    @Override
    public Account findByNumber(String number) {
        return mongoTemplate.findOne(query(where("number").is(number)), Account.class);
    }

    @Override
    public void insert(Account account) {
        mongoTemplate.insert(account);
    }

    @Override
    public void update(Account account) {
        Update update = new Update();
        update.set("name", account.getName());
        update.set("type", account.getType());
        update.set("balance", account.getBalance());
        if (account.getBalanceDate() == null) {
            update.set("balanceDate", LocalDate.now());
        } else {
            update.set("balanceDate", account.getBalanceDate());
        }
        mongoTemplate.updateFirst(query(is(account)), update, Account.class);
    }

    @Override
    public boolean remove(Account account) {
        return mongoTemplate.remove(query(is(account)), Account.class).getN() > 0;
    }
}
