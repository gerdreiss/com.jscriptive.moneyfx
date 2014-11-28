package com.jscriptive.moneyfx.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import static java.lang.String.format;

/**
 * Created by jscriptive.com on 29/10/2014.
 */
@Document
public class Bank {

    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    @DBRef
    private Country country;

    public Bank() {
    }

    public Bank(String name, Country country) {
        setName(name);
        setCountry(country);
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

    private void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public String getCountryCode() {
        return country == null ? null : country.getLocale().getCountry();
    }

    public String getCurrencyCode() {
        return country == null ? null : country.getCurrency().getCurrencyCode();
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bank)) return false;

        Bank bank = (Bank) o;

        if (country != null ? !country.equals(bank.country) : bank.country != null) return false;
        if (name != null ? !name.equals(bank.name) : bank.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return format("Bank{name='%s', country=%s}", name, country);
    }
}
