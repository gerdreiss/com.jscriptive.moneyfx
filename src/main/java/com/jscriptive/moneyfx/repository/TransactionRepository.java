package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.model.TransactionFilter;

import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface TransactionRepository {

    List<Transaction> findAll();

    List<Transaction> filterAll(TransactionFilter filter);

    List<Transaction> findByCategory(Category category);

    List<Transaction> findByAccount(Account account);

    List<Transaction> findIncomingByAccountAndYearAndMonth(Account value, Integer year, Integer month);

    List<Transaction> findOutgoingByAccountAndYearAndMonth(Account value, Integer year, Integer month);

    List<Transaction> findByAccountAndCategory(Account account, Category category);

    List<Transaction> findIncomingByYearAndMonth(Integer year, Integer month);

    List<Transaction> findOutgoingByYearAndMonth(Integer year, Integer month);

    Transaction findEarliestTransaction();

    Transaction findEarliestTransactionOfAccount(Account account);

    void save(Transaction transaction);

    void remove(Transaction trx);

    void removeByAccount(Account account);

}
