package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.matching.Osm2OdMatch;

public interface OsmEntity<T extends EntityType> extends Entity<T> {

    public Osm2OdMatch<T> getMatch();

    /**
     * Check if a matching entity with a modified geometry is available from
     * the OpenData layer.
     *
     * @return true if a modified geometry is available
     */
    public boolean isGeometryUpdateRequired();

    /**
     * Check if the status entity has changed and should be updated.
     *
     * @return true if this entity requires a status update.
     */
    public boolean isStatusUpdateRequired();

    /**
     * Check if any of the entity's tag should be updated.
     * This excludes any tagging related to the geometry (like type=multipolygon)
     * or to the status (like construction=*)
     *
     * @return true if this entity requires a tagging update.
     */
    public boolean isTaggingUpdateRequired();
    /**
     * Sometimes an open data source reports an object that doesn't exist in reality
     * or for some reason is not desirable in OSM.
     * For example, the Utrecht municipality sometimes draws a circular building
     * on the map to indicate a location. In reality no building is present.
     * An other example in Utrecht are machine rooms for bascule bridges. They are
     * considered buildings in the open data source (BAG), but you would normally never
     * been added to OSM.
     * The problem here is that these object keep popping up in comparison tools. A
     * solution could be to use the no: life-cycle-prefix as explained by
     * http://wiki.openstreetmap.org/wiki/Prefix:no
     *
     * @return true if this object has a no: life-cycle prefix
     */
    public boolean isNoEntity();
}
