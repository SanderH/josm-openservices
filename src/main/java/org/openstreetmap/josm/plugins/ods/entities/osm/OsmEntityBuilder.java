package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface OsmEntityBuilder {
    //    public void initialize();

    public void initialize(OdsModule odsModule);

    public OsmLayerManager getLayerManager();
    /**
     * Check if we recognize the osm primitive as a valid object
     * for this builder.
     *
     * @param primitive The primitive to check
     * @return true if this builder can handle the osm primitive
     */
    public boolean recognizes(OsmPrimitive primitive);

    /**
     * Create an ods Entity for the passed osm primitive if no such
     * entity exists. And add it to the layerManager.
     * Make sure this builder can handle the primitive by calling
     * the recognizes() method first.
     *
     * @param primitive
     * @return
     */
    public Entity<?> buildOsmEntity(OsmPrimitive primitive) throws InvalidGeometryException;

    /**
     * Update the ods Entity for the passed osm primitive if such
     * entity exists.
     * Make sure this builder can handle the primitive by calling
     * the recognizes() method first.
     *
     * @param primitive
     */
    public void updateTags(OsmPrimitive primitive, Map<String, String> newTags);

    public void updateGeometry(Node node);

    public void updateGeometry(Way way);

    /**
     * Retrieve a list of keys known to this EntityBuilder.
     * This helps us to see if there are any extra keys.
     *
     * @return
     */
    public Set<String> getParsedKeys();
}
