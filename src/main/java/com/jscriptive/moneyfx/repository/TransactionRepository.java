package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Account;
import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface TransactionRepository {
    List<Transaction> findByAccount(Account account);
    List<Transaction> findByCategory(Category category);
}
