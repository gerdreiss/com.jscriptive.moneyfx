package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.repository.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class AccountRepositoryMongo extends AbstractRepositoryMongo<Account> implements AccountRepository {

    @Override
    public List<Account> findAll() {
        return mongoTemplate.findAll(Account.class).stream().sorted((a1, a2) -> a1.getBank().getName().compareTo(a2.getBank().getName())).collect(toList());
    }

    @Override
    public Account findByNumber(String number) {
        return mongoTemplate.findOne(query(where("number").is(number)), Account.class);
    }

}
