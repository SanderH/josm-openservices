package org.openstreetmap.josm.plugins.ods.properties;

public interface AttributeMapper<T1, T2> {
    public void map(T1 source, T2 target);
}
