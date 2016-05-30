package org.openstreetmap.josm.plugins.ods.properties;

public interface AttributeMapper<T1, T2, T3> {
    public void map(T1 source, T2 target);
}
