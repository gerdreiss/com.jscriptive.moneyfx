package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.Transaction;

import java.util.Collection;
import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface TransactionRepository {

    void insert(Transaction transaction);

    void insert(Collection<Transaction> transactions);

    List<Transaction> findAll();

    List<Transaction> findByAccount(Account account);

    List<Transaction> findByAccountAndYear(Account account, Integer year);

    List<Transaction> findByCategory(Category category);

    List<Transaction> findByAccountAndYearAndMonth(Account account, Integer year, Integer month);

    List<Transaction> findIncomingByAccountAndYearAndMonth(Account value, Integer year, Integer month);

    List<Transaction> findOutgoingByAccountAndYearAndMonth(Account value, Integer year, Integer month);
}
