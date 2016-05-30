package org.openstreetmap.josm.plugins.ods.entities;

import java.util.Iterator;
import java.util.stream.Stream;

public interface UniqueIndex<T> extends Index<T> {
    public T get(Object primaryId);
    public Iterator<T> iterator();
    public Stream<T> stream();
    public Iterable<T> getAll();
    public boolean contains(T entity);
}
