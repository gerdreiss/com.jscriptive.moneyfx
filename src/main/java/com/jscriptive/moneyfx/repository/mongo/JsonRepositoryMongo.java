package com.jscriptive.moneyfx.repository.mongo;

import com.jscriptive.moneyfx.repository.JsonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jscriptive.com on 06/12/14.
 */
@Repository
public class JsonRepositoryMongo implements JsonRepository {

    @Autowired
    protected MongoTemplate mongoTemplate;


    @Override
    public Map<String, List<String>> extractAll() {
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        Map<String, List<String>> data = new HashMap<>(collectionNames.size());
        for (String collection : collectionNames) {
            data.put(collection, mongoTemplate.find(null, String.class, collection));
        }
        return data;
    }
}
