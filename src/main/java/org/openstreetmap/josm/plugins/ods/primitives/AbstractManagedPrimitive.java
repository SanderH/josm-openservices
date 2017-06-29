package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.NodeData;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public abstract class AbstractManagedPrimitive implements ManagedPrimitive {
    private OsmPrimitive primitive = null;
    private final LayerManager layerManager;
    //    private Set<ManagedPrimitive<?>> referrers;
    private final long uniqueId = (new NodeData()).getUniqueId();
    private Map<String, String> keys;
    private Entity entity = null;

    public AbstractManagedPrimitive(LayerManager layerManager) {
        this(layerManager, new HashMap<>());
    }

    public AbstractManagedPrimitive(LayerManager layerManager, Map<String, String> keys) {
        super();
        this.layerManager = layerManager;
        this.keys = (keys == null ? new HashMap<>() : keys);
    }

    public AbstractManagedPrimitive(LayerManager layerManager, OsmPrimitive primitive) {
        this.layerManager = layerManager;
        assert primitive != null;
        this.primitive = primitive;
    }

    @Override
    public LayerManager getLayerManager() {
        return layerManager;
    }

    @Override
    public Collection<ManagedPrimitive> getReferrers() {
        Set<ManagedPrimitive> referrers = new HashSet<>();
        for (OsmPrimitive osm : getPrimitive().getReferrers()) {
            ManagedPrimitive ods = layerManager.getManagedPrimitive(osm);
            if (ods != null) {
                referrers.add(ods);
            }
        }
        return referrers;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public void setPrimitive(OsmPrimitive primitive) {
        this.primitive = primitive;
    }

    @Override
    public OsmPrimitive getPrimitive() {
        return primitive;
    }


    @Override
    public boolean contains(ManagedNode mNode) {
        return false;
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
    public Command putAll(Map<String, String> tags) {
        OsmPrimitive osm = getPrimitive();

        if (osm != null) {
            return new ChangePropertyCommand(Collections.singleton(osm), tags);
        }
        return null;
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
    public LatLon getCenter() {
        return getBBox().getCenter();
    }

    @Override
    public Command put(String key, String value) {
        OsmPrimitive osm = getPrimitive();
        if (osm != null) {
            return new ChangePropertyCommand(osm, key, value);
        }
        return null;
    }

    @Override
    public String get(String key) {
        return getKeys().get(key);
    }

    @Override
    public void geometryChanged() {
        // Default implementation. No action required
    }
}
