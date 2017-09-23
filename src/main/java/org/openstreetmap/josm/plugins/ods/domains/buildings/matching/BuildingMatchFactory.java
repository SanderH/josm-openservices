package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import java.util.Collection;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;

public interface BuildingMatchFactory {

    public BuildingMatch create(OpenDataBuilding odBuilding, OsmBuilding osmBuilding);

    public BuildingMatch create(Collection<? extends OpenDataBuilding> odBuildings, Collection<? extends OsmBuilding> osmBuildings);

}
