package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;
import org.openstreetmap.josm.plugins.ods.osm.DefaultPrimitiveBuilder;

public abstract class AbstractEntityPrimitiveBuilder<E extends OdEntity<?>> implements EntityPrimitiveBuilder<E> {
    private final DefaultPrimitiveBuilder primitiveBuilder;
    private final Class<E> clazz;

    public AbstractEntityPrimitiveBuilder(Class<E> clazz) {
        super();
        this.clazz = clazz;
        LayerManager layerManager = OpenDataServicesPlugin.INSTANCE.getActiveModule().getOpenDataLayerManager();
        this.primitiveBuilder = new DefaultPrimitiveBuilder(layerManager);
    }

    public DefaultPrimitiveBuilder getPrimitiveFactory() {
        return primitiveBuilder;
    }

    @Override
    public void run() {
        OdsModule module = OpenDataServicesPlugin.INSTANCE.getActiveModule();
        module.getRepository().query(clazz)
        .forEach(entity -> {
            if (entity.getPrimitive() == null) {
                createPrimitive(entity);
            }
        });
    }
}
