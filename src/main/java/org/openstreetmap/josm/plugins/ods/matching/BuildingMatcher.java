package org.openstreetmap.josm.plugins.ods.matching;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Repository;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

public class BuildingMatcher implements Matcher<Building> {
    private final OdsModule module;
    Repository odRepository;
    Repository osmRepository;

    private Map<Long, Match<Building>> buildingMatches = new HashMap<>();
    private List<Building> unidentifiedOsmBuildings = new LinkedList<>();
    private List<Building> unmatchedOpenDataBuildings = new LinkedList<>();
    private List<Building> unmatchedOsmBuildings = new LinkedList<>();

    public BuildingMatcher(OdsModule module) {
        super();
        this.module = module;
    }
    
    @Override
    public void initialize() throws OdsException {
        // No action required
//        odRepository = module.getOpenDataLayerManager().getRepository();
//        osmRepository = module.getOsmLayerManager().getRepository();
//        odBuildingStore = module.getOpenDataLayerManager().getEntityStore(Building.class);
//        osmBuildingStore = module.getOsmLayerManager().getEntityStore(Building.class);
    }

    
    @Override
    public Class<Building> getType() {
        return Building.class;
    }

    @Override
    public void run() {
        unmatchedOpenDataBuildings.clear();
        unmatchedOsmBuildings.clear();
        odRepository = module.getOpenDataLayerManager().getRepository();
        osmRepository = module.getOsmLayerManager().getRepository();
        for (Building building : odRepository.getAll(Building.class)) {
            processOpenDataBuilding(building);
        }
        for (Building building : osmRepository.getAll(Building.class)) {
            processOsmBuilding(building);
        }
        analyze();
    }

    private void processOpenDataBuilding(Building odBuilding) {
        if (odBuilding.getMatch(Building.class) != null) return;
        Long id = (Long) odBuilding.getReferenceId();
        Match<Building> match = buildingMatches.get(id);
        if (match != null) {
            match.addOpenDataEntity(odBuilding);
            odBuilding.addMatch(match, Building.class);
            return;
        }
        Iterator<Building> osmBuildings = osmRepository.query(Building.class, "referenceId", id).iterator();
        if (osmBuildings.hasNext()) {
            match = new BuildingMatch(osmBuildings.next(), odBuilding);
            while (osmBuildings.hasNext()) {
                Building osmBuilding = osmBuildings.next();
                osmBuilding.addMatch(match, Building.class);
                match.addOsmEntity(osmBuilding);
            }
            buildingMatches.put(id, match);
        } else {
            unmatchedOpenDataBuildings.add(odBuilding);
        }
    }

    private void processOsmBuilding(Building osmBuilding) {
        if (osmBuilding.getMatch(Building.class) != null) {
            return;
        }
        Object id = osmBuilding.getReferenceId();
        if (id == null) {
            unidentifiedOsmBuildings.add(osmBuilding);
            return;
        }
        Long l;
        try {
            l = (Long)id;
        }
        catch (Exception e) {
            unidentifiedOsmBuildings.add(osmBuilding);
            return;
        }
        Iterator<Building> odBuildings = odRepository.query(Building.class, "referenceId", l).iterator();
        if (odBuildings.hasNext()) {
            Match<Building> match = new BuildingMatch(osmBuilding, odBuildings.next());
            while (odBuildings.hasNext()) {
                match.addOpenDataEntity(odBuildings.next());
            }
            buildingMatches.put(l, match);
        } else {
            unmatchedOsmBuildings.add(osmBuilding);
        }
    }
    
    public void analyze() {
        for (Match<Building> match : buildingMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (Building building: unmatchedOpenDataBuildings) {
            ManagedPrimitive primitive = building.getPrimitive();
            if (primitive != null) {
                primitive.put(ODS.KEY.IDMATCH, "false");
                primitive.put(ODS.KEY.STATUS, building.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        buildingMatches.clear();
        unidentifiedOsmBuildings.clear();
        unmatchedOpenDataBuildings.clear();
        unmatchedOsmBuildings.clear();
    }
}
