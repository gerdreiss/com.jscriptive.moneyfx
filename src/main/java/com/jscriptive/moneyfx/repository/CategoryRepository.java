package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Category;

import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface CategoryRepository {

    List<Category> findAll();

    Category findByName(String name);

    Category save(Category category);

    void remove(Category category);

}
