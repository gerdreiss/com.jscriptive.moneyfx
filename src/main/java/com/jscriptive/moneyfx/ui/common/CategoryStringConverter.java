package com.jscriptive.moneyfx.ui.common;

import com.jscriptive.moneyfx.model.Category;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Created by jscriptive.com on 20/11/2014.
 */
public class CategoryStringConverter extends StringConverter<Category> {
    private final List<Category> categories;

    public CategoryStringConverter(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString(Category object) {
        if (object == null) {
            return "All categories";
        }
        return object.getName();
    }

    @Override
    public Category fromString(String string) {
        if ("All categories".equals(string)) {
            return null;
        }
        return categories.stream().filter(category -> category.getName().equals(string)).findFirst().get();
    }
}
