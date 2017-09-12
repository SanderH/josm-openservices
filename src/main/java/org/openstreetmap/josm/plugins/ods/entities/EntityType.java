package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public interface EntityType {

    public boolean canHandle(OsmPrimitive primitive);

}
