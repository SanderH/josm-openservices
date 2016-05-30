package org.openstreetmap.josm.plugins.ods.entities;

public interface EntityPrimitiveBuilder<T> {
    public Class<T> getEntityClass();
    public void createPrimitive(T entity);
}
