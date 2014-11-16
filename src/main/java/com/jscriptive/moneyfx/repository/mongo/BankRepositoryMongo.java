package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class BankRepositoryMongo implements BankRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Bank> findByName(String name) {
        return Collections.emptyList();
    }

    @Override
    public void insert(Bank bank) {
        mongoTemplate.insert(bank);
    }

}
