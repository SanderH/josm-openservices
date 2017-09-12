package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;

public class BuildingEntityType implements EntityType {
    public static BuildingEntityType INSTANCE = new BuildingEntityType();

    private BuildingEntityType() {
        // Hide constructor because this is a singleton class
    }

    public static boolean isBuildingWay(Way way) {
        if (isBuilding(way)) {
            return true;
        }
        for (OsmPrimitive osm : way.getReferrers()) {
            if (isBuilding(osm)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canHandle(OsmPrimitive primitive) {
        return isBuilding(primitive);
    }

    public static boolean isBuilding(OsmPrimitive primitive) {
        return ((primitive.hasKey("building")
                || primitive.hasKey("building:part"))
                && (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
                || primitive
                .getDisplayType() == OsmPrimitiveType.MULTIPOLYGON
                || primitive
                .getDisplayType() == OsmPrimitiveType.RELATION));
    }

    public static boolean isBuildingWay(OsmPrimitive referrer) {
        if (referrer.getType() == OsmPrimitiveType.WAY) {
            return isBuildingWay((Way)referrer);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Building";
    }
}
