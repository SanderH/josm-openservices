package org.openstreetmap.josm.plugins.ods;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.osm.ManagedNodeSet;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.storage.GeoRepository;

public class TestLayerManager implements LayerManager, LayerChangeListener {
    private final OsmDataLayer osmDataLayer;
    private final Map<OsmPrimitive, ManagedPrimitive> primitiveMap = new HashMap<>();
    private final GeoRepository repository = new GeoRepository();

    public TestLayerManager(OsmDataLayer osmDataLayer) {
        super();
        this.osmDataLayer = osmDataLayer;
    }

    @Override
    public GeoRepository getRepository() {
        return repository;
    }

    @Override
    public void layerAdded(LayerAddEvent e) {
        // No action required
    }

    @Override
    public void layerRemoving(LayerRemoveEvent e) {
        // No action required
    }

    @Override
    public void layerOrderChanged(LayerOrderChangeEvent e) {
        // No action required
    }

    @Override
    public OsmDataLayer getOsmDataLayer() {
        return osmDataLayer;
    }

    @Override
    public boolean isOsm() {
        return true;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void deActivate() {
        // TODO Auto-generated method stub
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    }

    @Override
    public void register(OsmPrimitive primitive,
            ManagedPrimitive managedPrimitive) {
        primitiveMap.put(primitive, managedPrimitive);
    }

    @Override
    public ManagedPrimitive getManagedPrimitive(OsmPrimitive primitive) {
        return primitiveMap.get(primitive);
    }


    @Override
    public ManagedNodeSet getManagedNodes() {
        // TODO Auto-generated method stub
        return null;
    }


}
