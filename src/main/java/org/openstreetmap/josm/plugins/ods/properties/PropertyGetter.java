package org.openstreetmap.josm.plugins.ods.properties;

public interface PropertyGetter<T1, T2> {
    public T2 get(T1 obj);
}
