package com.jscriptive.moneyfx.repository.mongo.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Currency;

/**
 * Created by jscriptive.com on 28/11/14.
 */
@Component
public class CurrencyReadConverter implements Converter<String, Currency> {

    @Override
    public Currency convert(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }
}
