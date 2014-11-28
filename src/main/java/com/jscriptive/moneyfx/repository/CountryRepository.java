package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Country;

import java.util.Collection;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface CountryRepository {

    Collection<Country> findAll();

    Country findByCode(String code);

    void save(Country country);

    void remove(Country country);
}
