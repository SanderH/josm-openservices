package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.matching.StraightMatch;

@Deprecated
public class BuildingMatch extends StraightMatch<Building> {

    public BuildingMatch(Building building) {
        super(building);
    }
}