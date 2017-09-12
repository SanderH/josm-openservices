package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.COMPARABLE;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.MATCH;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.NO_MATCH;

import java.util.stream.Stream;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;

public class BuildingVerifier {

    public void verify(Stream<Building> buildings) {
        buildings.forEach(building -> {
            verify(building);
        });
    }

    public void verify(Building building) {
        //        building.getMatch().ifPresent(match -> {
        //            Building osmBuilding = match.getMainMatch();
        //            if (osmBuilding != null) {
        //                verify(match, building, osmBuilding);
        //                return;
        //            }
        //        });
        //        )) {
        //            @SuppressWarnings("unchecked")
        //            StraightMatch<Building> buildingMatch = (StraightMatch<Building>) match
        //            .get();
        //            analyze(buildingMatch);
        //            match.get().updateMatchTags();
        //        } else {
        //            ManagedPrimitive primitive = building.getPrimitive();
        //            if (primitive != null) {
        //                primitive.put(ODS.KEY.IDMATCH, "false");
        //                primitive.put(ODS.KEY.STATUS,
        //                        building.getStatus().toString());
        //            }
        //        }
    }

    //    public void verify(Match<Building> match, Building odBuilding, Building osmBuilding) {
    //        match.clearDifferences();
    //        analyzeStartDate(match, odBuilding, osmBuilding);
    //        verifyStatus(match, odBuilding, osmBuilding);
    //        analyzeGeometry(match, odBuilding, osmBuilding);
    //    }
    //
    //    private static void analyzeStartDate(Match<Building> match, Building odBuilding, Building osmBuilding) {
    //        if (!Objects.equals(osmBuilding.getStartDate(),
    //                odBuilding.getStartDate())) {
    //            match.addAttributeDifference(new TagDifference(match, "start_date"));
    //        }
    //    }
    //
    //    private static void verifyStatus(Match<Building> match, Building odBuilding, Building osmBuilding) {
    //        EntityStatus osmStatus = osmBuilding.getStatus();
    //        EntityStatus odStatus = odBuilding.getStatus();
    //        if (osmStatus.equals(odStatus)
    //                || (odStatus.equals(IN_USE_NOT_MEASURED)
    //                        && osmStatus.equals(IN_USE))
    //                || (odStatus.equals(PLANNED) && osmStatus.equals(CONSTRUCTION))
    //                || (odStatus.equals(CONSTRUCTION) && (osmStatus.equals(IN_USE)
    //                        || osmStatus.equals(IN_USE_NOT_MEASURED)))) {
    //            return;
    //        }
    //        match.setStatusDifference(new StatusDifference(match));
    //    }
    //
    //    private static void analyzeGeometry(StraightMatch<Building> match) {
    //        Building odBuilding = match.getOpenDataEntity();
    //        Building osmBuilding = match.getOsmEntity();
    //        MatchStatus status = compareGeometries(osmBuilding, odBuilding);
    //        if (status == MATCH || status == MatchStatus.COMPARABLE) {
    //            return;
    //        }
    //        match.addDifference(new GeometryDifference(match));
    //    }

    private static MatchStatus compareGeometries(Building osmBuilding,
            Building odBuilding) {
        MatchStatus matchStatus = compareCentroids(osmBuilding, odBuilding);
        if (matchStatus == NO_MATCH)
            return NO_MATCH;
        return MatchStatus.combine(matchStatus,
                compareAreas(osmBuilding, odBuilding));
    }

    private static MatchStatus compareAreas(Building osmBuilding,
            Building odBuilding) {
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

    private static MatchStatus compareCentroids(Building osmBuilding,
            Building odBuilding) {
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

    //    private static List<Difference> checkAddressDifference(
    //            StraightMatch<Building> match) {
    //        Optional<Address> odAddress = match.getOpenDataEntity().getAddress();
    //        Optional<Address> osmAddress = match.getOsmEntity().getAddress();
    //        if (!(odAddress.isPresent() && osmAddress.isPresent())) {
    //            return Collections.emptyList();
    //        }
    //        List<String> differingTags = AddressTagMatcher.compare(odAddress.get(),
    //                osmAddress.get());
    //        if (differingTags.isEmpty()) {
    //            return Collections.emptyList();
    //        }
    //        List<Difference> differences = new ArrayList<>(differingTags.size());
    //        for (String key : differingTags) {
    //            differences.add(new TagDifference(match, key));
    //        }
    //        return differences;
    //    }

}
