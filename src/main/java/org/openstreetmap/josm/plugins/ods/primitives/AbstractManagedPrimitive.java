package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.NodeData;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public abstract class AbstractManagedPrimitive<P extends OsmPrimitive> implements ManagedPrimitive<P> {
    private P primitive = null;
    private long uniqueId = (new NodeData()).getUniqueId();
    private Map<String, String> keys;
    private Entity entity = null;
    
    public AbstractManagedPrimitive() {
        this(new HashMap<>());
    }

    public AbstractManagedPrimitive(Map<String, String> keys) {
        super();
        this.keys = (keys == null ? new HashMap<>() : keys);
    }

    public AbstractManagedPrimitive(P primitive) {
        assert primitive != null;
        this.primitive = primitive;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    public void setPrimitive(P primitive) {
        this.primitive = primitive;
    }

    @Override
    public P getPrimitive() {
        return primitive;
    }

    @Override
    public Map<String, String> getKeys() {
        OsmPrimitive osm = getPrimitive();
        if (osm != null) {
            return osm.getKeys();
        }
        return keys;
    }

    @Override
    public void putAll(Map<String, String> tags) {
        OsmPrimitive osm = getPrimitive();
        if (osm != null) {
            for (Entry<String, String> entry : tags.entrySet()) {
                osm.put(entry.getKey(), entry.getValue());
            }
        }
        else {
            keys.putAll(tags);
        }
    }

    @Override
    public void remove(String key) {
        OsmPrimitive osm = getPrimitive();
        if (osm != null) {
            osm.remove(key);
        }
        else {
            keys.remove(key);
        }
    }

    @Override
    public Long getUniqueId() {
        if (getPrimitive() != null) {
            return getPrimitive().getUniqueId();
        }
        return uniqueId;
    }

    
    @Override
    public <E extends Entity> void setEntity(E entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

//    @Override
//    public Map<String, String> getKeys() {
//        if (getPrimitive() != null) {
//            return getPrimitive().getKeys();
//        }
//        return keys;
//    }

//    @Override
//    public void setKeys(Map<String, String> keys) {
//        if (getPrimitive() != null) {
//            primitive.setKeys(keys);
//        }
//        else {
//            this.keys = keys;
//        }
//    }

    @Override
    public void put(String key, String value) {
        P osm = getPrimitive();
        if (osm != null) {
            osm.put(key, value);
        }
        else {
            keys.put(key, value);
        }
    }

    @Override
    public String get(String key) {
        return getKeys().get(key);
    }

//    @Override
//    public void remove(String key) {
//        getKeys().remove(key);
//    }
//
//    @Override
//    public boolean hasKeys() {
//        return !getKeys().isEmpty();
//    }
//
//    @Override
//    public Collection<String> keySet() {
//        return getKeys().keySet();
//    }
//
//    @Override
//    public void removeAll() {
//        getKeys().clear();
//    }
}
