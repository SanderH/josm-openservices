package org.openstreetmap.josm.plugins.ods.storage;

import java.util.stream.Stream;

public class SubclassIndex<T, T2 extends T> implements Index<T2> {
    private final Index<T> superIndex;
    private final Class<T2> type;

    public SubclassIndex(Index<T> superIndex, Class<T2> type) {
        super();
        this.superIndex = superIndex;
        this.type = type;
    }

    @Override
    public Class<T2> getType() {
        return type;
    }

    @Override
    public IndexKey<? super T2> getIndexFunction() {
        return superIndex.getIndexFunction();
    }

    @Override
    public boolean isUnique() {
        return superIndex.isUnique();
    }

    @Override
    public Object getKey(T2 object) {
        return superIndex.getKey(object);
    }

    @Override
    public void insert(T2 entity) {
        superIndex.insert(entity);
    }

    @Override
    public Stream<T2> getAllByTemplate(T2 object) {
        return superIndex.getAllByTemplate(object)
                .filter(obj -> type.isInstance(obj))
                .map(obj -> type.cast(obj));
    }

    @Override
    public Stream<T2> getAll(Object object) {
        return superIndex.getAll(object)
                .filter(obj -> type.isInstance(obj))
                .map(obj -> type.cast(obj));
    }

    @Override
    public void remove(T2 entity) {
        superIndex.remove(entity);
    }

    @Override
    public void clear() {
        // No action required because this index is a read-only
        // wrapper around an other index
    }

    //    @Override
    //    public <T3 extends T2> Index<T2> filter(Class<T3> subClass) {
    //        throw new UnsupportedOperationException()
    //    }
}
