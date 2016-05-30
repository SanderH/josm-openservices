package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityRepository;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

/**
 * Update the open data layer after new data has been downloaded.
 * TODO find better name for this class
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class LayerUpdater {
    private OdsModule module;
    private DataSet dataSet;
    
    public LayerUpdater(OdsModule module) {
        this.module = module;
        this.dataSet = module.getOpenDataLayerManager().getOsmDataLayer().data;
    }

    public void run() {
        dataSet.beginUpdate();
        EntityRepository repository = module.getOpenDataLayerManager().getRepository();
        for (Entity entity : repository.getAll()) {
            if (entity.getPrimitive() != null) {
                update(entity.getPrimitive());
            }
        }
//        for (EntityStore<? extends Entity> store : module.getOpenDataLayerManager().getStores()) {
//            for (Entity entity : store.getPrimaryIndex().getAll()) {
//                if (entity.getPrimitive() != null) {
//                    update(entity.getPrimitive());
//                }
//            }
//        };
        dataSet.endUpdate();
    }

    private void update(ManagedPrimitive<? extends OsmPrimitive> primitive) {
        Entity entity = primitive.getEntity();
        if (entity != null && !entity.isIncomplete()) {
            primitive.create(dataSet);
        }
    }

}
