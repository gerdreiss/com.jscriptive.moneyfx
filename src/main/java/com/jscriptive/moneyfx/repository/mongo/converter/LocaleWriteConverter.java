package com.jscriptive.moneyfx.repository.mongo.converter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by jscriptive.com on 28/11/14.
 */
@Component
public class LocaleWriteConverter implements Converter<Locale, DBObject> {

    @Override
    public DBObject convert(Locale locale) {
        return new BasicDBObject(2).append("language", locale.getLanguage()).append("country", locale.getCountry());
    }
}
