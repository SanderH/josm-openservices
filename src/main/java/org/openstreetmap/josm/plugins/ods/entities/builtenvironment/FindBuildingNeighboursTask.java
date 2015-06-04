package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.EntitySource;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.tasks.Task;


/**
 * This tasks verifies if there are adjacent buildings in
 * the down loaded data.
 * 
 * TODO consider running over all buildings, not just the new ones.
 * 
 * @author gertjan
 *
 */
public class FindBuildingNeighboursTask implements Task {
    private final GtBuildingStore buildingStore;
    private final Double tolerance;
    
    public FindBuildingNeighboursTask(GtBuildingStore buildingStore, GeoUtil geoUtil, Double tolerance) {
        super();
        this.buildingStore = buildingStore;
        this.tolerance = tolerance;
    }

    @Override
    public void run(Context ctx) {
        EntitySource entitySource = (EntitySource) ctx.get("entitySource");
        for (Building building : buildingStore) {
            if (entitySource == building.getEntitySource()) {
                findNeigbours(building);
            }
        }
    }

    private void findNeigbours(Building building) {
        // TODO consider using a buffer around the building
        for (Building candidate : buildingStore.getGeoIndex().intersection(building.getGeometry())) {
            if (candidate == building) continue;
            if (building.getNeighbours().contains(candidate)) continue;
            building.getNeighbours().add(candidate);
            candidate.getNeighbours().add(building);
        }
    }
}