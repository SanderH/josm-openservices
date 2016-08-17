package org.openstreetmap.josm.plugins.ods.entities.osm;

import org.openstreetmap.josm.data.osm.event.PrimitivesAddedEvent;
import org.openstreetmap.josm.data.osm.event.TagsChangedEvent;
import org.openstreetmap.josm.data.osm.event.WayNodesChangedEvent;
import org.openstreetmap.josm.plugins.ods.AbstractLayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.deamons.OsmLayerListener;

/**
 * The OsmLayerManager manages the layer containing the data that has been
 * down loaded from the OSM server. 
 * 
 * @author Gertjan Idema
 * 
 */
public class OsmLayerManager extends AbstractLayerManager {
    private OsmLayerListener layerListener;
    private OsmEntitiesBuilder entitiesBuilder;

    public OsmLayerManager(OdsModule module, String name) {
        super(name);
        this.entitiesBuilder = new OsmEntitiesBuilder(module, this);
        layerListener = new OsmLayerListener(this);
        startLayerListener();
    }

    @Override
    public boolean isOsm() {
        return true;
    }
    
    private void startLayerListener() {
        Thread thread = new Thread(layerListener);
        thread.start();
    }
    
    public OsmEntitiesBuilder getEntitiesBuilder() {
        return entitiesBuilder;
    }

    
    @Override
    public void primitivesAdded(PrimitivesAddedEvent event) {
        layerListener.primitivesAdded(event);
    }

    @Override
    public void tagsChanged(TagsChangedEvent event) {
        layerListener.tagsChanged(event);
    }

    @Override
    public void wayNodesChanged(WayNodesChangedEvent event) {
        layerListener.wayNodesChanged(event);
    }
}
