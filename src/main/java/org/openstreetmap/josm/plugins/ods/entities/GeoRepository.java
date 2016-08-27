package org.openstreetmap.josm.plugins.ods.entities;

public class GeoRepository extends Repository {
    public <T> void addGeoIndex(Class<T> type, String property) {
        EntityManager<T> manager = getManager(type);
        if (manager != null) {
            GeoIndex<T> geoIndex = createGeoIndex(type, property);
            manager.addIndex(geoIndex);
        }
    }

    public <T> GeoIndex<T> getGeoIndex(Class<T> type, String property) {
        Index<T> index = getManager(type).getIndex(property);
        return (GeoIndex<T>) index;
    }
    
    private static <T> GeoIndex<T> createGeoIndex(Class<T> type, String property) {
        return new GeoIndexImpl<>(type, property);
    }
}
