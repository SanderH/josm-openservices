package org.openstreetmap.josm.plugins.ods.storage;

import java.util.Objects;
import java.util.function.Function;

public class IndexKeyImpl<T> implements IndexKey<T> {
    private final Class<T> type;
    final Function<? super T, Object> indexFunction;

    public IndexKeyImpl(Class<T> type, Function<? super T, Object> indexFunction) {
        super();
        this.type = type;
        this.indexFunction = indexFunction;
    }

    @Override
    public Class<T> getBaseClass() {
        return type;
    }

    //    @Override
    //    public Function<T, Object> getFunction() {
    //        return indexFunction;
    //    }

    @Override
    public Object getKey(T t) {
        return indexFunction.apply(t);
    }

    @Override
    public <T2 extends T> IndexKey<T2> forSubClass(Class<T2> subClass) {
        @SuppressWarnings("unchecked")
        IndexKey<T2> result = new SubClassIndexKey<>(subClass, (Function<T2, Object>) indexFunction);
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBaseClass(), indexFunction);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof IndexKey)) return false;
        IndexKeyImpl<?> other = (IndexKeyImpl<?>) obj;
        return Objects.equals(other.getBaseClass(), getBaseClass()) && Objects.equals(other.indexFunction, indexFunction);
    }

    class SubClassIndexKey<T2 extends T> extends IndexKeyImpl<T2> {

        public SubClassIndexKey(Class<T2> type, Function<T2, Object> function) {
            super(type, function);
        }

        @Override
        public <T3 extends T2> IndexKey<T3> forSubClass(Class<T3> subClass) {
            return IndexKeyImpl.this.forSubClass(subClass);
        }
    }
}
