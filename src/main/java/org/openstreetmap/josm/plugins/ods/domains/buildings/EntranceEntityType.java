package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;

public class EntranceEntityType implements EntityType {
    public final static EntranceEntityType INSTANCE = new EntranceEntityType();

    private EntranceEntityType() {
        // Hide public constructor because of singleton
    }

    @Override
    public boolean canHandle(OsmPrimitive primitive) {
        return primitive.hasKey("entrance");
    }

    @Override
    public String toString() {
        return "Entrance";
    }
}
