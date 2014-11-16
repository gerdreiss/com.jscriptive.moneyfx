package com.jscriptive.moneyfx.repository;

import com.jscriptive.moneyfx.model.Entity;
import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.List;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public abstract class EntityRepository<E extends Entity> {


    public E get(ObjectId id) {
        return null;
    }

    public void persist(E entity) {
    }

    public void merge(E entity) {
    }

    public void remove(E entity) {
    }

    public List<E> find(E example) {
        return Collections.emptyList();
    }

}
