package org.openstreetmap.josm.plugins.ods.geotools.util;

import org.opengis.feature.simple.SimpleFeature;

public interface AttributeParser<T> {
    public T parse(SimpleFeature feature);
}
