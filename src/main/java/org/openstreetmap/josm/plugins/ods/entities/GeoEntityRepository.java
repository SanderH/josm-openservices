package org.openstreetmap.josm.plugins.ods.entities;

public class GeoEntityRepository extends EntityRepository {
    public <E extends Entity> void addGeoIndex(Class<E> type, String property) {
        EntityManager<E> manager = getManager(type);
        if (manager != null) {
            GeoIndex<E> geoIndex = createGeoIndex(type, property);
            manager.addIndex(geoIndex);
        }
    }

    public <E extends Entity> GeoIndex<E> getGeoIndex(Class<E> type, String property) {
        Index<E> index = getManager(type).getIndex(property);
        return (GeoIndex<E>) index;
    }
    
    private static <T extends Entity> GeoIndex<T> createGeoIndex(Class<T> type, String property) {
        return new GeoIndexImpl<>(type, property);
    }
}
