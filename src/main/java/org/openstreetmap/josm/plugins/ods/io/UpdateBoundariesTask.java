package org.openstreetmap.josm.plugins.ods.io;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

// TODO Replace with BoudaryUpdateEvent
@Deprecated
public class UpdateBoundariesTask extends AbstractTask {
    private Boundary boundary;
    private final OpenDataLayerManager odLayerManager;
    /**
     * @param openDataLayerDownloader
     */
    UpdateBoundariesTask(OpenDataLayerManager odLayerManager) {
        this.odLayerManager = odLayerManager;
    }

    @Override
    public Void call() throws Exception {
        DataSource ds = new DataSource(boundary.getBounds(), "Import");
        odLayerManager.extendBoundary(boundary.getMultiPolygon());
        OsmDataLayer osmDataLayer = odLayerManager.getOsmDataLayer();
        osmDataLayer.getDataSet().addDataSource(ds);
        return null;
    }
}