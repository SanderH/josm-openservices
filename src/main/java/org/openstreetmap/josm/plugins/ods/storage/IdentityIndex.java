package org.openstreetmap.josm.plugins.ods.storage;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.stream.Stream;

public class IdentityIndex<T> implements UniqueIndex<T> {
    private final Class<T> type;
    private final IdentityHashMap<T, T> map = new IdentityHashMap<>();

    public IdentityIndex(Class<T> type) {
        super();
        this.type = type;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public IndexKey<T> getIndexFunction() {
        return null;
    }

    @Override
    public <T2 extends T> UniqueIndex<T2> forSubClass(Class<T2> pType) {
        return new IdentityIndex<>(pType);
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public Object getKey(T entity) {
        return entity;
    }

    @Override
    public void insert(T entity) {
        map.put(entity, entity);
    }

    @Override
    public Stream<T> getAll(Object id) {
        T entity = map.get(id);
        if (entity == null) {
            return Stream.empty();
        }
        return Stream.of(entity);
    }

    @Override
    public Stream<T> getAllByTemplate(T t) {
        return getAll(getKey(t));
    }

    @Override
    public void remove(T entity) {
        map.remove(entity);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public T get(Object id) {
        return map.get(id);
    }

    @Override
    public Iterator<T> iterator() {
        return map.values().iterator();
    }

    @Override
    public Stream<T> stream() {
        return map.values().stream();
    }

    @Override
    public Iterable<T> getAll() {
        return map.values();
    }

    @Override
    public boolean contains(T entity) {
        return map.containsKey(entity);
    }
}
