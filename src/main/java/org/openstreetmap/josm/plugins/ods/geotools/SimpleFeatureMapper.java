package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.HashMap;
import java.util.Map;

public class SimpleFeatureMapper implements FeatureMapper {
    Map<Attribute<?>, AttributeMapper<?>> attrMap = new HashMap<>();
    
    public SimpleFeatureMapper() {
        super();
    }

    @Override
    public String mapFeatureName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> void addAttributeMapper(String name, Class<T> clazz, AttributeMapper<T> mapper) {
        attrMap.put(new Attribute<>(name, clazz), mapper);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> AttributeMapper<T> getAttributeMapper(String name, Class<T> clazz) {
        return (AttributeMapper<T>) attrMap.get(new Attribute<>(name, clazz));
    }
}
