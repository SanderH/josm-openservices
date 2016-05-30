package org.openstreetmap.josm.plugins.ods.properties;

public interface PropertySetter<T1, T2> {
    public void set(T1 obj, T2 value);
}
