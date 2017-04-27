package org.openstreetmap.josm.plugins.ods.storage;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;

/**
 * This class implements the Set interface with an Identity hash map,
 * using reference-equality in place of object-equality when comparing keys (and values).
 * In other words, in an IdentitySet, two object o1 and o2 are considered equal
 * if and only if (o1==o2).
 * (In normal Set implementations (like HashSet) two objects o1 and o2 are considered equal 
 * if and only if (o1==null ? o2==null : o1.equals(o2)).)
 * 
 * @author Gertjan Idema
 *
 * @param <E>
 */
public class IdentitySet<E> extends AbstractSet<E> {
    private IdentityHashMap<E, E> map = new IdentityHashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }
    
    @Override
    public Object[] toArray() {
        return map.keySet().toArray();
    }

    @SuppressWarnings("hiding")
    @Override
    public <E> E[] toArray(E[] a) {
        return map.keySet().toArray(a);
    }

    @Override
    public boolean add(E e) {
        return map.put(e, e) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return map.keySet().containsAll(c);
    }

    @Override
    public void clear() {
        map.clear();
    }
}
