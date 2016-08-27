package org.openstreetmap.josm.plugins.ods.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.util.NestedIterator;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class Repository {
    Map<Class<?>, EntityManager<?>> managers = new HashMap<>();
    
//    public <T> void register(Class<T> type) {
//    }
    
    public <T> void register(Class<T> type, String ... properties) {
        UniqueIndex<T> primaryIndex = new UniqueIndexImpl<>(type, properties);
        if (!managers.containsKey(type)) {
            EntityManager<T> manager = new EntityManager<>(type, primaryIndex);
            managers.put(type, manager);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Object> EntityManager<E> getManager(Class<E> type) {
        return (EntityManager<E>) managers.get(type);
    }

    public <E extends Object> void addIndex(Class<E> type, String ... properties) {
        EntityManager<E> manager = getManager(type);
        if (manager != null) {
            manager.createIndex(properties);
        }
    }
    
    public <E extends Object> void addIndex(Class<E> type, Index<E> index) {
        EntityManager<E> manager = getManager(type);
        if (manager != null) {
            manager.addIndex(index);
        }
    }
    
    public <E extends Object> void add(E entity) {
        @SuppressWarnings("unchecked")
        Class<E> type = (Class<E>) entity.getClass();
        EntityManager<E> manager = getManager(type);
        if (manager == null) {
            manager = createEntityManager(type);
            managers.put(type, manager);
        }
        manager.add(entity);
    }

    private <E extends Object> EntityManager<E> createEntityManager(
            Class<E> type) {
        EntityManager<E> manager = new EntityManager<>(type);
        for (Class<?> superClass : getSuperClasses(type)) {
            @SuppressWarnings("unchecked")
            EntityManager<? super E> superManager = (EntityManager<? super E>) getUntypedManager(superClass);
            if (superManager != null && superManager.isPrimary()) {
                manager.addSuperManager(superManager);
            }
        }
        return manager;
    }

    private EntityManager<?> getUntypedManager(Class<?> type) {
        return managers.get(type);
    }

    public <E extends Object> Iterable<E> getAll(Class<E> entityClass) {
        EntityManager<E> manager = getManager(entityClass);
        if (manager != null) {
            return manager.getAll();
        }
        return Collections.emptyList();
    }
    
    public Iterable<Object> getAll() {
        return new Iterable<Object>() {

            @Override
            public Iterator<Object> iterator() {
                List<Iterator<?>> iterators = new LinkedList<>();
                for (EntityManager<?> manager : managers.values()) {
                    if (manager.isPrimary()) {
                        iterators.add(manager.getAll().iterator());
                    }
                }
                return new NestedIterator<>(iterators);
            }
        };
    }
    
    private Set<Class<?>> getSuperClasses(Class<?> type) {
        Set<Class<?>> superClasses = new HashSet<>();
        for (Class<?> superClass : type.getInterfaces()) {
            superClasses.add(superClass);
        }
        if (type.getSuperclass() != Object.class) {
            superClasses.addAll(getSuperClasses(type.getSuperclass()));
        }
        return superClasses;
    }

    public void clear() {
        for (EntityManager<?> manager : managers.values()) {
            manager.clear();
        }
    }

    class EntityManager<T> {
        private final Class<T> type;
        private UniqueIndex<T> primaryIndex;
        private final Map<List<String>, Index<T>> indexes = new HashMap<>();
        private final List<EntityManager<? super T>> superManagers = new LinkedList<>();
        private Geometry boundary;

        /**
         * Create a new write-only entity manager.
         * 
         * @param type
         */
        public EntityManager(Class<T> type) {
            this.type = type;
        }

        public EntityManager(Class<T> type, UniqueIndex<T> primaryIndex) {
            this.type = type;
            this.primaryIndex = primaryIndex;
            if (primaryIndex != null) {
                addIndex(primaryIndex);
            }
        }

        public Iterable<T> getAll() {
            return primaryIndex.getAll();
        }

        public void addSuperManager(EntityManager<? super T> superManager) {
            superManagers.add(superManager);
        }

        public boolean isPrimary() {
            return primaryIndex != null;
        }

        public Class<T> getType() {
            return type;
        }
        
        protected void addIndex(Index<T> index) {
            indexes.put(index.getProperties(), index);
        }
        
        public final UniqueIndex<T> createPrimaryIndex(String ... properties) {
            UniqueIndex<T> index = createUniqueIndex(properties);
            primaryIndex = index;
            return index;
        }

        
        public final UniqueIndex<T> createUniqueIndex(String ... properties) {
            UniqueIndex<T> index = new UniqueIndexImpl<>(type, properties);
            addIndex(index);
            return index;
        }

        @SafeVarargs
        public final Index<T> createIndex(String ... properties) {
            Index<T> index = new IndexImpl<>(type, properties);
            addIndex(index);
            return index;
        }

        public void add(T entity) {
            if (isPrimary()) {
                if (!contains(entity)) {
                    for (Index<T> index : indexes.values()) {
                        index.insert(entity);
                    }
                }
            }
            for (EntityManager<? super T> superManager : superManagers) {
                superManager.add(entity);
            }
        }

        public Geometry getBoundary() {
            if (boundary == null) {
                boundary = new GeometryFactory().buildGeometry(Collections.emptyList());
            }
            return boundary;
        }

        public void extendBoundary(Geometry bounds) {
            if (this.boundary == null) {
                this.boundary = bounds;
            } else {
                this.boundary = this.boundary.union(bounds);
            }
        }

        public UniqueIndex<T> getPrimaryIndex() {
            return primaryIndex;
        }

        public Iterator<T> iterator() {
            return getPrimaryIndex().iterator();
        }

        public Stream<T> stream() {
            return getPrimaryIndex().stream();
        }

        public boolean contains(T entity) {
            return getPrimaryIndex().contains(entity);
        }
        
//        public List<E> getById(Object id) {
//            return getIdIndex().getAll(id);
//        }
//
        public T getByPrimary(Object id) {
            return getPrimaryIndex().get(id);
        }

        public void remove(T entity) {
            for (Index<T> index : indexes.values()) {
                index.remove(entity);
            }
        }

        /**
         * Clear the entity store. Remove all entities
         */
        public void clear() {
            for (Index<?> index : indexes.values()) {
                index.clear();
            }
            boundary = null;
        }

        public Index<T> getIndex(String ... properties) {
            return indexes.get(Arrays.asList(properties));
        }
    }

    public <T> Iterable<T> query(Class<T> type, String property, Object value) {
        EntityManager<T> manager = getManager(type);
        if (manager == null) {
            return Collections.emptyList();
        }
        Index<T> index = manager.getIndex(new String[] {property});
        return index.getAll(value);
    }
}
