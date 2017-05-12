package org.openstreetmap.josm.plugins.ods.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator that iterates over a collection of iterators.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 * @param <E>
 */
public class ChainedIterator<E> implements Iterator<E> {
    private final Iterator<Iterator<? extends E>> iterators;
    private Iterator<? extends E> activeIterator;

    public ChainedIterator(List<Iterator<? extends E>> iteratorList) {
        this.iterators = iteratorList.iterator();
        if (this.iterators.hasNext()) {
            activeIterator = iterators.next();
        }
    }

    @Override
    public boolean hasNext() {
        if (activeIterator == null) return false;
        // Find first child iterator with remaining objects
        while (!activeIterator.hasNext() && iterators.hasNext()) {
            activeIterator = iterators.next();
        }
        return activeIterator.hasNext();
    }

    @Override
    public E next() {
        if (activeIterator == null) {
            throw new NoSuchElementException();
        }
        while (!activeIterator.hasNext() && iterators.hasNext()) {
            activeIterator = iterators.next();
        }
        return activeIterator.next();
    }
}
