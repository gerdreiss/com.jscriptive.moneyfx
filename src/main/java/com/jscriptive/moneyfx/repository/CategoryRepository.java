package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Category;

import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface CategoryRepository {

    void insert(Category category);

    List<Category> findAll();

    Category findByName(String name);


}
