package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.repository.TransactionFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import static com.jscriptive.moneyfx.repository.mongo.util.NullSafeCriteriaBuilder.is;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class TransactionFilterRepositoryMongo implements TransactionFilterRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Collection<TransactionFilter> findAll() {
        return mongoTemplate.findAll(TransactionFilter.class);
    }

    @Override
    public void insert(TransactionFilter filter) {
        mongoTemplate.insert(filter);
    }

    @Override
    public void update(TransactionFilter filter) {
        Update update = Update.update("bankName", filter.getBankName())
                .addToSet("accountNumber", filter.getAccountNumber())
                .addToSet("categoryName", filter.getCategoryName())
                .addToSet("concept", filter.getConcept())
                .addToSet("dtOpRange", filter.getDtOpRange())
                .addToSet("dtValRange", filter.getDtValRange())
                .addToSet("amountRange", filter.getAmountRange());
        mongoTemplate.updateFirst(query(is(filter)), update, Account.class);
    }
}
