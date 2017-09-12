package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;

public class BuildingUnitEntityType implements EntityType {

    public static final BuildingUnitEntityType INSTANCE = new BuildingUnitEntityType();

    private BuildingUnitEntityType() {
        // Hide constructor because this is a singleton class
    }

    @Override
    public boolean canHandle(OsmPrimitive primitive) {
        // The concept of a building unit doesn't exit in OSM.
        // It only exist in the Inspire
        return false;
    }

    @Override
    public String toString() {
        return "Building Unit";
    }
}
