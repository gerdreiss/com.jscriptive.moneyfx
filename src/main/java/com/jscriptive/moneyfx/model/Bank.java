package com.jscriptive.moneyfx.model;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
public class Bank extends Entity {

    private static final String REPO_NAME = "banks";

    private String name;

    public Bank() {
        super(REPO_NAME);
    }

    public Bank(String name) {
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
        if (!(o instanceof Bank)) return false;
        if (!super.equals(o)) return false;

        Bank bank = (Bank) o;

        if (name != null ? !name.equals(bank.name) : bank.name != null) return false;

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
        return String.format("Bank{name='%s'}", name);
    }
}
