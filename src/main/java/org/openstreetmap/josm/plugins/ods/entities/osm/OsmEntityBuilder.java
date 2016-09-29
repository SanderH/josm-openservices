package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface OsmEntityBuilder {
//    public void initialize();
    
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
    public Entity buildOsmEntity(OsmPrimitive primitive);
    
    /**
     * Update the ods Entity for the passed osm primitive if such
     * entity exists.
     * Make sure this builder can handle the primitive by calling
     * the recognizes() method first. 
     * 
     * @param primitive
     */
    public void updateTags(OsmPrimitive primitive, Map<String, String> newTags);
    
    public void updateGeometry(Way way);

    /**
     * Update the ods Entity for the passed osm primitive if such
     * entity exists.
     * Make sure this builder can handle the primitive by calling
     * the recognizes() method first. 
     * 
     * @param primitive
     */
//    public void updateGeometry(Way way, List<Node> nodes);
    
    /**
     * Retrieve a list of keys known to this EntityBuilder.
     * This helps us to see if there are any extra keys.
     * 
     * @return
     */
    public Set<String> getParsedKeys();

//    public Class<T> getEntityClass();
}
