package org.openstreetmap.josm.plugins.ods.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class IndexImpl<T> implements Index<T> {
    private final Class<T> clazz;
    private final Map<Object, IdentitySet<T>> map = new HashMap<>();
    private final IndexKey<? super T> indexKey;

    public IndexImpl(IndexKey<T> indexFunction) {
        super();
        this.indexKey = indexFunction;
        this.clazz = indexFunction.getBaseClass();
    }

    public IndexImpl(Class<T> clazz, IndexKey<? super T> indexFunction) {
        super();
        this.clazz = clazz;
        this.indexKey = indexFunction;
    }

    @SafeVarargs
    public IndexImpl(Class<T> clazz, String ... properties) {
        super();
        this.clazz = clazz;
        this.indexKey = IndexKeyFactory.createPropertyIndexKey(clazz, properties);
    }

    @Override
    public IndexKey<? super T> getIndexFunction() {
        return indexKey;
    }

    @Override
    public Class<T> getType() {
        return clazz;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#add(T)
     */
    @Override
    public void insert(T entity) {
        Object key = getKey(entity);
        if (key != null) {
            IdentitySet<T> set = map.get(key);
            if (set == null) {
                set = new IdentitySet<>();
                map.put(key, set);
            }
            set.add(entity);
        }
    }


    @Override
    public Stream<T> getAllByTemplate(T t) {
        Object key = getKey(t);
        return getAll(key);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.entities.Index#get(U)
     */
    @Override
    public Stream<T> getAll(Object key) {
        Set<T> result = map.get(key);
        if (result == null) {
            return Stream.empty();
        }
        return result.stream();
    }

    @Override
    public void remove(T entity) {
        map.remove(getKey(entity));
    }

    @Override
    public Object getKey(T entity) {
        return indexKey.getKey(entity);
    }

    @Override
    public void clear() {
        map.clear();
    }

    //    @Override
    //    public <T2 extends T> Index<T2> filter(Class<T2> subClass) {
    //        return new SubclassIndex<>(this, subClass);
    //    }
}
