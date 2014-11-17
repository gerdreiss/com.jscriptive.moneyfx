package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Bank;

import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface AccountRepository {

    List<Account> findAll();

    List<Account> findByBank(Bank bank);

    Account findByNumber(String number);

    void insert(Account account);
}
