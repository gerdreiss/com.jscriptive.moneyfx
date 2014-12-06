package com.jscriptive.moneyfx.repository.mongo.converter;

import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by jscriptive.com on 06/12/14.
 */
@Component
class DBObjectToStringConverter implements Converter<DBObject, String> {

    @Override
    public String convert(DBObject source) {
        return source == null ? null : source.toString();
    }
}
