package org.openstreetmap.josm.plugins.ods.entities;

import java.util.stream.Stream;

public interface EntityDao<T> {

    void add(T entity);

    Stream<T> getAll();

}
