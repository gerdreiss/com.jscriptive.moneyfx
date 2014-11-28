package com.jscriptive.moneyfx.repository.mongo.converter;

import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by jscriptive.com on 28/11/14.
 */
@Component
public class LocaleReadConverter implements Converter<DBObject, Locale> {

    @Override
    public Locale convert(DBObject object) {
        return new Locale(
                String.valueOf(object.get("language")),
                String.valueOf(object.get("country")));
    }
}
