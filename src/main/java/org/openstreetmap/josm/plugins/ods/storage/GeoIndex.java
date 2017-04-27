package org.openstreetmap.josm.plugins.ods.storage;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

public interface GeoIndex<T> extends Index<T> {
    public List<T> intersection(Geometry geometry);
}