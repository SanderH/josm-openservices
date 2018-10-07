package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.event.TagsChangedEvent;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class OsmEntitiesBuilder {
    private final OdsModule module;
    private final OsmLayerManager layerManager;

    public OsmEntitiesBuilder(OdsModule module, OsmLayerManager layerManager) {
        super();
        this.module = module;
        this.layerManager = layerManager;
    }

    /**
     * Build ODS entities from OSM primitives.
     * Check all primitives in the OSM layer
     *
     */
    public void build() {
        OsmDataLayer dataLayer = layerManager.getOsmDataLayer();
        if (dataLayer == null) return;
        build(dataLayer.getDataSet().allPrimitives());
    }

    /**
     * Build Ods entities from the provided OSM primitives
     *
     * @param osmPrimitives
     */
    public void build(Collection<? extends OsmPrimitive> osmPrimitives) {
        List<OsmEntityBuilder> entityBuilders = module.getEntityBuilders();
        Repository repository = module.getRepository();
        for (OsmPrimitive primitive : osmPrimitives) {
            if (!primitive.isIncomplete() && primitive.isTagged()) {
                for (OsmEntityBuilder builder : entityBuilders) {
                    if (builder.recognizes(primitive)) {
                        try {
                            Entity entity = builder.buildOsmEntity(primitive);
                            repository.add(entity);
                        } catch (InvalidGeometryException e) {
                            // TODO Create validation error here? Or at a lower level.
                        }
                    }
                }
            }
        }
    }

    /**
     * Update an Ods entity from the provided OSM primitive
     *
     * @param osmPrimitives
     */
    public void tagsChanged(TagsChangedEvent event) {
        OsmPrimitive primitive = event.getPrimitive();
        List<OsmEntityBuilder> entityBuilders = module.getEntityBuilders();
        for (OsmEntityBuilder builder : entityBuilders) {
            if (builder.recognizes(primitive)) {
                builder.updateTags(primitive, primitive.getKeys());
            }
        }
    }

    /**
     * Update the geometry of an entity based on the updated node.
     *
     * @param node
     */
    public void updatedGeometry(Node node) {
        List<OsmEntityBuilder> entityBuilders = module.getEntityBuilders();
        for (OsmEntityBuilder builder : entityBuilders) {
            if (builder.recognizes(node)) {
                builder.updateGeometry(node);
            }
        }
    }

    /**
     * Update the geometry of an entity based on the updated way.
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
