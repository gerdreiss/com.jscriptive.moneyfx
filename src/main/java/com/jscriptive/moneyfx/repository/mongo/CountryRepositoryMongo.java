package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.model.Country;
import com.jscriptive.moneyfx.repository.CountryRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by jscriptive.com on 16/11/14.
 */
@Repository
public class CountryRepositoryMongo extends AbstractRepositoryMongo<Country> implements CountryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Collection<Country> findAll() {
        List<Country> countries = mongoTemplate.findAll(Country.class);
        if (CollectionUtils.isNotEmpty(countries)) {
            countries.sort((c1, c2) -> c1.getLocale().getCountry().compareTo(c2.getLocale().getCountry()));
        }
        return countries;
    }

    @Override
    public Country findByCode(String code) {
        Collection<Country> countries = findAll();
        if (CollectionUtils.isNotEmpty(countries)) {
            Optional<Country> result = countries.stream().filter(c -> code.equals(c.getLocale().getCountry())).findFirst();
            if (result.isPresent()) {
                return result.get();
            }
        }
        return null;
    }

}
