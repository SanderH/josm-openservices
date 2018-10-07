package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.COMPARABLE;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.MATCH;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.NO_MATCH;

import java.util.Collections;
import java.util.Set;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.ods.MatchTask;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingDao;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OpenDataBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuildingDao;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.io.TaskStatus;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;
import org.openstreetmap.josm.plugins.ods.matching.Od2OsmMatch;

public class BuildingMatcher implements MatchTask {
    // TODO make matchFactory configurable by using dependency injection
    private final BuildingMatchFactory matchFactory;
    private final OdBuildingDao odBuildingDao;
    private final OsmBuildingDao osmBuildingDao;
    private final TaskStatus status = new TaskStatus();

    public BuildingMatcher(OdBuildingDao odBuildingDao,
            OsmBuildingDao osmBuildingDao, BuildingMatchFactory matchFactory) {
        super();
        this.odBuildingDao = odBuildingDao;
        this.osmBuildingDao = osmBuildingDao;
        this.matchFactory = matchFactory;
    }

    @Override
    public void initialize() throws OdsException {
        // No action required
    }

    /*
     * Try to find matches between buildings on the open data layer
     * and building on the OSM layer.
     */
    @Override
    public Void call() {
        matchById();
        return null;
    }

    private void matchById() {
        odBuildingDao.findAll().forEach(odBuilding -> {
            Od2OsmMatch match = odBuilding.getMatch();
            // TODO Handle duplicate matches, maybe use a validator.
            if (match != null) return;
            Object id = odBuilding.getReferenceId();
            Set<? extends OsmBuilding> osmBuildings = osmBuildingDao.listById(id);
            matchBuildings(odBuilding, osmBuildings);
        });
    }

    private void matchBuildings(OpenDataBuilding odBuilding, Set<? extends OsmBuilding> osmBuildings) {
        if (osmBuildings.isEmpty()) return;
        BuildingMatch match = matchFactory.create(
                Collections.singleton(odBuilding), osmBuildings);
        odBuilding.setMatch(match);
        osmBuildings.forEach(b -> b.setMatch(match));
    }

    //    public void analyze() {
    //        odRepository.getAll(Building.class).forEach(building -> {
    //            Optional<Match<Building>> match = building.getMatch();
    //            if (match.isPresent()) {
    //                @SuppressWarnings("unchecked")
    //                StraightMatch<Building> buildingMatch = (StraightMatch<Building>) match
    //                .get();
    //                analyze(buildingMatch);
    //                match.get().updateMatchTags();
    //            } else {
    //                ManagedPrimitive primitive = building.getPrimitive();
    //                if (primitive != null) {
    //                    primitive.put(ODS.KEY.IDMATCH, "false");
    //                    primitive.put(ODS.KEY.STATUS,
    //                            building.getStatus().toString());
    //                }
    //            }
    //        });
    //    }

    //    public void analyze(StraightMatch<Building> match) {
    //        match.getDifferences().clear();
    //        analyzeStartDate(match);
    //        analyzeStatus(match);
    //        analyzeGeometry(match);
    //    }
    //
    //    private static void analyzeStartDate(StraightMatch<Building> match) {
    //        if (!Objects.equals(match.getOsmEntity().getStartDate(),
    //                match.getOpenDataEntity().getStartDate())) {
    //            match.addDifference(new TagDifference(match, "start_date"));
    //        }
    //    }

    //    private static void analyzeStatus(StraightMatch<Building> match) {
    //        EntityStatus osmStatus = match.getOsmEntity().getStatus();
    //        EntityStatus odStatus = match.getOpenDataEntity().getStatus();
    //        if (osmStatus.equals(odStatus)
    //                || (odStatus.equals(IN_USE_NOT_MEASURED)
    //                        && osmStatus.equals(IN_USE))
    //                || (odStatus.equals(PLANNED) && osmStatus.equals(CONSTRUCTION))
    //                || (odStatus.equals(CONSTRUCTION) && (osmStatus.equals(IN_USE)
    //                        || osmStatus.equals(IN_USE_NOT_MEASURED)))) {
    //            return;
    //        }
    //        match.addDifference(new StatusDifference(match));
    //    }

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

    @Override
    public void reset() {
        // Nothing to reset
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }
}
