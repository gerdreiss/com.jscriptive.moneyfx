package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

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
        return mongoTemplate.findOne(new Query(where("name").is(name)), Category.class);
    }
}
