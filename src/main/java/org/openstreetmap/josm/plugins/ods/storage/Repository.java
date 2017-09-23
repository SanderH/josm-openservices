package org.openstreetmap.josm.plugins.ods.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.properties.pojo.PojoUtils;
import org.openstreetmap.josm.plugins.ods.storage.query.DefaultQueryBuilder;
import org.openstreetmap.josm.plugins.ods.storage.query.Query;
import org.openstreetmap.josm.plugins.ods.storage.query.QueryExecutor;
import org.openstreetmap.josm.plugins.ods.storage.query.QueryExecutorFactory;
import org.openstreetmap.josm.plugins.ods.storage.query.QueryPredicate;
import org.openstreetmap.josm.plugins.ods.storage.query.ResultSet;
import org.openstreetmap.josm.tools.Logging;

public class Repository {
    private final ObjectStores objectStores = new ObjectStores();
    private final QueryExecutors queryExecutors = new QueryExecutors();

    public <T> void register(Class<T> type, String ... properties) {
        if (!objectStores.contains(type)) {
            UniqueIndex<T> primaryIndex;
            if (properties.length == 0) {
                primaryIndex = new IdentityIndex<>(type);
            }
            else {
                primaryIndex = new UniqueIndexImpl<>(type, properties);
            }
            ObjectStore<T> store = new ObjectStore<>(this, primaryIndex);
            objectStores.put(type, store);
        }
    }

    public <E extends Object> ObjectStore<E> getStore(Class<E> type) {
        return objectStores.get(type);
    }

    public <E extends Object> void addIndex(Class<E> type, String ... properties) {
        boolean valid = true;
        for (String property : properties) {
            valid &= PojoUtils.hasAttribute(type, property);
        }
        if (valid) {
            ObjectStore<E> store = getStore(type);
            if (store != null) {
                store.createIndex(properties);
            }
        }
        else {
            Logging.warn("Can't create index for class ''{0}'' with these properties: {1}", type, properties);
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
            objectStores.put(type, store);
        }
        store.add(entity);
    }

    private synchronized <E extends Object> ObjectStore<E> createObjectStore(
            Class<E> type) {
        ObjectStore<E> store = objectStores.get(type);
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

    //    public <E> Index<E> getIndex(Class<E> entityClass, String ...properties) {
    //        ObjectStore<E> store = getStore(entityClass);
    //        return store == null ? null : store.getIndex(properties);
    //    }

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

    //    public <E> Iterator<? extends E> iterator(Class<E> entityClass) {
    //        ObjectStore<E> store = getStore(entityClass);
    //        if (store == null) {
    //            return Collections.emptyIterator();
    //        }
    //        return store.iterator(true);
    //    }
    //
    //    public <E> Stream<? extends E> getAll(Class<E> entityClass) {
    //        ObjectStore<E> store = getStore(entityClass);
    //        if (store == null) {
    //            return Stream.empty();
    //        }
    //        return store.getAll(true);
    //    }
    //
    //    public <T> T getByPrimary(Class<T> type, Object value) {
    //        ObjectStore<T> store = getStore(type);
    //        return store.getByPrimary(value);
    //    }

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
        objectStores.clear();
        queryExecutors.clear();
    }

    //    public <T> Stream<T> query(Class<T> type, String property, Object value) {
    //        ObjectStore<T> store = getStore(type);
    //        if (store == null) {
    //            return Stream.empty();
    //        }
    //        IndexKey<T> indexKey = IndexKeyFactory.createPropertyIndexKey(type, property);
    //        Index<T> index = store.getIndex(indexKey);
    //        return index.getAll(value);
    //    }

    //    public <T> Stream<T> query(Class<T> type, String[] properties, Object[] values) {
    //        ObjectStore<T> store = getStore(type);
    //        if (store == null) {
    //            return Stream.empty();
    //        }
    //        Index<T> index = store.getIndex(properties);
    //        return index.getAll(values);
    //    }

    private static class ObjectStores {
        Map<Class<?>, ObjectStore<?>> stores = new HashMap<>();

        ObjectStores() {}

        public boolean contains(Class<?> type) {
            return stores.containsKey(type);
        }

        public void clear() {
            stores.values().forEach(s -> s.clear());
        }

        public <T> void put(Class<T> type, ObjectStore<T> store) {
            stores.put(type,  store);
        }

        @SuppressWarnings("unchecked")
        public <T> ObjectStore<T> get(Class<T> type) {
            return (ObjectStore<T>) stores.get(type);
        }
    }

    private static class QueryExecutors {
        Map<Query<?>, QueryExecutor<?>> executors = new HashMap<>();

        QueryExecutors() {}

        public void clear() {
            executors.clear();
        }

        public <T> void put(Query<T> query, QueryExecutor<T> executor) {
            executors.put(query,  executor);
        }

        @SuppressWarnings("unchecked")
        public <T> QueryExecutor<T> get(Query<T> query) {
            return (QueryExecutor<T>) executors.get(query);
        }
    }

    public <T> ResultSet<T> run(Query<T> query) {
        QueryExecutor<T> executor = queryExecutors.get(query);
        if (executor == null) {
            executor = QueryExecutorFactory.create(this, query);
            queryExecutors.put(query, executor);
        }
        return executor.run(new HashMap<String, Object>());
    }

    public Query<Object> query() {
        return query(Object.class, Query.TRUE);
    }

    public <T> Query<T> query(Class<T> type) {
        return query(type, Query.TRUE);
    }

    public <T> Query<T> query(Class<T> type, QueryPredicate predicate) {
        DefaultQueryBuilder<T> builder = new DefaultQueryBuilder<>(this, type);
        builder.setFilter(predicate);
        return builder.getQuery();
    }
}
