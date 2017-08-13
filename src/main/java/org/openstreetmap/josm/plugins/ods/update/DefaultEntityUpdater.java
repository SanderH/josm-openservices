package org.openstreetmap.josm.plugins.ods.update;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.matching.GeometryDifference;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.matching.StraightMatch;

public class DefaultEntityUpdater implements EntityUpdater {
    private final OdsModule module;
    //    private final Class<E> entityType;
    private final GeometryUpdater geometryUpdater;
    private final Set<Way> updatedWays = new HashSet<>();

    public DefaultEntityUpdater(OdsModule module, Class<?> entityType, GeometryUpdater geometryUpdater) {
        super();
        this.module = module;
        //        this.entityType = entityType;
        this.geometryUpdater = geometryUpdater;
    }

    //
    //    @Override
    //    public Class<E> getType() {
    //        return entityType;
    //    }
    //

    @Override
    public UpdateResult update(List<Match> matches) {
        updatedWays.clear();
        //        List<GeometryDifference> geometryDifferences = new LinkedList<>();
        //        List<StatusDifference> statusDifferences = new LinkedList<>();
        //        List<TagDifference> tagDifferences = new LinkedList<>();
        Set<Entity> updatedEntities = new HashSet<>();
        //        for (Match match : matches) {
        //            // TODO We probably could use a Visitor pattern here
        //            for (Difference difference : match.getDifferences()) {
        //                if (difference instanceof GeometryDifference) {
        //                    geometryDifferences.add((GeometryDifference) difference);
        //                }
        //                if (difference instanceof StatusDifference) {
        //                    statusDifferences.add((StatusDifference) difference);
        //                }
        //                if (difference instanceof TagDifference) {
        //                    tagDifferences.add((TagDifference) difference);
        //                }
        //            }
        //            if (match.hasDifferences()) {
        //                updatedEntities.addAll(match.getOpenDataEntities());
        //            }
        //        }
        //        updateAttributes(tagDifferences);
        //        updateStatuses(statusDifferences);
        //        updateGeometries(geometryDifferences);
        return new UpdateResultImpl(updatedEntities, updatedWays);
    }

    private void updateGeometries(List<GeometryDifference> geometryDifferences) {
        //        if (!geometryDifferences.isEmpty()) {
        //            //            BuildingGeometryUpdater geometryUpdater = new BuildingGeometryUpdater(
        //            //                module, geometryUpdateNeeded);
        //            UpdateResult result = geometryUpdater.run(geometryDifferences);
        //            updatedWays.addAll(result.getUpdatedWays());
        //        }
        //        updateMatching();
    }

    protected List<Command> updateAttributes(List<StraightMatch<?>> matches) {
        List<Command> commands = new LinkedList<>();
        //        for (StraightMatch<?> match : matches) {
        //            for (Difference difference : match.getDifferences()) {
        //                if (difference instanceof TagDifference) {
        //
        //
        //                    ManagedPrimitive osmPrimitive = osmEntity.getPrimitive();
        //                    osmEntity.setSourceDate(odEntity.getSourceDate());
        //                    osmPrimitive.put("source:date", odEntity.getPrimitive().get("source:date"));
        //                }
        //
        //                private void updateStatus(E odEntity, E osmEntity) {
        //                    ManagedPrimitive odPrimitive = odEntity.getPrimitive();
        //                    ManagedPrimitive localPrimitive = osmEntity.getPrimitive();
        //                    if (osmEntity.getStatus().equals(EntityStatus.CONSTRUCTION) &&
        //                            (odEntity.getStatus().equals(EntityStatus.IN_USE) ||
        //                                    odEntity.getStatus().equals(EntityStatus.IN_USE_NOT_MEASURED))
        //                            ) {
        //                        if (odEntity.getSourceDate() != null) {
        //                            osmEntity.setSourceDate(odEntity.getSourceDate());
        //                            localPrimitive.put("source:date", odEntity.getSourceDate().format(DateTimeFormatter.ISO_DATE));
        //                        }
        //                        localPrimitive.put("building", odPrimitive.get("building"));
        //                        localPrimitive.put("construction", null);
        //                        osmEntity.setStatus(odEntity.getStatus());
        //                        // TODO Do we need this.
        //                        localPrimitive.getPrimitive().setModified(true);
        //                    }
        //                }
        //
        //                private void updateMatching() {
        //                    // TODO only update matching for modified objects
        //                    for (Matcher matcher : module.getMatcherManager().getMatchers()) {
        //                        matcher.run();
        //                    }
        //                }
        //            }
        return commands;
    }
}
