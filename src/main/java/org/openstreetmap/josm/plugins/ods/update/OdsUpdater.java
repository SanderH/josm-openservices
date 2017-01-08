package org.openstreetmap.josm.plugins.ods.update;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingUpdater;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

/**
 * The updater updates objects in the Osm layer with new data from the OpenData layer.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsUpdater {
    private OdsModule module;
    private List<EntityUpdater> entityUpdaters = new LinkedList<>();
    private Set<Way> updatedWays = new HashSet<>();
    
    public OdsUpdater(OdsModule module) {
        super();
        this.module = module;
        // TODO get entity updaters from 
        this.entityUpdaters.add(new BuildingUpdater(module));
    }

    public void doUpdate(Collection<OsmPrimitive> primitives) {
        LayerManager layerManager = module.getOpenDataLayerManager();
        List<Match<?>> updateableMatches = new LinkedList<>();
        for (OsmPrimitive primitive : primitives) {
            ManagedPrimitive mPrimitive = layerManager.getManagedPrimitive(primitive);
            if (mPrimitive != null) {
                Entity entity = mPrimitive.getEntity();
                if (entity != null) {
                    Match<?> match = entity.getMatch(entity.getBaseType());
                    if (match != null && match.isSimple()) {
                        updateableMatches.add(match);
                    }
                }
            }
        }
        for (EntityUpdater updater : entityUpdaters) {
            UpdateResult result = updater.update(updateableMatches);
            updatedWays.addAll(result.getUpdatedWays());
        }
        for (Match<?> match : updateableMatches) {
            match.analyze();
            match.updateMatchTags();
        }
    }

    public Collection<? extends Way> getUpdatedWays() {
        return updatedWays;
    }
}