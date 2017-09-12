package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.MainLayerManager.ActiveLayerChangeEvent;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingAligner;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingEntityType;
import org.openstreetmap.josm.tools.I18n;

/**
 * Temporary action to align a single building's geometry to it's surroundings..
 * Used during development of this new functionality
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class AlignBuildingAction extends OdsAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AlignBuildingAction(OdsModule module) {
        super(module, "Align Geometry", (String)null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Layer layer = MainApplication.getLayerManager().getActiveLayer();
        LayerManager layerManager = getModule().getLayerManager(layer);
        // This action should only occur when the OpenData layer is active

        OsmDataLayer osmLayer = (OsmDataLayer) layer;
        Collection<Way> ways = osmLayer.data.getSelectedWays().stream()
                .filter(BuildingEntityType::isBuildingWay).collect(Collectors.toList());
        if (ways.isEmpty()) {
            JOptionPane.showMessageDialog(MainApplication.getMainPanel(), I18n.tr(
                    "Please select at least one way that is part of a building."));
            return;
        }
        align(ways, osmLayer);
        layerManager.getOsmDataLayer().data.clearSelection();
    }

    private static void align(Collection<Way> ways, OsmDataLayer osmLayer) {
        BuildingAligner buildingAligner = new BuildingAligner(ways);
        buildingAligner.run();
    }

    @Override
    public void activeOrEditLayerChanged(ActiveLayerChangeEvent e) {
        Layer newLayer = MainApplication.getLayerManager().getActiveLayer();
        LayerManager layerManager = getModule().getLayerManager(newLayer);
        this.setEnabled(layerManager != null);
    }
}
