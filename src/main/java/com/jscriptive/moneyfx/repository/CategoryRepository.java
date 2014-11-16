package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public interface CategoryRepository {
    List<Category> findByName(String name);
}
