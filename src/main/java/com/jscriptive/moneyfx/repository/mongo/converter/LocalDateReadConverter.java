package com.jscriptive.moneyfx.repository.mongo.converter;

import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Created by jscriptive.com on 17/11/2014.
 */
@Component
public class LocalDateReadConverter implements Converter<DBObject, LocalDate> {

    @Override
    public LocalDate convert(DBObject dbObject) {
        return LocalDate.of(
                Integer.parseInt(String.valueOf(dbObject.get("year"))),
                Integer.parseInt(String.valueOf(dbObject.get("month"))),
                Integer.parseInt(String.valueOf(dbObject.get("day"))));
    }
}
