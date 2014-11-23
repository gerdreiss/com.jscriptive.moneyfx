package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Category;
import com.jscriptive.moneyfx.model.TransactionFilter;

import java.util.Collection;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface TransactionFilterRepository {

    Collection<TransactionFilter> findAll();

    void insert(TransactionFilter filter);

    void update(TransactionFilter filter);

    int removeByCategory(Category category);
}
