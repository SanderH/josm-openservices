package org.openstreetmap.josm.plugins.ods.update;

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
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

public class DefaultEntityUpdater<E extends Entity> implements EntityUpdater {
    private final OdsModule module;
    private final Class<E> entityType;
    private final GeometryUpdater geometryUpdater;
    private Set<Way> updatedWays = new HashSet<>();
    
    public DefaultEntityUpdater(OdsModule module, Class<E> entityType, GeometryUpdater geometryUpdater) {
        super();
        this.module = module;
        this.entityType = entityType;
        this.geometryUpdater = geometryUpdater;
    }

    
    @Override
    public Class<E> getType() {
        return entityType;
    }


    @Override
    public UpdateResult update(List<Match<?>> matches) {
        updatedWays.clear();
        List<Match<? extends Entity>> geometryUpdateNeeded = new LinkedList<>();
        Set<E> updatedEntities = new HashSet<>();
        for (Match<?> match : matches) {
            if (match.getBaseType().equals(entityType)) {
                @SuppressWarnings("unchecked")
                Match<E> theMatch = (Match<E>) match;
                if (match.getGeometryMatch() == MatchStatus.NO_MATCH) {
                    geometryUpdateNeeded.add(theMatch);
                }
                E osmEntity = theMatch.getOsmEntity();
                E odEntity = theMatch.getOpenDataEntity();
                if (match.getAttributeMatch().equals(MatchStatus.NO_MATCH)) {
                    updateAttributes(odEntity, osmEntity);
                    updatedEntities.add(osmEntity);
                }
                if (!match.getStatusMatch().equals(MatchStatus.MATCH)) {
                    updateStatus(odEntity, osmEntity);
                    updatedEntities.add(osmEntity);
                }
            }
        }
        if (!geometryUpdateNeeded.isEmpty()) {
//            BuildingGeometryUpdater geometryUpdater = new BuildingGeometryUpdater(
//                module, geometryUpdateNeeded);
            UpdateResult result = geometryUpdater.run(geometryUpdateNeeded);
            updatedWays.addAll(result.getUpdatedWays());
            updatedEntities.addAll((Collection<? extends E>) result.getUpdatedEntities());
        }
        updateMatching();
        return new UpdateResultImpl(updatedEntities, updatedWays);
    }

    protected void updateAttributes(E odEntity, E osmEntity) {
        ManagedPrimitive osmPrimitive = osmEntity.getPrimitive();
        osmEntity.setSourceDate(odEntity.getSourceDate());
        osmPrimitive.put("source:date", odEntity.getPrimitive().get("source:date"));
    }

    private void updateStatus(E odEntity, E osmEntity) {
        ManagedPrimitive odPrimitive = odEntity.getPrimitive();
        ManagedPrimitive localPrimitive = osmEntity.getPrimitive();
        if (osmEntity.getStatus().equals(EntityStatus.CONSTRUCTION) &&
                (odEntity.getStatus().equals(EntityStatus.IN_USE) ||
                 odEntity.getStatus().equals(EntityStatus.IN_USE_NOT_MEASURED))
        ) {
            if (odEntity.getSourceDate() != null) {
                osmEntity.setSourceDate(odEntity.getSourceDate());
                localPrimitive.put("source:date", odEntity.getSourceDate().format(DateTimeFormatter.ISO_DATE));
            }
            localPrimitive.put("building", odPrimitive.get("building"));
            localPrimitive.put("construction", null);
            osmEntity.setStatus(odEntity.getStatus());
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
}
