package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import static org.openstreetmap.josm.plugins.ods.entities.EntityStatus.CONSTRUCTION;
import static org.openstreetmap.josm.plugins.ods.entities.EntityStatus.IN_USE;
import static org.openstreetmap.josm.plugins.ods.entities.EntityStatus.IN_USE_NOT_MEASURED;
import static org.openstreetmap.josm.plugins.ods.entities.EntityStatus.PLANNED;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.COMPARABLE;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.MATCH;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.NO_MATCH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.matching.AddressTagMatcher;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.matching.Difference;
import org.openstreetmap.josm.plugins.ods.matching.GeometryDifference;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;
import org.openstreetmap.josm.plugins.ods.matching.StatusDifference;
import org.openstreetmap.josm.plugins.ods.matching.StraightMatch;
import org.openstreetmap.josm.plugins.ods.matching.TagDifference;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class BuildingMatcher implements Matcher {
    private final OdsModule module;
    Repository odRepository;
    Repository osmRepository;

    private final List<Building> unidentifiedOsmBuildings = new LinkedList<>();

    public BuildingMatcher(OdsModule module) {
        super();
        this.module = module;
    }

    @Override
    public void initialize() throws OdsException {
        // No action required
    }

    @Override
    public void run() {
        odRepository = module.getOpenDataLayerManager().getRepository();
        osmRepository = module.getOsmLayerManager().getRepository();
        odRepository.getAll(Building.class)
        .forEach(this::processOpenDataBuilding);
        // osmRepository.getAll(Building.class)
        // .forEach(this::processOsmBuilding);
        //        analyze();
    }

    private void processOpenDataBuilding(Building odBuilding) {
        if (odBuilding.getMatch().isPresent())
            return;
        Long id = (Long) odBuilding.getReferenceId();
        // if (match != null) {
        // match.addOpenDataEntity(odBuilding);
        // odBuilding.addMatch(match);
        // return;
        // }
        Building[] osmBuildings = osmRepository
                .query(Building.class, "referenceId", id)
                .toArray(Building[]::new);
        matchBuildings(odBuilding, osmBuildings);
    }

    private static void matchBuildings(Building odBuilding,
            Building[] osmBuildings) {
        if (osmBuildings.length == 1) {
            Building osmBuilding = osmBuildings[0];
            Optional<Match<Building>> match = osmBuilding.getMatch();
            if (!match.isPresent()) {
                match = Optional.of(new StraightMatch<>(osmBuilding));
            }
            // match.analyze();
            // match.updateMatchTags();
        }
    }

    public void analyze() {
        odRepository.getAll(Building.class).forEach(building -> {
            Optional<Match<Building>> match = building.getMatch();
            if (match.isPresent()) {
                @SuppressWarnings("unchecked")
                StraightMatch<Building> buildingMatch = (StraightMatch<Building>) match
                .get();
                analyze(buildingMatch);
                match.get().updateMatchTags();
            } else {
                ManagedPrimitive primitive = building.getPrimitive();
                if (primitive != null) {
                    primitive.put(ODS.KEY.IDMATCH, "false");
                    primitive.put(ODS.KEY.STATUS,
                            building.getStatus().toString());
                }
            }
        });
    }

    public void analyze(StraightMatch<Building> match) {
        match.getDifferences().clear();
        analyzeStartDate(match);
        analyzeStatus(match);
        analyzeGeometry(match);
    }

    private static void analyzeStartDate(StraightMatch<Building> match) {
        if (!Objects.equals(match.getOsmEntity().getStartDate(),
                match.getOpenDataEntity().getStartDate())) {
            match.addDifference(new TagDifference(match, "start_date"));
        }
    }

    private static void analyzeStatus(StraightMatch<Building> match) {
        EntityStatus osmStatus = match.getOsmEntity().getStatus();
        EntityStatus odStatus = match.getOpenDataEntity().getStatus();
        if (osmStatus.equals(odStatus)
                || (odStatus.equals(IN_USE_NOT_MEASURED)
                        && osmStatus.equals(IN_USE))
                || (odStatus.equals(PLANNED) && osmStatus.equals(CONSTRUCTION))
                || (odStatus.equals(CONSTRUCTION) && (osmStatus.equals(IN_USE)
                        || osmStatus.equals(IN_USE_NOT_MEASURED)))) {
            return;
        }
        match.addDifference(new StatusDifference(match));
    }

    private static void analyzeGeometry(StraightMatch<Building> match) {
        Building odBuilding = match.getOpenDataEntity();
        Building osmBuilding = match.getOsmEntity();
        MatchStatus status = compareGeometries(osmBuilding, odBuilding);
        if (status == MATCH || status == MatchStatus.COMPARABLE) {
            return;
        }
        match.addDifference(new GeometryDifference(match));
    }

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

    private static List<Difference> checkAddressDifference(
            StraightMatch<Building> match) {
        Optional<Address> odAddress = match.getOpenDataEntity().getAddress();
        Optional<Address> osmAddress = match.getOsmEntity().getAddress();
        if (!(odAddress.isPresent() && osmAddress.isPresent())) {
            return Collections.emptyList();
        }
        List<String> differingTags = AddressTagMatcher.compare(odAddress.get(),
                osmAddress.get());
        if (differingTags.isEmpty()) {
            return Collections.emptyList();
        }
        List<Difference> differences = new ArrayList<>(differingTags.size());
        for (String key : differingTags) {
            differences.add(new TagDifference(match, key));
        }
        return differences;
    }

    @Override
    public void reset() {
        unidentifiedOsmBuildings.clear();
    }
}
