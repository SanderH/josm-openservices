package org.openstreetmap.josm.plugins.ods.properties;

public interface OdsEntityType<T> {

    PropertyHandler<T, ?> createAttributeHandler(
            String attributeName);

}
