package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.exception.BusinessException;
import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.repository.TransactionFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import static com.jscriptive.moneyfx.repository.mongo.util.CriteriaBuilder.isId;
import static org.springframework.data.mongodb.core.query.Criteria.where;
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
        if (filter.getId() == null) {
            throw new BusinessException("Only persisted filter can be updated: ID must be present");
        }
        Update update = new Update();
        update.set("bankName", filter.getBankName());
        update.set("accountNumber", filter.getAccountNumber());
        update.set("categoryName", filter.getCategoryName());
        update.set("concept", filter.getConcept());
        update.set("dtOpRange", filter.getDtOpRange());
        update.set("dtValRange", filter.getDtValRange());
        update.set("amountRange", filter.getAmountRange());
        mongoTemplate.updateFirst(query(isId(filter.getId())), update, Account.class);
    }

    @Override
    public int removeByCategory(Category category) {
        return mongoTemplate.remove(query(where("category.name").is(category.getName())), TransactionFilter.class).getN();
    }
}
