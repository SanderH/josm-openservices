package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.MainLayerManager.ActiveLayerChangeEvent;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingAligner;
import org.openstreetmap.josm.plugins.ods.update.OdsImporter;
import org.openstreetmap.josm.plugins.ods.update.OdsUpdater;
import org.openstreetmap.josm.tools.I18n;

public class OdsUpdateAction extends OdsAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public OdsUpdateAction(OdsModule module) {
        super(module, "Update", (String)null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OdsImporter importer = new OdsImporter(getModule());
        OdsUpdater updater = new OdsUpdater(getModule());

        Layer layer = MainApplication.getLayerManager().getActiveLayer();
        LayerManager layerManager = getModule().getLayerManager(layer);
        if (layerManager == null || layerManager.isOsm()) {
            JOptionPane.showMessageDialog(MainApplication.getMainPanel(),
                    I18n.tr("This operation is only allowed on the ODS layer;"));
            return;
        }

        OsmDataLayer osmLayer = (OsmDataLayer) layer;
        importer.doImport(osmLayer.getDataSet().getAllSelected());
        updater.doUpdate(osmLayer.getDataSet().getAllSelected());
        Set<Way> modifiedWays = new HashSet<>();
        modifiedWays.addAll(importer.getImportedWays());
        modifiedWays.addAll(updater.getUpdatedWays());
        if (!modifiedWays.isEmpty()) {
            BuildingAligner buildingAligner = new BuildingAligner(modifiedWays);
            buildingAligner.run();
        }

        layerManager.getOsmDataLayer().getDataSet().clearSelection();
        MainApplication.getLayerManager().setActiveLayer(getModule().getOsmLayerManager().getOsmDataLayer());
    }

    @Override
    public void activeOrEditLayerChanged(ActiveLayerChangeEvent e) {
        Layer newLayer = MainApplication.getLayerManager().getActiveLayer();
        LayerManager layerManager = getModule().getLayerManager(newLayer);
        this.setEnabled(layerManager != null && !layerManager.isOsm());
    }
}
