package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

public class BuildingMatcher implements Matcher<Building> {
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
        odRepository = module.getOpenDataLayerManager().getRepository();
        osmRepository = module.getOsmLayerManager().getRepository();
        odRepository.getAll(Building.class).forEach(this::processOpenDataBuilding);
        //        osmRepository.getAll(Building.class)
        //        .forEach(this::processOsmBuilding);
        analyze();
    }

    private void processOpenDataBuilding(Building odBuilding) {
        if (odBuilding.getMatch(Building.class) != null) return;
        Long id = (Long) odBuilding.getReferenceId();
        //        if (match != null) {
        //            match.addOpenDataEntity(odBuilding);
        //            odBuilding.addMatch(match);
        //            return;
        //        }
        osmRepository.query(Building.class, "referenceId", id).forEach(osmBuilding -> {
            matchBuildings(osmBuilding, odBuilding);
        });
    }

    //    private void processOsmBuilding(Building osmBuilding) {
    //        if (osmBuilding.getMatch(Building.class) != null) {
    //            return;
    //        }
    //        Object id = osmBuilding.getReferenceId();
    //        if (id == null) {
    //            unidentifiedOsmBuildings.add(osmBuilding);
    //            return;
    //        }
    //        Long l;
    //        try {
    //            l = (Long)id;
    //        }
    //        catch (ClassCastException e) {
    //            unidentifiedOsmBuildings.add(osmBuilding);
    //            return;
    //        }
    //        Iterator<Building> odBuildings = odRepository.query(Building.class, "referenceId", l).iterator();
    //        if (odBuildings.hasNext()) {
    //            Match<Building> match = new BuildingMatch(osmBuilding, odBuildings.next());
    //            while (odBuildings.hasNext()) {
    //                match.addOpenDataEntity(odBuildings.next());
    //            }
    //            buildingMatches.put(l, match);
    //        } else {
    //            unmatchedOsmBuildings.add(osmBuilding);
    //        }
    //    }

    private static void matchBuildings(Building osmEntity, Building odEntity) {
        Match<Building> match = osmEntity.getMatch(Building.class);
        if (match != null) {
            match.addOpenDataEntity(odEntity);
            odEntity.addMatch(match);
        }
        else {
            match = new BuildingMatch(osmEntity, odEntity);
        }
        //        match.analyze();
        //        match.updateMatchTags();
    }

    public void analyze() {
        odRepository.getAll(Building.class).forEach(building -> {
            Match<Building> match = building.getMatch(Building.class);
            if (match != null) {
                match.analyze();
                match.updateMatchTags();
            }
            else {
                ManagedPrimitive primitive = building.getPrimitive();
                if (primitive != null) {
                    primitive.put(ODS.KEY.IDMATCH, "false");
                    primitive.put(ODS.KEY.STATUS, building.getStatus().toString());
                }
            }
        });
    }

    @Override
    public void reset() {
        unidentifiedOsmBuildings.clear();
    }
}
