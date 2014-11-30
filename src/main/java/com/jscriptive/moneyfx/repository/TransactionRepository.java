package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface TransactionRepository {

    void save(Transaction transaction);

    void remove(Transaction trx);

    void removeByAccount(Account account);

    long countTransactions();

    long countTransactionsOfAccount(Account account);

    List<Transaction> findAll();

    List<Transaction> filterAll(TransactionFilter filter);

    List<Transaction> findByCategory(Category category);

    List<Transaction> findByAccount(Account account);

    List<Transaction> findByAccountAndCategory(Account account, Category category);

    List<TransactionVolume> getYearlyIncomingVolumes();

    List<TransactionVolume> getYearlyOutgoingVolumes();

    List<TransactionVolume> getYearlyIncomingVolumesOfAccount(Account account);

    List<TransactionVolume> getYearlyOutgoingVolumesOfAccount(Account account);

    List<TransactionVolume> getMonthlyIncomingVolumes();

    List<TransactionVolume> getMonthlyOutgoingVolumes();

    List<TransactionVolume> getMonthlyIncomingVolumesOfAccount(Account account);

    List<TransactionVolume> getMonthlyOutgoingVolumesOfAccount(Account account);

    ValueRange<LocalDate> getTransactionOpDateRange();

    ValueRange<LocalDate> getTransactionOpDateRangeForAccount(Account account);

}
