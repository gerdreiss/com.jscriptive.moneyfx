package com.jscriptive.moneyfx.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
@Document
public class Category {

    public static final Category OTHER = new Category("Other");

    @Id
    private String id;
    @Indexed
    private String name;
    @DBRef
    private TransactionFilter filterRule;

    public Category() {
    }

    public Category(String name) {
        setName(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransactionFilter getFilterRule() {
        return filterRule;
    }

    public void setFilterRule(TransactionFilter filterRule) {
        this.filterRule = filterRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category category = (Category) o;

        if (filterRule != null ? !filterRule.equals(category.filterRule) : category.filterRule != null) return false;
        //noinspection RedundantIfStatement
        if (name != null ? !name.equals(category.name) : category.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (filterRule != null ? filterRule.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Category{name='%s', filterRule=%s}", name, filterRule);
    }
}
