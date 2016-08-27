package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.openstreetmap.josm.plugins.ods.properties.PropertyHandler;

public class FeaturePropertyHandlerFactory {
    @SuppressWarnings("static-method")
    public PropertyHandler<SimpleFeature, String> createIDPropertyHandler() {
        return new FeatureIDPropertyHandler();
    }
    
    public <T> PropertyHandler<SimpleFeature, T> createPropertyHandler(SimpleFeatureType simpleFeatureType, Class<T> clazz, Integer index) {
        return new FeaturePropertyHandler<>(simpleFeatureType, clazz, index);
    }
    
    public <T> PropertyHandler<SimpleFeature, T> createPropertyHandler(SimpleFeatureType simpleFeatureType, Class<T> clazz, String name) {
        return new FeaturePropertyHandler<>(simpleFeatureType, clazz, name);
    }
    
    public <T> PropertyHandler<SimpleFeature, T> createPropertyHandler(SimpleFeatureType simpleFeatureType, Class<T> clazz, Name name) {
        return new FeaturePropertyHandler<>(simpleFeatureType, clazz, name);
    }
    
    public class FeaturePropertyHandler<T2> implements PropertyHandler<SimpleFeature, T2> {
        private final Class<T2> clazz;
        private int index = -1;
//        private Name sName;
        private String sName;
//        private BiConsumer<SimpleFeature, T2> setter;
//        private Function<SimpleFeature, T2> getter;
        
        public FeaturePropertyHandler(SimpleFeatureType featureType, Class<T2> clazz, Integer index) {
            super();
            this.clazz = clazz;
            if (index >= featureType.getAttributeCount()) {
                throw new IllegalArgumentException("Invalid attribute index");
            }
            this.index = index;
        }

        public FeaturePropertyHandler(SimpleFeatureType featureType, Class<T2> clazz, Name name) {
            this(featureType, clazz, featureType.indexOf(name));
        }

        public FeaturePropertyHandler(SimpleFeatureType featureType, Class<T2> clazz, String name) {
            this.clazz = clazz;
            this.sName = name;
        }

        @Override
        public Class<T2> getType() {
            return clazz;
        }

        @Override
        public void set(SimpleFeature feature, T2 value) {
            if (index >= 0) {
                feature.setAttribute(index, value);
            }
            else {
                feature.setAttribute(sName, value);
            }
        }

        @Override
        public T2 get(SimpleFeature feature) {
            if (index >= 0) {
                @SuppressWarnings("unchecked")
                T2 value = (T2) feature.getAttribute(index);
                return value;
            }
            @SuppressWarnings("unchecked")
            T2 value = (T2) feature.getAttribute(sName);
            return value;
        }
    }
    
    public static class FeatureIDPropertyHandler implements PropertyHandler<SimpleFeature, String> {
        @Override
        public void set(SimpleFeature feature, String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String get(SimpleFeature feature) {
            return feature.getID();
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    }
}
