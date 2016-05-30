package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.osm.DefaultPrimitiveBuilder;

public abstract class AbstractEntityPrimitiveBuilder<T> implements EntityPrimitiveBuilder<T> {
    private DefaultPrimitiveBuilder primitiveBuilder;
    private Class<T> entityClass;

    public AbstractEntityPrimitiveBuilder(LayerManager layerManager, Class<T> entityClass) {
        super();
        this.primitiveBuilder = new DefaultPrimitiveBuilder(layerManager);
        this.entityClass = entityClass;
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    public DefaultPrimitiveBuilder getPrimitiveFactory() {
        return primitiveBuilder;
    }
}
