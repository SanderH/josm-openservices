package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import static org.openstreetmap.josm.plugins.ods.entities.EntityStatus.CONSTRUCTION;
import static org.openstreetmap.josm.plugins.ods.entities.EntityStatus.IN_USE;
import static org.openstreetmap.josm.plugins.ods.entities.EntityStatus.IN_USE_NOT_MEASURED;
import static org.openstreetmap.josm.plugins.ods.entities.EntityStatus.PLANNED;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.COMPARABLE;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.MATCH;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.NO_MATCH;

import java.util.Objects;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.matching.MatchImpl;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;

public class BuildingMatch extends MatchImpl<Building> {
    private MatchStatus geometryMatch;
    private MatchStatus startDateMatch;
    private MatchStatus statusMatch;

    public BuildingMatch(Building osmBuilding, Building openDataBuilding) {
        super(osmBuilding, openDataBuilding, Building.class);
    }

    @Override
    public void analyze() {
        startDateMatch = compareStartDates();
        statusMatch = compareStatuses();
        geometryMatch = compareGeometries();
    }

    private MatchStatus compareStartDates() {
        if (Objects.equals(getOsmEntity().getStartDate(), getOpenDataEntity().getStartDate())) {
            return MATCH;
        }
        return NO_MATCH;
    }

    private MatchStatus compareStatuses() {
        EntityStatus osmStatus = getOsmEntity().getStatus();
        EntityStatus odStatus = getOpenDataEntity().getStatus();
        if (osmStatus.equals(odStatus)) {
            return MATCH;
        }
        if (odStatus.equals(IN_USE_NOT_MEASURED) && osmStatus.equals(IN_USE)) {
            return MATCH;
        }
        if (odStatus.equals(PLANNED) && osmStatus.equals(CONSTRUCTION)) {
            return COMPARABLE;
        }
        if (odStatus.equals(CONSTRUCTION) &&
                (osmStatus.equals(IN_USE) || osmStatus.equals(IN_USE_NOT_MEASURED))) {
            return COMPARABLE;
        }
        return NO_MATCH;
    }

    private MatchStatus compareGeometries() {
        MatchStatus matchStatus = NO_MATCH;
        for (Building odBuilding : getOpenDataEntities()) {
            for (Building osmBuilding :getOsmEntities()) {
                MatchStatus status = compareGeometries(osmBuilding, odBuilding);
                if (status == MATCH) {
                    return MATCH;
                }
                if (status == COMPARABLE) {
                    matchStatus = COMPARABLE;
                }
            }
        }
        return matchStatus;
    }

    private static MatchStatus compareGeometries(Building osmBuilding, Building odBuilding) {
        MatchStatus matchStatus = compareCentroids(osmBuilding, odBuilding);
        if (matchStatus == NO_MATCH) return NO_MATCH;
        return MatchStatus.combine(matchStatus, compareAreas(osmBuilding, odBuilding));
    }

    private static MatchStatus compareAreas(Building osmBuilding, Building odBuilding) {
        double osmArea = osmBuilding.getPrimitive().getArea();
        double odArea = odBuilding.getPrimitive().getArea();
        if (osmArea == 0.0 || odArea == 0.0) {
            return NO_MATCH;
        }
        double match = (osmArea - odArea) / osmArea;
        if (match == 0.0) {
            return MATCH;
        }
        if (Math.abs(match) < 0.01) {
            return COMPARABLE;
        }
        return NO_MATCH;
    }

    private static MatchStatus compareCentroids(Building osmBuilding, Building odBuilding) {
        LatLon osmCentroid = osmBuilding.getPrimitive().getCenter();
        LatLon odCentroid = odBuilding.getPrimitive().getCenter();
        double centroidDistance = osmCentroid.distance(odCentroid);
        if (centroidDistance == 0) {
            return MATCH;
        }
        if (centroidDistance < 1e-6) {
            return COMPARABLE;
        }
        return NO_MATCH;
    }

    @Override
    public MatchStatus getGeometryMatch() {
        return geometryMatch;
    }

    @Override
    public MatchStatus getStatusMatch() {
        return statusMatch;
    }

    @Override
    public MatchStatus getAttributeMatch() {
        return startDateMatch;
    }
}