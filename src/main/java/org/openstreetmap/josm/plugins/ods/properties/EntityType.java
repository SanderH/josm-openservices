package org.openstreetmap.josm.plugins.ods.properties;

public interface EntityType<T> {

    PropertyHandler<T, ?> createAttributeHandler(
            String attributeName);

}
