package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.GeoEntityRepository;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

public abstract class AbstractOsmEntityBuilder<T extends Entity> implements OsmEntityBuilder<T> {
    private LayerManager layerManager;
    private Class<T> baseType;
    private GeoEntityRepository repository;
//    private EntityStore<T> entityStore;
    private GeoUtil geoUtil;
    
    public AbstractOsmEntityBuilder(OdsModule module, Class<T> baseType) {
        super();
        this.geoUtil = module.getGeoUtil();
        this.layerManager = module.getOsmLayerManager();
        this.repository = layerManager.getRepository();
        this.baseType = baseType;
//        this.entityStore = layerManager.getEntityStore(baseType);
    }

    @Override
    public void initialize() {
//        if (entityStore == null) {
//            entityStore = layerManager.getEntityStore(baseType);
//        }
    }

    @Override
    public Class<T> getEntityClass() {
        return baseType;
    }

//    public EntityType<T> getEntityType() {
//        return entityType;
//    }

//    public EntityStore<T> getEntityStore() {
//        return entityStore;
//    }
//
    public GeoUtil getGeoUtil() {
        return geoUtil;
    }
    
    protected void register(ManagedPrimitive<?> primitive, T entity) {
        entity.setPrimitive(primitive);
        repository.add(entity);
        primitive.setEntity(entity);
    }
    
    /*
     * Check if the primitive is incomplete.
     * Or any of it's members in case of a relation
     */
    public static boolean isIncomplete(OsmPrimitive primitive) {
        if (OsmPrimitiveType.RELATION != primitive.getType()) {
            return primitive.isIncomplete();
        }
        if (primitive.isIncomplete()) return true;
        for (OsmPrimitive member : ((Relation)primitive).getMemberPrimitives()) {
            if (isIncomplete(member)) return true;
        }
        return false;
    }
}
