package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Bank;

import java.util.Collection;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface BankRepository {

    void insert(Bank bank);

    Collection<Bank> findAll();

    Bank findByName(String name);
}
