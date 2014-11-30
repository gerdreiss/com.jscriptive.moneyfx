package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.TransactionFilter;
import com.jscriptive.moneyfx.repository.TransactionFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class TransactionFilterRepositoryMongo extends AbstractRepositoryMongo<TransactionFilter> implements TransactionFilterRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Collection<TransactionFilter> findAll() {
        return mongoTemplate.findAll(TransactionFilter.class);
    }

    @Override
    public void removeByCategory(Category category) {
        mongoTemplate.remove(query(where("category.$id").is(category.getId())), TransactionFilter.class);
    }
}
