package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Bank;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface BankRepository {
    List<Bank> findByName(String name);
    void insert(Bank bank);
}
