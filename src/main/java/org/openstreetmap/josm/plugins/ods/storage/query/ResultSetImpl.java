package org.openstreetmap.josm.plugins.ods.storage.query;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.storage.UniqueIndex;

public class ResultSetImpl<T> implements ResultSet<T> {
    private final List<UniqueIndex<? extends T>> indexes;
    private Predicate<T> predicate = (x -> true);

    public ResultSetImpl(List<UniqueIndex<? extends T>> indexes) {
        this.indexes = indexes;
    }

    @Override
    public ResultSet<T> filter(Predicate<T> p) {
        this.predicate = p;
        return this;
    }


    @Override
    public Stream<? extends T> stream() {
        if (indexes.size() == 1) {
            return indexes.get(0).stream().filter(predicate);
        }
        return indexes.stream().flatMap(idx -> idx.stream().filter(predicate));
    }

    @Override
    public Iterator<? extends T> iterator() {
        return stream().filter(predicate).iterator();
        //        if (indexes.size() == 1) {
        //            return indexes.get(0).iterator();
        //        }
        //        List<Iterator<? extends T>> iterators = new ArrayList<>(indexes.size());
        //        for (UniqueIndex<? extends T> index : indexes) {
        //            iterators.add(index.iterator());
        //        }
        //        @SuppressWarnings("unchecked")
        //        Iterator<? extends T> it = new IteratorChain(iterators);
        //        return it;
    }
}
