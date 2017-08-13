package org.openstreetmap.josm.plugins.ods.properties.pojo;

import org.openstreetmap.josm.plugins.ods.properties.OdsEntityType;
import org.openstreetmap.josm.plugins.ods.properties.PropertyHandler;
import org.openstreetmap.josm.tools.I18n;

public class PojoEntityType<T> implements OdsEntityType<T> {
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
        return I18n.tr("Pojo entity type ({0})", type.getSimpleName());
    }
    
    
}
