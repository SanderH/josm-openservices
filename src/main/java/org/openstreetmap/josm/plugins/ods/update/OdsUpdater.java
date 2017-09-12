package org.openstreetmap.josm.plugins.ods.update;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.OdsModule;

/**
 * The updater updates objects in the Osm layer with new data from the OpenData layer.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsUpdater {
    @SuppressWarnings("unused")
    private final OdsModule module;
    private final Set<Way> updatedWays = new HashSet<>();

    public OdsUpdater(OdsModule module) {
        super();
        this.module = module;
    }

    public void doUpdate(Collection<OsmPrimitive> primitives) {
        //        LayerManager layerManager = module.getOpenDataLayerManager();
        //        List<Match> updateableMatches = new LinkedList<>();
        //        for (OsmPrimitive primitive : primitives) {
        //            ManagedPrimitive mPrimitive = layerManager.getManagedPrimitive(primitive);
        //            if (mPrimitive != null) {
        //                Entity entity = mPrimitive.getEntity();
        //                entity.getMatch().ifPresent(updateableMatches::add);
        //            }
        //        }
        //        for (EntityUpdater updater : module.getUpdaters()) {
        //            UpdateResult result = updater.update(updateableMatches);
        //            updatedWays.addAll(result.getUpdatedWays());
        //        }
        //        for (Match match : updateableMatches) {
        //            match.analyze();
        //            match.updateMatchTags();
        //        }
    }

    public Collection<? extends Way> getUpdatedWays() {
        return updatedWays;
    }
}