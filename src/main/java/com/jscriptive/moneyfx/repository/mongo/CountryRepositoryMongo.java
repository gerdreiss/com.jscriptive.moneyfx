package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Country;
import com.jscriptive.moneyfx.repository.CountryRepository;
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
public class CountryRepositoryMongo implements CountryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Collection<Country> findAll() {
        return mongoTemplate.findAll(Country.class);
    }

    @Override
    public Country findByCode(String code) {
        return mongoTemplate.findOne(query(where("country").is(code)), Country.class);
    }

    @Override
    public void save(Country country) {
        mongoTemplate.save(country);
    }

    @Override
    public void remove(Country country) {
        mongoTemplate.remove(country);
    }


}
