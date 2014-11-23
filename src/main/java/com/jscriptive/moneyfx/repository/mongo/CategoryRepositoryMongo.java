package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.exception.BusinessException;
import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jscriptive.moneyfx.repository.mongo.util.CriteriaBuilder.is;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class CategoryRepositoryMongo implements CategoryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void insert(Category category) {
        mongoTemplate.insert(category);
    }

    @Override
    public List<Category> findAll() {
        return mongoTemplate.findAll(Category.class);
    }

    @Override
    public Category findByName(String name) {
        return mongoTemplate.findOne(query(where("name").is(name)), Category.class);
    }

    @Override
    public boolean remove(Category category) {
        return mongoTemplate.remove(query(is(category)), Category.class).getN() > 0;
    }

    @Override
    public void update(Category category) {
        if (category.getId() == null) {
            throw new BusinessException("Only persisted category can be updated: ID must be present");
        }
        if (category.getFilterRule() != null && category.getFilterRule().getId() == null) {
            throw new BusinessException("Only persisted filter rules of the category can be updated: transaction filter ID must be present");
        }
        Update update = new Update();
        update.set("name", category.getName());
        if (category.getFilterRule() != null) {
            update.set("filterRule.", category.getFilterRule());
        }
        mongoTemplate.updateFirst(query(is(category)), update, Account.class);
    }
}
