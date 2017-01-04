package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingAligner;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsImporterNg;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsUpdater;
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
        OdsImporterNg importer = new OdsImporterNg(getModule());
        OdsUpdater updater = new OdsUpdater(getModule());

        Layer layer = Main.getLayerManager().getActiveLayer();
        LayerManager layerManager = getModule().getLayerManager(layer);
        if (layerManager == null || layerManager.isOsm()) {
            JOptionPane.showMessageDialog(Main.panel, 
                I18n.tr("This operation is only allowed on the ODS layer;"));
            return;
        }
        
        OsmDataLayer osmLayer = (OsmDataLayer) layer;
        importer.doImport(osmLayer.data.getAllSelected());
        updater.doUpdate(osmLayer.data.getAllSelected());
        Set<Way> modifiedWays = new HashSet<>();
        modifiedWays.addAll(importer.getImportedWays());
        modifiedWays.addAll(updater.getUpdatedWays());
        if (!modifiedWays.isEmpty()) {
            BuildingAligner buildingAligner = new BuildingAligner(modifiedWays);
            buildingAligner.run();
        }

        layerManager.getOsmDataLayer().data.clearSelection();
        Main.getLayerManager().setActiveLayer(getModule().getOsmLayerManager().getOsmDataLayer());
    }

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        LayerManager layerManager = getModule().getLayerManager(newLayer);
        this.setEnabled(layerManager != null && !layerManager.isOsm());
    }
}
