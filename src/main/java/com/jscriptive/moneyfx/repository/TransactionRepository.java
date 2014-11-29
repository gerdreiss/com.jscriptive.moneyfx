package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface TransactionRepository {

    List<Transaction> findAll();

    List<Transaction> filterAll(TransactionFilter filter);

    List<Transaction> findByCategory(Category category);

    List<Transaction> findByAccount(Account account);

    List<Transaction> findByAccountAndCategory(Account account, Category category);

    List<Transaction> findIncomingByAccountAndYearAndMonth(Account value, Integer year, Integer month);

    List<Transaction> findOutgoingByAccountAndYearAndMonth(Account value, Integer year, Integer month);

    List<Transaction> findIncomingByYearAndMonth(Integer year, Integer month);

    List<Transaction> findOutgoingByYearAndMonth(Integer year, Integer month);

    List<Transaction> findIncomingByAccountAndYear(Account value, Integer year);

    List<Transaction> findOutgoingByAccountAndYear(Account value, Integer year);

    List<Transaction> findIncomingByYear(Integer year);

    List<Transaction> findOutgoingByYear(Integer year);

    void save(Transaction transaction);

    void remove(Transaction trx);

    void removeByAccount(Account account);

    long countTransactions();

    long countTransactionsOfAccount(Account account);

    ValueRange<LocalDate> getTransactionOpDateRange();

    ValueRange<LocalDate> getTransactionOpDateRangeForAccount(Account account);

}
