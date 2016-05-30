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

public class EntityRepository {
    private Map<Class<? extends Entity>, EntityManager<? extends Entity>> managers = new HashMap<>();
    
    public <T extends Entity> void register(Class<T> type) {
    }
    
    public <T extends Entity> void register(Class<T> type, String ... properties) {
        UniqueIndex<T> primaryIndex = new UniqueIndexImpl<>(type, properties);
        if (!managers.containsKey(type)) {
            EntityManager<T> manager = new EntityManager<>(type, primaryIndex);
            managers.put(type, manager);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Entity> EntityManager<E> getManager(Class<E> type) {
        return (EntityManager<E>) managers.get(type);
    }

    public <E extends Entity> void addIndex(Class<E> type, String ... properties) {
        EntityManager<E> manager = getManager(type);
        if (manager != null) {
            manager.createIndex(properties);
        }
    }
    
    public <E extends Entity> void addIndex(Class<E> type, Index<E> index) {
        EntityManager<E> manager = getManager(type);
        if (manager != null) {
            manager.addIndex(index);
        }
    }
    
    public <E extends Entity> void add(E entity) {
        @SuppressWarnings("unchecked")
        Class<E> type = (Class<E>) entity.getClass();
        EntityManager<E> manager = getManager(type);
        if (manager == null) {
            manager = createEntityManager(type);
            managers.put(type, manager);
        }
        manager.add(entity);
    }

    private <E extends Entity> EntityManager<E> createEntityManager(
            Class<E> type) {
        EntityManager<E> manager = new EntityManager<E>(type);
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

    public <E extends Entity> Iterable<E> getAll(Class<E> entityClass) {
        EntityManager<E> manager = getManager(entityClass);
        if (manager != null) {
            return manager.getAll();
        }
        return Collections.emptyList();
    }
    
    public Iterable<Entity> getAll() {
        return new Iterable<Entity>() {

            @Override
            public Iterator<Entity> iterator() {
                List<Iterator<? extends Entity>> iterators = new LinkedList<>();
                for (EntityManager<? extends Entity> manager : managers.values()) {
                    if (manager.isPrimary()) {
                        iterators.add(manager.getAll().iterator());
                    }
                }
                return new NestedIterator<Entity>(iterators);
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

    class EntityManager<E extends Entity> {
        private final Class<E> type;
        private UniqueIndex<E> primaryIndex;
        private final Map<List<String>, Index<E>> indexes = new HashMap<>();
        private final List<EntityManager<? super E>> superManagers = new LinkedList<>();
        private Geometry boundary;

        /**
         * Create a new write-only entity manager.
         * 
         * @param type
         */
        public EntityManager(Class<E> type) {
            this.type = type;
        }

        public EntityManager(Class<E> type, UniqueIndex<E> primaryIndex) {
            this.type = type;
            this.primaryIndex = primaryIndex;
            if (primaryIndex != null) {
                addIndex(primaryIndex);
            }
        }

        public Iterable<E> getAll() {
            return primaryIndex.getAll();
        }

        public void addSuperManager(EntityManager<? super E> superManager) {
            superManagers.add(superManager);
        }

        public boolean isPrimary() {
            return primaryIndex != null;
        }

        public Class<E> getType() {
            return type;
        }
        
        protected void addIndex(Index<E> index) {
            indexes.put(index.getProperties(), index);
        }
        
        public final UniqueIndex<E> createPrimaryIndex(String ... properties) {
            UniqueIndex<E> index = createUniqueIndex(properties);
            primaryIndex = index;
            return index;
        }

        
        public final UniqueIndex<E> createUniqueIndex(String ... properties) {
            UniqueIndex<E> index = new UniqueIndexImpl<>(type, properties);
            addIndex(index);
            return index;
        }

        @SafeVarargs
        public final Index<E> createIndex(String ... properties) {
            Index<E> index = new IndexImpl<>(type, properties);
            addIndex(index);
            return index;
        }

        public void add(E entity) {
            if (isPrimary()) {
                if (!contains(entity)) {
                    for (Index<E> index : indexes.values()) {
                        index.insert(entity);
                    }
                }
            }
            for (EntityManager<? super E> superManager : superManagers) {
                superManager.add(entity);
            }
        }

        public Geometry getBoundary() {
            if (boundary == null) {
                boundary = new GeometryFactory().buildGeometry(Collections.emptyList());
            }
            return boundary;
        }

        public void extendBoundary(Geometry boundary) {
            if (this.boundary == null) {
                this.boundary = boundary;
            } else {
                this.boundary = this.boundary.union(boundary);
            }
        }

        public UniqueIndex<E> getPrimaryIndex() {
            return primaryIndex;
        };

        public Iterator<E> iterator() {
            return getPrimaryIndex().iterator();
        }

        public Stream<E> stream() {
            return getPrimaryIndex().stream();
        }

        public boolean contains(E entity) {
            return getPrimaryIndex().contains(entity);
        }
        
//        public List<E> getById(Object id) {
//            return getIdIndex().getAll(id);
//        }
//
        public E getByPrimary(Object id) {
            return getPrimaryIndex().get(id);
        }

        public void remove(E entity) {
            for (Index<E> index : indexes.values()) {
                index.remove(entity);
            }
        }

        /**
         * Clear the entity store. Remove all entities
         */
        public void clear() {
            for (Index<E> index : indexes.values()) {
                index.clear();
            }
            boundary = null;
        }

        public Index<E> getIndex(String ... properties) {
            return indexes.get(Arrays.asList(properties));
        }
    }

    public <T extends Entity> Iterable<T> query(Class<T> type, String property, Object value) {
        EntityManager<T> manager = getManager(type);
        if (manager == null) {
            return Collections.emptyList();
        }
        Index<T> index = manager.getIndex(new String[] {property});
        return index.getAll(value);
    }
}
