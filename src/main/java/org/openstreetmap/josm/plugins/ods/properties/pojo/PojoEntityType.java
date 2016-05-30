package org.openstreetmap.josm.plugins.ods.properties.pojo;

import org.openstreetmap.josm.plugins.ods.properties.EntityType;
import org.openstreetmap.josm.plugins.ods.properties.PropertyHandler;

public class PojoEntityType<T> implements EntityType<T> {
    private final PojoPropertyHandlerFactory handlerFactory = new PojoPropertyHandlerFactory();
    private final Class<T> type;

    public PojoEntityType(Class<T> type) {
        super();
        this.type = type;
    }

    @Override
    public PropertyHandler<T, ?> createAttributeHandler(String attributeName) {
        return handlerFactory.createPropertyHandler(type, attributeName);
    }

    @Override
    public String toString() {
        return String.format("Pojo entity type (%s)", type.getSimpleName());
    }
    
    
}
