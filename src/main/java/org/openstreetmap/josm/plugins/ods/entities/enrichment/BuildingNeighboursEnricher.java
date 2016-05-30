package org.openstreetmap.josm.plugins.ods.entities.enrichment;

import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;


/**
 * This tasks verifies if there are adjacent buildings in
 * the down loaded data.
 * 
 * TODO consider running over all buildings, not just the new ones.
 * 
 * @author gertjan
 *
 */
public class BuildingNeighboursEnricher implements Consumer<Building> {
    private final GeoIndex<Building> geoIndex;
    
    public BuildingNeighboursEnricher(OpenDataLayerManager layerManager, GeoUtil geoUtil) {
        super();
        // TODO add #default 
        this.geoIndex =layerManager.getRepository().getGeoIndex(Building.class, "geometry");
    }

    @Override
    public void accept(Building building) {
        // TODO consider using a buffer around the building
        for (Building candidate : geoIndex.intersection(building.getGeometry())) {
            if (candidate == building) continue;
            if (building.getNeighbours().contains(candidate)) continue;
            building.getNeighbours().add(candidate);
            candidate.getNeighbours().add(building);
        }
    }
}
