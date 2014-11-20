package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.repository.AccountRepository;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
        return mongoTemplate.find(new Query(where("bank.name").is(bank.getName())), Account.class);
    }

    @Override
    public Account findByNumber(String number) {
        return mongoTemplate.findOne(new Query(where("number").is(number)), Account.class);
    }

    @Override
    public void insert(Account account) {
        mongoTemplate.insert(account);
    }

    @Override
    public void update(Account account) {
        Query query = new Query(Criteria.where("bank.name").is(account.getBank().getName()).and("number").is(account.getNumber()).and("name").is(account.getName()));
        Update update = Update.update("number", account.getNumber()).addToSet("name", account.getName()).addToSet("type", account.getType());
        if (account.getBalance() != null) {
            update = update.addToSet("balance", account.getBalance());
            if (account.getBalanceDate() == null) {
                update = update.addToSet("balanceDate", LocalDate.now());
            } else {
                update = update.addToSet("balanceDate", account.getBalanceDate());
            }
        }
        mongoTemplate.updateFirst(query, update, Account.class);
    }

    @Override
    public boolean remove(Account account) {
        Query query = new Query(Criteria.where("bank.name").is(account.getBank().getName()).and("number").is(account.getNumber()).and("name").is(account.getName()));
        WriteResult result = mongoTemplate.remove(query, Account.class);
        return 0 < result.getN();
    }
}
