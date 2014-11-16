package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class CategoryRepositoryMongo implements CategoryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Category> findByName(String name) {
        return Collections.emptyList();
    }
}
