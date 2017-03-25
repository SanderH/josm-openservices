package org.openstreetmap.josm.plugins.ods;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.DataSet.UploadPolicy;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.event.AbstractDatasetChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataSetListener;
import org.openstreetmap.josm.data.osm.event.NodeMovedEvent;
import org.openstreetmap.josm.data.osm.event.PrimitivesAddedEvent;
import org.openstreetmap.josm.data.osm.event.PrimitivesRemovedEvent;
import org.openstreetmap.josm.data.osm.event.RelationMembersChangedEvent;
import org.openstreetmap.josm.data.osm.event.TagsChangedEvent;
import org.openstreetmap.josm.data.osm.event.WayNodesChangedEvent;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.GeoRepository;
import org.openstreetmap.josm.plugins.ods.osm.ManagedNodeSet;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

/**
 * 
 * @author Gertjan Idema
 * 
 */
public abstract class AbstractLayerManager
        implements LayerManager, DataSetListener {
    private String name;
    private OsmDataLayer osmDataLayer;
    private Map<OsmPrimitive, ManagedPrimitive> primitiveMap = new HashMap<>();
    private GeoRepository repository = new GeoRepository();
    private boolean active = false;

    public AbstractLayerManager(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public GeoRepository getRepository() {
        return repository;
    }

    @Override
    public ManagedNodeSet getManagedNodes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OsmDataLayer getOsmDataLayer() {
        return osmDataLayer;
    }

    protected OsmDataLayer createOsmDataLayer() {
        DataSet dataSet = new DataSet();
        if (!isOsm()) {
            dataSet.setUploadPolicy(UploadPolicy.BLOCKED);
        }
        OsmDataLayer layer = new OsmDataLayer(dataSet, getName(), null);
        dataSet.addDataSetListener(this);
        return layer;
    }

    public boolean isActive() {
        return this.active;
    }

    public void activate() {
        if (!active) {
            Layer oldLayer = null;
            if (Main.map != null) {
                oldLayer = Main.getLayerManager().getActiveLayer();
            }
            osmDataLayer = createOsmDataLayer();
            Main.getLayerManager().addLayer(osmDataLayer);
            if (oldLayer != null) {
                Main.getLayerManager().setActiveLayer(oldLayer);
            }
            this.active = true;
        }
    }

    @Override
    public void reset() {
        if (isActive()) {
            primitiveMap.clear();
            getRepository().clear();
            deActivate();
            activate();
//            this.osmDataLayer.data.clear();
//            this.osmDataLayer.data.getDataSources().clear();
//            if (!Main.getLayerManager().containsLayer(osmDataLayer)) {
//                Main.getLayerManager().addLayer(osmDataLayer);
//            }
        }
    }

    @Override
    public void deActivate() {
        if (isActive()) {
            active = false;
            this.reset();
            Main.getLayerManager().removeLayer(this.osmDataLayer);
        }
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

    // Implement DataSetListener.
    // The default is to ignore all events

    @Override
    public void primitivesAdded(PrimitivesAddedEvent event) {
        // ignore
    }

    @Override
    public void primitivesRemoved(PrimitivesRemovedEvent event) {
        // ignore
    }

    @Override
    public void tagsChanged(TagsChangedEvent event) {
        // ignore
    }

    @Override
    public void nodeMoved(NodeMovedEvent event) {
        // ignore
    }

    @Override
    public void wayNodesChanged(WayNodesChangedEvent event) {
        // ignore
    }

    @Override
    public void relationMembersChanged(RelationMembersChangedEvent event) {
        // ignore
    }

    @Override
    public void otherDatasetChange(AbstractDatasetChangedEvent event) {
        // ignore
    }

    @Override
    public void dataChanged(DataChangedEvent event) {
        // ignore
    }
}
