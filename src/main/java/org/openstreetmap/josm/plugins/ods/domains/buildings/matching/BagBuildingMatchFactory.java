package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import java.util.Collection;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;

public class BagBuildingMatchFactory implements BuildingMatchFactory {

    @Override
    public BuildingMatch create(OpenDataBuilding odBuilding,
            OsmBuilding osmBuilding) {
        return new SimpleBagBuildingMatch(odBuilding, osmBuilding);
    }

    @Override
    public BuildingMatch create(Collection<? extends OpenDataBuilding> odBuildings,
            Collection<? extends OsmBuilding> osmBuildings) {
        return new ComplexBagBuildingMatch(odBuildings, osmBuildings);
    }
}