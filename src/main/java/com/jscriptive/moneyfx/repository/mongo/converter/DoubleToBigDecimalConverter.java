package com.jscriptive.moneyfx.repository.mongo.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.jscriptive.moneyfx.util.BigDecimalUtils.CURRENCY_CONTEXT;

/**
 * Created by jscriptive.com on 21/11/14.
 */
@Component
public class DoubleToBigDecimalConverter implements Converter<Double, BigDecimal> {

    @Override
    public BigDecimal convert(Double source) {
        return new BigDecimal(source, CURRENCY_CONTEXT);
    }
}