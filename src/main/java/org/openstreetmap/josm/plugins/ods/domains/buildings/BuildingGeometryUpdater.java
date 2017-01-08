package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.primitives.SimpleManagedPolygon;
import org.openstreetmap.josm.plugins.ods.update.DefaultGeometryUpdater;


public class BuildingGeometryUpdater extends DefaultGeometryUpdater<Building> {
    
    public BuildingGeometryUpdater(OdsModule module) {
        super(module, BuildingGeometryUpdater::simpleBuildings);
    }

    
    /**
     * Check if this is a simple match of exactly one simple (single ring)
     * OSM building to one simple open data building.
     * @param match
     * @return
     */
    static boolean simpleBuildings(Match<?> match) {
        return match.isSimple() &&
        (match.getOsmEntity().getPrimitive() instanceof SimpleManagedPolygon) &&
        (match.getOpenDataEntity().getPrimitive() instanceof SimpleManagedPolygon);
    }
}
