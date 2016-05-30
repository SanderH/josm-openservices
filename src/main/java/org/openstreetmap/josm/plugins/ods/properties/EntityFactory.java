package org.openstreetmap.josm.plugins.ods.properties;

public interface EntityFactory<T> {
    public T newInstance();
}
