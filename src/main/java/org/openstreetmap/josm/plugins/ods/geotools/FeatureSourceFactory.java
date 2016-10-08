package org.openstreetmap.josm.plugins.ods.geotools;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.NameImpl;

public class FeatureSourceFactory {
    public static SimpleFeatureSource createFeatureSource(SimpleFeatureSource delegate, Query query) {
        if (query instanceof UniqueIdQuery) {
            String idProperty = ((UniqueIdQuery)query).getIdProperty();
            return new UniqueIdSimpleFeatureSource(new NameImpl("Dummy"), delegate, idProperty);
        }
        return delegate;
    }
}
