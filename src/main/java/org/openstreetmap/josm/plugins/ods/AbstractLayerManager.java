package org.openstreetmap.josm.plugins.ods;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.GeoEntityRepository;
import org.openstreetmap.josm.plugins.ods.osm.ManagedNodeSet;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

/**
 * 
 * @author Gertjan Idema
 * 
 */
public abstract class AbstractLayerManager implements LayerManager {
    private String name;
    private OsmDataLayer osmDataLayer;
    private Map<OsmPrimitive, ManagedPrimitive<?>> primitiveMap = new HashMap<>();
//    private Map<Long, Entity> nodeEntities = new HashMap<>();
//    private Map<Long, Entity> wayEntities = new HashMap<>();
//    private Map<Long, Entity> relationEntities = new HashMap<>();
    private GeoEntityRepository repository = new GeoEntityRepository();
//    private EntityStoreMap entityStoreMap = new EntityStoreMap();
    private boolean active = false;

    public AbstractLayerManager(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public GeoEntityRepository getRepository() {
        return repository;
    }

//    public <T extends Entity> void addEntityStore(EntityStore<T> entityStore) {
//        this.entityStoreMap.put(entityStore.getType(), entityStore);
//    }
//    
//    public <T extends Entity> void addEntityStore(Class<T> clazz, EntityStore<T> entityStore) {
//        this.entityStoreMap.put(clazz, entityStore);
//    }
    
    @Override
    public ManagedNodeSet getManagedNodes() {
        throw new UnsupportedOperationException();
    }

    public OsmDataLayer getOsmDataLayer() {
        return osmDataLayer;
    }
    
    protected OsmDataLayer createOsmDataLayer() {
        OsmDataLayer layer = new OsmDataLayer(new DataSet(), getName(), null);
        layer.setUploadDiscouraged(!isOsm());
        return layer;
    }

    public boolean isActive() {
        return this.active;
    }
    
    public void activate() {
        if (!active) {
            Layer oldLayer = null;
            if (Main.map != null) {
                oldLayer = Main.main.getActiveLayer();
            }
            osmDataLayer = createOsmDataLayer();
            Main.main.addLayer(osmDataLayer);
            if (oldLayer != null) {
                Main.map.mapView.setActiveLayer(oldLayer);
            }
            this.active = true;
        }
    }
    
//    public Collection<EntityStore<?>> getStores() {
//        return entityStoreMap.getStores();
//    }
//    
//    @Override
//    public <E extends Entity> EntityStore<E> getEntityStore(Class<E> clazz) {
//        return entityStoreMap.get(clazz);
//    }

    public void reset() {
        if (isActive()) {
            // Clear all data stores
            getRepository().clear();
//            for (EntityStore<?> store : entityStoreMap.stores.values()) {
//                store.clear();
//            }
//            nodeEntities.clear();
//            wayEntities.clear();
//            relationEntities.clear();
            this.osmDataLayer.data.clear();
            this.osmDataLayer.data.dataSources.clear();
        }
    }

    @Override
    public void deActivate() {
        if (isActive()) {
            active = false;
            this.reset();
            Main.main.removeLayer(this.osmDataLayer);
        }
    }

    @Override
    public void register(OsmPrimitive primitive,
            ManagedPrimitive<?> managedPrimitive) {
        primitiveMap.put(primitive, managedPrimitive);
    }


//    @Override
//    public void register(OsmPrimitive primitive, Entity entity) {
//        switch (primitive.getType()) {
//        case NODE:
//            nodeEntities.put(primitive.getUniqueId(), entity);
//            break;
//        case WAY:
//            wayEntities.put(primitive.getUniqueId(), entity);
//            break;
//        case RELATION:
//            relationEntities.put(primitive.getUniqueId(), entity);
//            break;
//        default:
//            break;
//        }
//    }

    @Override
    public ManagedPrimitive<?> getManagedPrimitive(OsmPrimitive primitive) {
        return primitiveMap.get(primitive);
    }
    
//    @Override
//    public Entity getEntity(OsmPrimitive primitive) {
//        switch (primitive.getType()) {
//        case NODE:
//            return nodeEntities.get(primitive.getUniqueId());
//        case WAY:
//            return wayEntities.get(primitive.getUniqueId());
//        case RELATION:
//            return relationEntities.get(primitive.getUniqueId());
//        default:
//            return null;
//        }
//    }
    
//    private class EntityStoreMap {
//        private Map<Class<?>, EntityStore<? extends Entity>> stores = new HashMap<>();
//
//        public <T extends Entity> void put(Class<T> clazz,
//                EntityStore<? extends T> store) {
//            stores.put(clazz, store);
//        }
//
//        public Collection<EntityStore<? extends Entity>> getStores() {
//            return stores.values();
//        }
//        
//        @SuppressWarnings("unchecked")
//        public <T extends Entity> EntityStore<T> get(Class<T> clazz) {
//            return (EntityStore<T>) stores.get(clazz);
//        }
//    }
}
