package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.openstreetmap.josm.plugins.ods.properties.PropertyHandler;

public class FeaturePropertyHandlerFactory {
    public PropertyHandler<SimpleFeature, String> createIDPropertyHandler() {
        return new FeatureIDPropertyHandler();
    }
    
    public <T> PropertyHandler<SimpleFeature, T> createPropertyHandler(SimpleFeatureType simpleFeatureType, Class<T> clazz, Integer index) {
        return new FeaturePropertyHandler<T>(simpleFeatureType, clazz, index);
    }
    
    public <T> PropertyHandler<SimpleFeature, T> createPropertyHandler(SimpleFeatureType simpleFeatureType, Class<T> clazz, String name) {
        return new FeaturePropertyHandler<T>(simpleFeatureType, clazz, name);
    }
    
    public <T> PropertyHandler<SimpleFeature, T> createPropertyHandler(SimpleFeatureType simpleFeatureType, Class<T> clazz, Name name) {
        return new FeaturePropertyHandler<T>(simpleFeatureType, clazz, name);
    }
    
    public static class FeaturePropertyHandler<T2> implements PropertyHandler<SimpleFeature, T2> {
        private final Class<T2> clazz;
        private int index;
//        private Name name;
//        private String s;
        private BiConsumer<SimpleFeature, T2> setter;
        private Function<SimpleFeature, T2> getter;
        
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
            this(featureType, clazz, featureType.indexOf(name));
        }

        @Override
        public Class<T2> getType() {
            return clazz;
        }

        @Override
        public void set(SimpleFeature feature, T2 value) {
            if (setter != null) {
                setter.accept(feature, value);
            }
            feature.setAttribute(index, value);
        }

        @Override
        public T2 get(SimpleFeature feature) {
            if (getter != null) {
                return getter.apply(feature);
            }
            @SuppressWarnings("unchecked")
            T2 value = (T2) feature.getAttribute(index);
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
