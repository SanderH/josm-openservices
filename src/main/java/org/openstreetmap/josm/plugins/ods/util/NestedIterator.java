package org.openstreetmap.josm.plugins.ods.util;

import java.util.Collections;
import java.util.Iterator;

/**
 * Iterator that iterates over a collection of iterators.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 * @param <E>
 */
public class NestedIterator<E> implements Iterator<E> {
    private final Iterator<Iterator<? extends E>> parentIterator;
    private Iterator<? extends E> childIterator;
    
    public NestedIterator(Iterable<Iterator<? extends E>> parentIterable) {
        this(parentIterable.iterator());
    }

    public NestedIterator(Iterator<Iterator<? extends E>> parentIterator) {
        super();
        this.parentIterator = parentIterator;
        this.childIterator = 
            (parentIterator.hasNext() ? parentIterator.next() : Collections.emptyIterator());
    }

    @Override
    public boolean hasNext() {
        // Find first child iterator with remaining objects
        while (!childIterator.hasNext() && parentIterator.hasNext()) {
            childIterator = parentIterator.next();
        }
        return childIterator.hasNext();
    }

    @Override
    public E next() {
        return childIterator.next();
    }
}
