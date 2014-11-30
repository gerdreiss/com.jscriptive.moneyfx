package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Bank;
import com.jscriptive.moneyfx.repository.BankRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class BankRepositoryMongo extends AbstractRepositoryMongo<Bank> implements BankRepository {

    public static final Sort NAME_ASC = new Sort(ASC, "name");

    @Override
    public Collection<Bank> findAll() {
        return mongoTemplate.find(new Query().with(NAME_ASC), Bank.class);
    }

    @Override
    public Bank findByName(String name) {
        return mongoTemplate.findOne(query(where("name").is(name)), Bank.class);
    }


}
