package org.openstreetmap.josm.plugins.ods.matching.update;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;
import org.openstreetmap.josm.plugins.ods.osm.update.BuildingGeometryUpdater;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

@Deprecated
public class BuildingUpdater implements EntityUpdater {
    private final OdsModule module;
    private Set<Way> updatedWays = new HashSet<>();
    
    public BuildingUpdater(OdsModule module) {
        super();
        this.module = module;
    }

    @Override
    public void update(List<Match<?>> matches) {
        updatedWays.clear();
        List<Match<Building>> geometryUpdateNeeded = new LinkedList<>();
        Set<Entity> updatedEntities = new HashSet<>();
        for (Match<?> match : matches) {
            if (match.getBaseType().equals(Building.class)) {
                @SuppressWarnings("unchecked")
                Match<Building> buildingMatch = (Match<Building>) match;
                if (match.getGeometryMatch() == MatchStatus.NO_MATCH) {
                    geometryUpdateNeeded.add(buildingMatch);
                }
                Building osmBuilding = buildingMatch.getOsmEntity();
                Building odBuilding = buildingMatch.getOpenDataEntity();
                if (match.getAttributeMatch().equals(MatchStatus.NO_MATCH)) {
                    updateAttributes(odBuilding, osmBuilding);
                    updatedEntities.add(osmBuilding);
                }
                if (!match.getStatusMatch().equals(MatchStatus.MATCH)) {
                    updateStatus(odBuilding, osmBuilding);
                    updatedEntities.add(osmBuilding);
                }
            }
        }
        if (!geometryUpdateNeeded.isEmpty()) {
            BuildingGeometryUpdater geometryUpdater = new BuildingGeometryUpdater(
                module, geometryUpdateNeeded);
            geometryUpdater.run();
            updatedWays.addAll(geometryUpdater.getUpdatedWays());
            updatedEntities.addAll(geometryUpdater.getUpdatedEntities());
        }
        updateMatching();
    }

    private static void updateAttributes(Building odBuilding, Building osmBuilding) {
        ManagedPrimitive osmPrimitive = osmBuilding.getPrimitive();
        osmBuilding.setSourceDate(odBuilding.getSourceDate());
        osmPrimitive.put("source:date", odBuilding.getPrimitive().get("source:date"));
        osmBuilding.setStartDate(odBuilding.getStartDate());
        osmPrimitive.put("start_date", odBuilding.getStartDate());
//        osmPrimitive.setModified(true);
    }

    private static void updateStatus(Building odBuilding, Building osmBuilding) {
        ManagedPrimitive odPrimitive = odBuilding.getPrimitive();
        ManagedPrimitive localPrimitive = osmBuilding.getPrimitive();
        if (osmBuilding.getStatus().equals(EntityStatus.CONSTRUCTION)
                && odBuilding.getStatus().equals(EntityStatus.IN_USE)) {
            if (odBuilding.getSourceDate() != null) {
                osmBuilding.setSourceDate(odBuilding.getSourceDate());
                localPrimitive.put("source:date", odBuilding.getSourceDate().format(DateTimeFormatter.ISO_DATE));
            }
            localPrimitive.put("building", odPrimitive.get("building"));
            localPrimitive.put("construction", null);
            osmBuilding.setStatus(odBuilding.getStatus());
            // TODO Do we need this.
            localPrimitive.getPrimitive().setModified(true);
        }
    }
    
    private void updateMatching() {
        // TODO only update matching for modified objects
        for (Matcher<?> matcher : module.getMatcherManager().getMatchers()) {
            matcher.run();
        }
    }

    @Override
    public Collection<? extends Way> getUpdatedWays() {
        return updatedWays;
    }
}
