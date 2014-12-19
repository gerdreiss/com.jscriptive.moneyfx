package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    void remove(Transaction trx);

    void removeByAccount(Account account);

    long countTransactions();

    long countTransactionsOfAccount(Account account);

    List<Transaction> findAll();

    List<Transaction> filterAll(TransactionFilter filter);

    List<Transaction> findByCategory(Category category);

    List<Transaction> findByAccount(Account account);

    List<Transaction> findByAccountAndYear(Account account, Integer year);

    List<Transaction> findByAccountAndCategory(Account account, Category category);

    List<TransactionVolume> getCategoryVolumes(boolean includeTransfers);

    List<TransactionVolume> getAccountCategoryVolumes(Account account, boolean includeTransfers);

    List<TransactionVolume> getYearlyCategoryVolumes(boolean includeTransfers);

    List<TransactionVolume> getYearlyAccountCategoryVolumes(Account account, boolean includeTransfers);

    List<TransactionVolume> getYearlyIncomingVolumes(boolean includeTransfers);

    List<TransactionVolume> getYearlyOutgoingVolumes(boolean includeTransfers);

    List<TransactionVolume> getMonthlyIncomingVolumes(boolean includeTransfers);

    List<TransactionVolume> getMonthlyOutgoingVolumes(boolean includeTransfers);

    List<TransactionVolume> getYearlyIncomingVolumesOfAccount(Account account, boolean includeTransfers);

    List<TransactionVolume> getYearlyOutgoingVolumesOfAccount(Account account, boolean includeTransfers);

    List<TransactionVolume> getMonthlyIncomingVolumesOfAccount(Account account, boolean includeTransfers);

    List<TransactionVolume> getMonthlyOutgoingVolumesOfAccount(Account account, boolean includeTransfers);

    ValueRange<LocalDate> getTransactionOpDateRange();

    ValueRange<LocalDate> getTransactionOpDateRangeForYear(Integer year);

    ValueRange<LocalDate> getTransactionOpDateRangeForAccount(Account account);

    ValueRange<LocalDate> getTransactionOpDateRangeForAccountAndYear(Account account, Integer year);

}
