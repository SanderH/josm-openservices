package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.Repository;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

/**
 * Update the open data layer after new data has been downloaded.
 * TODO find better name for this class
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class LayerUpdater {
    private final OdsModule module;
    private final LayerManager layerManager;
    private DataSet dataSet;
    
    public LayerUpdater(OdsModule module) {
        this.module = module;
        this.layerManager = module.getOpenDataLayerManager();
        this.dataSet = layerManager.getOsmDataLayer().data;
    }

    public void run() {
        dataSet.beginUpdate();
        Repository repository = module.getOpenDataLayerManager().getRepository();
        for (Object obj : repository.getAll()) {
            if (obj instanceof Entity) {
                Entity entity = (Entity) obj;
                if (entity.getPrimitive() != null) {
                    update(entity.getPrimitive());
                }
            }
        }
        dataSet.endUpdate();
    }

    private void update(ManagedPrimitive<? extends OsmPrimitive> primitive) {
        Entity entity = primitive.getEntity();
        if (entity != null && !entity.isIncomplete()) {
            OsmPrimitive osmPrimitive = primitive.create(dataSet);
            layerManager.register(osmPrimitive, primitive);
        }
    }

}
