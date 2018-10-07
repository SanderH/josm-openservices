package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.osm.DefaultPrimitiveBuilder;

public abstract class AbstractEntityPrimitiveBuilder<E extends OdEntity> implements EntityPrimitiveBuilder<E> {
    private final DefaultPrimitiveBuilder primitiveBuilder;
    private final EntityDao<E> dao;

    public AbstractEntityPrimitiveBuilder(OdsModule module, EntityDao<E> dao) {
        super();
        LayerManager layerManager =  module.getOpenDataLayerManager();
        this.primitiveBuilder = new DefaultPrimitiveBuilder(layerManager);
        this.dao = dao;
    }

    public DefaultPrimitiveBuilder getPrimitiveFactory() {
        return primitiveBuilder;
    }

    @Override
    public void run() {
        dao.findAll().forEach(entity -> {
            if (entity.getPrimitive() == null) {
                createPrimitive(entity);
            }
        });
    }
}
