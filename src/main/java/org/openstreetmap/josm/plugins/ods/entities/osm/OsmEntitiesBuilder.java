package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.event.TagsChangedEvent;
import org.openstreetmap.josm.data.osm.event.WayNodesChangedEvent;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.matching.OsmBuildingToAddressNodesMatcher;

public class OsmEntitiesBuilder {
    private final OdsModule module;
    private final OsmLayerManager layerManager;
//    private final OsmAddressNodeToBuildingMatcher nodeToBuildingMatcher;
    private final OsmBuildingToAddressNodesMatcher buildingToNodeMatcher;

    public OsmEntitiesBuilder(OdsModule module, OsmLayerManager layerManager) {
        super();
        this.module = module;
        this.layerManager = layerManager;
//        this.nodeToBuildingMatcher = new OsmAddressNodeToBuildingMatcher(layerManager);
        this.buildingToNodeMatcher = new OsmBuildingToAddressNodesMatcher(module);
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
        List<OsmEntityBuilder> entityBuilders = module.getEntityBuilders();
//        for (OsmEntityBuilder<?> builder : entityBuilders) {
//            builder.initialize();
//        }
        for (OsmPrimitive primitive : osmPrimitives) {
            if (!primitive.isIncomplete() && primitive.isTagged()) {
                for (OsmEntityBuilder builder : entityBuilders) {
                    if (builder.recognizes(primitive)) {
                        try {
                            Entity entity = builder.buildOsmEntity(primitive);
                            layerManager.getRepository().add(entity);
                        } catch (InvalidGeometryException e) {
                            // TODO Create validation error here? Or at a lower level.
                        }
                    }
                }
            }
        }
        Iterable<Building> iterable = layerManager.getRepository().getAll(Building.class);
        // TODO This code is specific for buildings and should be handled in a more generic way
        iterable.forEach(buildingToNodeMatcher::match);
    }
    
    /**
     * Update an Ods entity from the provided OSM primitive
     * 
     * @param osmPrimitives
     */
    public void tagsChanged(TagsChangedEvent event) {
        OsmPrimitive primitive = event.getPrimitive();
        List<OsmEntityBuilder> entityBuilders = module.getEntityBuilders();
//        for (OsmEntityBuilder<?> builder : entityBuilders) {
//            builder.initialize();
//        }
        for (OsmEntityBuilder builder : entityBuilders) {
            if (builder.recognizes(primitive)) {
                builder.updateTags(primitive, primitive.getKeys());
            }
        }
    }

    public void wayNodesChanged(WayNodesChangedEvent event) {
        Way osmWay = event.getChangedWay();
        List<OsmEntityBuilder> entityBuilders = module.getEntityBuilders();
        for (OsmEntityBuilder builder : entityBuilders) {
            if (builder.recognizes(osmWay)) {
                builder.updateGeometry(osmWay);
            }
        }
    }

    /**
     * Update the geometry of an entity according from the updated way.
     * 
     * @param osmWay
     */
    public void updatedGeometry(Way osmWay) {
        List<OsmEntityBuilder> entityBuilders = module.getEntityBuilders();
        for (OsmEntityBuilder builder : entityBuilders) {
            if (builder.recognizes(osmWay)) {
                builder.updateGeometry(osmWay);
            }
        }
    }

}
