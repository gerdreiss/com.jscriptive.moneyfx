package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.repository.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class CategoryRepositoryMongo extends AbstractRepositoryMongo<Category> implements CategoryRepository {

    private static final Sort NAME_ASC = new Sort(ASC, "name");

    @Override
    public List<Category> findAll() {
        return mongoTemplate.find(new Query().with(NAME_ASC), Category.class);
    }

    @Override
    public Category findByName(String name) {
        return mongoTemplate.findOne(query(where("name").is(name)), Category.class);
    }
}
