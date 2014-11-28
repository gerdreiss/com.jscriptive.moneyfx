package com.jscriptive.moneyfx.repository.mongo.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Currency;

/**
 * Created by jscriptive.com on 28/11/14.
 */
@Component
public class CurrencyWriteConverter implements Converter<Currency, String> {

    @Override
    public String convert(Currency currency) {
        return currency.getCurrencyCode();
    }
}
