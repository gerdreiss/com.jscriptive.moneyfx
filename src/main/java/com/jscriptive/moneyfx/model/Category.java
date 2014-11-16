package com.jscriptive.moneyfx.model;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
public class Category extends Entity {

    public static final Category DEFAULT = new Category("default");

    private static final String REPO_NAME = "categories";

    private String name;

    public Category() {
        super(REPO_NAME);
    }

    public Category(String name) {
        this();
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        if (!super.equals(o)) return false;

        Category category = (Category) o;

        if (name != null ? !name.equals(category.name) : category.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Category{name='%s'}", name);
    }
}
