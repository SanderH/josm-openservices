package org.openstreetmap.josm.plugins.ods.entities;

public interface EntityPrimitiveBuilder<E extends OdEntity> extends Runnable {
    //    public T getEntityType();
    public void createPrimitive(E entity);
}
