package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.Transaction;
import com.jscriptive.moneyfx.model.TransactionFilter;

import java.util.Collection;
import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface TransactionRepository {

    void insert(Transaction transaction);

    void insert(Collection<Transaction> transactions);

    List<Transaction> findAll();

    List<Transaction> filterAll(TransactionFilter filter);

    List<Transaction> findByAccount(Account account);

    List<Transaction> findByAccountAndYear(Account account, Integer year);

    List<Transaction> findByAccountAndYearAndMonth(Account account, Integer year, Integer month);

    List<Transaction> findIncomingByAccountAndYearAndMonth(Account value, Integer year, Integer month);

    List<Transaction> findOutgoingByAccountAndYearAndMonth(Account value, Integer year, Integer month);

    List<Transaction> findByAccountAndCategory(Account account, Category category);

    List<Transaction> findByYear(Integer year);

    List<Transaction> findByYearAndMonth(Integer year, Integer month);

    List<Transaction> findIncomingByYearAndMonth(Integer year, Integer month);

    List<Transaction> findOutgoingByYearAndMonth(Integer year, Integer month);

    List<Transaction> findByCategory(Category category);

    Transaction findEarliestTransaction();

    Transaction findEarliestTransactionOfAccount(Account account);

    int removeByAccount(Account account);

    void updateCategory(Transaction trx, Category category);

}
