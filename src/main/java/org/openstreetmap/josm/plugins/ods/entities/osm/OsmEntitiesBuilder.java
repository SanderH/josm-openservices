package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.matching.OsmAddressNodeToBuildingMatcher;

public class OsmEntitiesBuilder {
    private final OdsModule module;
    private final OsmLayerManager layerManager;
    private final OsmAddressNodeToBuildingMatcher nodeToBuildingMatcher;

    public OsmEntitiesBuilder(OdsModule module, OsmLayerManager layerManager) {
        super();
        this.module = module;
        this.layerManager = layerManager;
        this.nodeToBuildingMatcher = new OsmAddressNodeToBuildingMatcher(layerManager);
    }
    
    /**
     * Build ODS entities from OSM primitives.
     * Check all primitives in the OSM layer
     * 
     */
    public void build() {
        OsmDataLayer dataLayer = layerManager.getOsmDataLayer();
        if (dataLayer == null) return;
        build(dataLayer.data.allPrimitives());
    }
    
    /**
     * Build Ods entities from the provided OSM primitives
     * 
     * @param osmPrimitives
     */
    public void build(Collection<? extends OsmPrimitive> osmPrimitives) {
        List<OsmEntityBuilder<?>> entityBuilders = module.getEntityBuilders();
        for (OsmEntityBuilder<?> builder : entityBuilders) {
            builder.initialize();
        }
        for (OsmPrimitive primitive : osmPrimitives) {
            if (!primitive.isIncomplete() && primitive.isTagged()) {
                for (OsmEntityBuilder<?> builder : entityBuilders) {
                    builder.buildOsmEntity(primitive);
                }
            }
        }
        OsmLayerManager layerManager = module.getOsmLayerManager();
        Iterable<AddressNode> iterable = layerManager.getRepository().getAll(AddressNode.class);
        iterable.forEach(nodeToBuildingMatcher::match);
    }
}
