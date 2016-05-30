package org.openstreetmap.josm.plugins.ods.properties;

public interface EntityAttributeMapper<T1, T2> {
    public void map(T1 source, T2 target);
}
