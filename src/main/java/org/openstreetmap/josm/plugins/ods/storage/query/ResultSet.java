package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ResultSet<T> {
    public Stream<? extends T> stream();

    public Iterator<? extends T> iterator();

    @SuppressWarnings("unchecked")
    public static <T1> ResultSet<T1> emptyResultSet() {
        return (ResultSet<T1>) EmptyResultSet.EMPTY_RESULTSET;
    }

    static class EmptyResultSet<T1> implements ResultSet<T1> {
        public static ResultSet<Object> EMPTY_RESULTSET = new EmptyResultSet<>();

        @Override
        public Stream<T1> stream() {
            return Stream.empty();
        }

        @Override
        public Iterator<T1> iterator() {
            return Collections.<T1> emptyIterator();
        }

        @Override
        public ResultSet<T1> filter(Predicate<T1> predicate) {
            return this;
        }
    }

    public ResultSet<T> filter(Predicate<T> predicate);
}
