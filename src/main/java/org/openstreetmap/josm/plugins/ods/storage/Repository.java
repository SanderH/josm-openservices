package org.openstreetmap.josm.plugins.ods.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Repository {
    Map<Class<?>, ObjectStore<?>> stores = new HashMap<>();

    public <T> void register(Class<T> type) {
        register(type, (String[])null);
    }

    public <T> void register(Class<T> type, String ... properties) {
        UniqueIndex<T> primaryIndex;
        if (properties != null) {
            primaryIndex = new UniqueIndexImpl<>(type, properties);
        }
        else {
            primaryIndex = new IdentityIndex<>(type);
        }
        if (!stores.containsKey(type)) {
            ObjectStore<T> store = new ObjectStore<>(this, primaryIndex);
            stores.put(type, store);
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends Object> ObjectStore<E> getStore(Class<E> type) {
        return (ObjectStore<E>) stores.get(type);
    }

    public <E extends Object> void addIndex(Class<E> type, String ... properties) {
        ObjectStore<E> store = getStore(type);
        if (store != null) {
            store.createIndex(properties);
        }
    }

    public <E extends Object> void addIndex(IndexKey<E> indexKey) {
        ObjectStore<E> store = getStore(indexKey.getBaseClass());
        if (store != null) {
            store.addIndex(indexKey);
        }
    }

    public <E extends Object> void addIndex(Class<E> type, Index<E> index) {
        ObjectStore<E> store = getStore(type);
        if (store != null) {
            store.addIndex(index);
        }
    }

    public <E extends Object> void add(E entity) {
        @SuppressWarnings("unchecked")
        Class<E> type = (Class<E>) entity.getClass();
        ObjectStore<E> store = getStore(type);
        if (store == null) {
            store = createObjectStore(type);
            stores.put(type, store);
        }
        store.add(entity);
    }

    private <E extends Object> ObjectStore<E> createObjectStore(
            Class<E> type) {
        @SuppressWarnings("unchecked")
        ObjectStore<E> store = (ObjectStore<E>) stores.get(type);
        if (store ==null) {
            store = new ObjectStore<>(this, type);
            for (Class<?> superClass : getSuperClasses(type)) {
                @SuppressWarnings("unchecked")
                ObjectStore<? super E> superStore =
                (ObjectStore<? super E>) createObjectStore(superClass);
                store.addSuperStore(superStore);
            }
        }
        return store;
    }

    <T> UniqueIndex<T> createPrimaryIndex(Class<T> type) {
        UniqueIndex<T> primaryIndex = null;
        for (Class<?> superClass : getSuperClasses(type)) {
            @SuppressWarnings("unchecked")
            ObjectStore<? super T> superStore =
            (ObjectStore<? super T>) getStore(superClass);
            if (superStore != null) {
                UniqueIndex<? super T> superIndex = superStore.getPrimaryIndex();
                if (!(superIndex instanceof IdentityIndex)) {
                    if (primaryIndex == null) {
                        primaryIndex = superIndex.forSubClass(type);
                    }
                    else {
                        throw new UndeterminedPrimaryException();
                    }
                }
            }
        }
        return (primaryIndex != null ? primaryIndex : new IdentityIndex<>(type));
    }

    public <E> Index<E> getIndex(Class<E> entityClass, String ...properties) {
        ObjectStore<E> store = getStore(entityClass);
        return store == null ? null : store.getIndex(properties);
    }

    /**
     * Get the index for the specified indexKey.
     * TODO create a non-persistent index if no persistent index is available
     *
     * @param indexKey
     * @return
     */
    public <E> Index<E> getIndex(IndexKey<E> indexKey) {
        ObjectStore<E> store = getStore(indexKey.getBaseClass());
        return store == null ? null : store.getIndex(indexKey);
    }


    public <E> Stream<? extends E> getAll(Class<E> entityClass) {
        ObjectStore<E> store = getStore(entityClass);
        if (store == null) {
            return Stream.empty();
        }
        return store.getAll(true);
    }

    public Stream<?> getAll() {
        return getAll(Object.class);
    }
    //    public Stream<Object> getAll() {
    //        @SuppressWarnings("unchecked")
    //        Stream<? extends Object>[] streams = new Stream[stores.size()];
    //        int i=0;
    //        for (ObjectStore<?> store : stores.values()) {
    //            streams[i] = store.getAll();
    //        }
    //        return Stream.of(streams).flatMap(s->s);
    //    }
    //            @Override
    //            public Iterator<Object> iterator() {
    //                List<Iterator<?>> iterators = new LinkedList<>();
    //                for (ObjectStore<?> store : stores.values()) {
    //                    iterators.add(store.getAll(false).iterator());
    //                }
    //                return new NestedIterator<>(iterators);
    //            }
    //        };
    //    }

    public <T> T getByPrimary(Class<T> type, Object value) {
        ObjectStore<T> store = getStore(type);
        return store.getByPrimary(value);
    }

    static <T> Set<Class<? super T>> getSuperClasses(Class<?> type) {
        Set<Class<? super T>> superClasses = new HashSet<>();
        for (Class<?> superClass : type.getInterfaces()) {
            @SuppressWarnings("unchecked")
            Class<? super T> superSuper = (Class<? super T>) superClass;
            superClasses.add(superSuper);
            superClasses.addAll(getSuperClasses(superClass));
        }
        @SuppressWarnings("unchecked")
        Class<? super T> superClass = (Class<? super T>) type.getSuperclass();
        if (superClass != null) {
            superClasses.add(superClass);
            superClasses.addAll(getSuperClasses(superClass));
        }
        return superClasses;
    }

    public void clear() {
        for (ObjectStore<?> store : stores.values()) {
            store.clear();
        }
    }

    public <T> Stream<T> query(Class<T> type, String property, Object value) {
        ObjectStore<T> store = getStore(type);
        if (store == null) {
            return Stream.empty();
        }
        IndexKey<T> indexKey = IndexKeyFactory.createPropertyIndexKey(type, property);
        Index<T> index = store.getIndex(indexKey);
        return index.getAll(value);
    }

    public <T> Stream<T> query(Class<T> type, String[] properties, Object[] values) {
        ObjectStore<T> store = getStore(type);
        if (store == null) {
            return Stream.empty();
        }
        Index<T> index = store.getIndex(properties);
        return index.getAll(values);
    }
}
