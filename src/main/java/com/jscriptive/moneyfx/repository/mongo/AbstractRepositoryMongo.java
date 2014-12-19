package com.jscriptive.moneyfx.repository.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by jscriptive.com on 30/11/14.
 */
public class AbstractRepositoryMongo<E> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    public E save(E entity) {
        mongoTemplate.save(entity);
        return entity;
    }

    public void remove(E entity) {
        mongoTemplate.remove(entity);
    }
}
