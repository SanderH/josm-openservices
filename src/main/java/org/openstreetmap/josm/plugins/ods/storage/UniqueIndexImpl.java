package org.openstreetmap.josm.plugins.ods.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class UniqueIndexImpl<T> implements UniqueIndex<T> {
    private final Map<Object, T> map = new HashMap<>();
    private final Class<T> clazz;
    //    private final List<String> properties;
    //    private Method[] getters;
    //    private String[] properties;
    private IndexKey<T> indexKey;

    public UniqueIndexImpl(Class<T> clazz, String ... properties) {
        this(clazz, IndexKeyFactory.createPropertyIndexKey(
                clazz, properties));
    }

    public UniqueIndexImpl(Class<T> clazz,
            IndexKey<T> indexFunction) {
        this.clazz = clazz;
        this.indexKey = indexFunction;
    }


    @Override
    public Class<T> getType() {
        return clazz;
    }

    @Override
    public IndexKey<T> getIndexFunction() {
        return indexKey;
    }

    //    @Override
    //    public List<String> getProperties() {
    //        return properties;
    //    }
    //
    @Override
    public boolean isUnique() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#add(T)
     * TODO throw Exception for duplicate keys
     */
    @Override
    public void insert(T entity) {
        Object key = getKey(entity);
        if (key != null) {
            T existing = get(key);
            if (existing == null) {
                map.put(key, entity);
            }
        }
    }

    @Override
    public boolean contains(T entity) {
        return map.containsKey(getKey(entity));
    }

    //    private Method[] createGetters() {
    //        Method[] getters = new Method[properties.length];
    //        try {
    //            for (int i=0; i< properties.length; i++) {
    //                getters[i] = clazz.getMethod(getGetterName(i));
    //            }
    //            return getters;
    //        } catch (NoSuchMethodException | SecurityException e) {
    //            e.printStackTrace();
    //            throw new RuntimeException();
    //        }
    //    }
    //
    @Override
    public Iterator<T> iterator() {
        return map.values().iterator();
    }

    @Override
    public Stream<T> stream() {
        return map.values().stream();
    }

    //    public T get(T entity) {
    //        return get(getKey(entity));
    //    }
    //
    @Override
    public T get(Object key) {
        return map.get(key);
    }

    @Override
    public Iterable<T> getAll() {
        return map.values();
    }

    @Override
    public Stream<T> getAll(Object key) {
        T result = map.get(key);
        if (result == null) {
            return Stream.empty();
        }
        return Stream.of(result);
    }

    @Override
    public void remove(T entity) {
        map.remove(getKey(entity));
    }

    //    public void removeByKey(Object key) {
    //        map.remove(key);
    //    }
    //

    @Override
    public Object getKey(T t) {
        return (indexKey != null ? indexKey.getKey(t) : t);
    }

    @Override
    public <T2 extends T> UniqueIndex<T2> forSubClass(Class<T2> type) {
        return new UniqueIndexImpl<>(type, indexKey.forSubClass(type));
    }

    @Override
    public void clear() {
        map.clear();
    }
}
