package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.matching.StraightMatch;
import org.openstreetmap.josm.plugins.ods.primitives.SimpleManagedPolygon;
import org.openstreetmap.josm.plugins.ods.update.DefaultGeometryUpdater;


public class BuildingGeometryUpdater extends DefaultGeometryUpdater {

    public BuildingGeometryUpdater(OdsModule module) {
        super(module, BuildingGeometryUpdater::simpleBuildings);
    }


    /**
     * Check if this is a simple match of exactly one simple (single ring)
     * OSM building to one simple open data building.
     * @param match
     * @return
     */
    static boolean simpleBuildings(StraightMatch<?> match) {
        return (match.getOsmEntity().getPrimitive() instanceof SimpleManagedPolygon) &&
                (match.getOpenDataEntity().getPrimitive() instanceof SimpleManagedPolygon);
    }
}
