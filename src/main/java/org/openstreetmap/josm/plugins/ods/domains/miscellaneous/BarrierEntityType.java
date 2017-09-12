package org.openstreetmap.josm.plugins.ods.domains.miscellaneous;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;

public class BarrierEntityType implements EntityType {

    @Override
    public boolean canHandle(OsmPrimitive primitive) {
        // Currently not supported
        return false;
    }


    @Override
    public String toString() {
        return "Barrier";
    }

}
