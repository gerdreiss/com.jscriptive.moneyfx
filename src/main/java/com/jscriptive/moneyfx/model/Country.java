package com.jscriptive.moneyfx.model;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Created by jscriptive.com on 28/11/14.
 */
@Document
public class Country {

    @Id
    private String id;
    private Locale locale;
    private Currency currency;

    public Country() {
        this(Locale.getDefault(), Currency.getInstance(Locale.getDefault()));
    }

    public Country(Locale locale) {
        this(locale, Currency.getInstance(locale));
    }

    public Country(Locale locale, Currency currency) {
        this.locale = locale;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country)) return false;

        Country that = (Country) o;

        if (StringUtils.equals(this.getId(), that.getId())) return true;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = locale != null ? locale.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return format("Country{locale=%s, currency=%s}", locale, currency);
    }

    public static Country fromCountryCode(String code) {
        Locale locale;
        List<Locale> locales = LocaleUtils.languagesByCountry(code);
        if (CollectionUtils.isEmpty(locales)) {
            locale = new Locale.Builder().setRegion(code).build();
        } else {
            Optional<Locale> result = locales.stream().filter(l -> l.getLanguage().equalsIgnoreCase(code)).findFirst();
            if (result.isPresent()) {
                locale = result.get();
            } else {
                locale = locales.get(0);
            }
        }
        return new Country(locale);
    }
}
