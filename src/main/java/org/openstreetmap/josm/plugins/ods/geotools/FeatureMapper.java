package org.openstreetmap.josm.plugins.ods.geotools;

public interface FeatureMapper {
    public String mapFeatureName(String name);
    public <T> AttributeMapper<T> getAttributeMapper(String name, Class<T> clazz);
}
