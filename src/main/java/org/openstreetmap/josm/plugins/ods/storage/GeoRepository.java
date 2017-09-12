package org.openstreetmap.josm.plugins.ods.storage;

import org.openstreetmap.josm.plugins.ods.storage.GeoIndexImpl.GeoIndexKey;

import com.vividsolutions.jts.geom.Geometry;

public class GeoRepository extends Repository {
    public <T> void addGeoIndex(Class<T> type, String property) {
        ObjectStore<T> manager = getStore(type);
        if (manager != null) {
            GeoIndex<T> geoIndex = createGeoIndex(type, property);
            manager.addIndex(geoIndex);
        }
    }

    public <T> GeoIndex<T> getGeoIndex(Class<T> type, String property) {
        GeoIndexKey<T> indexKey = new GeoIndexKey<>(type, property);
        Index<T> index = getStore(type).getIndex(indexKey);
        if (index != null && index instanceof GeoIndex) return (GeoIndex<T>) index;
        return null;
    }

    private static <T> GeoIndex<T> createGeoIndex(Class<T> type, String property) {
        return new GeoIndexImpl<>(type, property);
    }

    public <T> Iterable<T> queryIntersection(Class<T> type, String property, Geometry geometry) {
        GeoIndex<T> geoIndex = getGeoIndex(type, property);
        return geoIndex.intersection(geometry);
    }
}
