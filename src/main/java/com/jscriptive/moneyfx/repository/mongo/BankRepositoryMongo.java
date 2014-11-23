package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import static com.jscriptive.moneyfx.repository.mongo.util.CriteriaBuilder.is;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class BankRepositoryMongo implements BankRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Bank findByName(String name) {
        return mongoTemplate.findOne(query(where("name").is(name)), Bank.class);
    }

    @Override
    public boolean remove(Bank bank) {
        return mongoTemplate.remove(query(is(bank)), Bank.class).getN() > 0;
    }

    @Override
    public Collection<Bank> findAll() {
        return mongoTemplate.findAll(Bank.class);
    }

    @Override
    public void insert(Bank bank) {
        mongoTemplate.insert(bank);
    }

}
