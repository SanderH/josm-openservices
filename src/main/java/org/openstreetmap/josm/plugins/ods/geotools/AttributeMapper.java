package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.feature.simple.SimpleFeature;

public interface AttributeMapper<T> {
    public boolean isRequired();
    public T get(SimpleFeature feature);
}
