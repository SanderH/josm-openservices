package org.openstreetmap.josm.plugins.ods.storage;

import java.util.stream.Stream;

public interface Index<T> {

    public Class<T> getType();

    public IndexKey<T> getIndexFunction();

    //    public List<String> getProperties();

    public boolean isUnique();

    public Object getKey(T entity);

    public void insert(T entity);

    public Stream<T> getAllByTemplate(T t);

    public Stream<T> getAll(Object id);

    public void remove(T entity);

    public void clear();
}