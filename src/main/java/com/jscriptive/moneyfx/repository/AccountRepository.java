package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Account;

import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface AccountRepository {

    Account save(Account account);

    void remove(Account account);

    List<Account> findAll();

    Account findByNumber(String number);
}
