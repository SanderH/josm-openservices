package org.openstreetmap.josm.plugins.ods.test;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.osm.ManagedNodeSet;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.storage.GeoRepository;

public class TestLayerManager implements LayerManager {
    private final OsmDataLayer dataLayer;

    public TestLayerManager(OsmDataLayer dataLayer) {
        this.dataLayer = dataLayer;
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
    public OsmDataLayer getOsmDataLayer() {
        return dataLayer;
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
        // TODO Auto-generated method stub

    }

    @Override
    public ManagedPrimitive getManagedPrimitive(OsmPrimitive primitive) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GeoRepository getRepository() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ManagedNodeSet getManagedNodes() {
        // TODO Auto-generated method stub
        return null;
    }
}
