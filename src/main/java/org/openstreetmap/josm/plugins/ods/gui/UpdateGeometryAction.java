package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.tools.I18n;

/**
 * Temporary action to update a single building's geometry.
 * Used during development of this new functionality
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class UpdateGeometryAction extends OdsAction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public UpdateGeometryAction(OdsModule module) {
        super(module, "Update Geometry", (String)null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Layer layer = Main.getLayerManager().getActiveLayer();
        LayerManager layerManager = getModule().getLayerManager(layer);
        // This action should only occur when the OpenData layer is active
        assert (layerManager != null && !layerManager.isOsm());
        
        OsmDataLayer osmLayer = (OsmDataLayer) layer;
        Collection<OsmPrimitive> primitives = osmLayer.data.getAllSelected();
        Match<Building> match = null;
        if (primitives.size() == 1) {
            OsmPrimitive primitive = primitives.iterator().next();
            ManagedPrimitive<?> mPrimitive = layerManager.getManagedPrimitive(primitive);
            if (mPrimitive != null) {
                Entity entity = mPrimitive.getEntity();
                if (entity != null && entity.getBaseType() == Building.class) {
                    match = ((Building)entity).getMatch();
                }
            }
        }
        if (match == null) {
            JOptionPane.showMessageDialog(Main.panel, I18n.tr(
                "Please select exactly one building that has been matched."));
            return;
        }
        update(match);
        layerManager.getOsmDataLayer().data.clearSelection();
        Main.getLayerManager().setActiveLayer(getModule().getOsmLayerManager().getOsmDataLayer());
    }

    private void update(Match<Building> match) {
        
    }

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        LayerManager layerManager = getModule().getLayerManager(newLayer);
        this.setEnabled(layerManager != null && !layerManager.isOsm());
    }
}
