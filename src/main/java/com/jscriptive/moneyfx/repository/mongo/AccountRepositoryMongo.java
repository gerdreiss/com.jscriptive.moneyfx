package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class AccountRepositoryMongo implements AccountRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Account> findByNumber(String number) {
        return Collections.emptyList();
    }

    @Override
    public void insert(Account account) {
        mongoTemplate.insert(account);
    }
}
