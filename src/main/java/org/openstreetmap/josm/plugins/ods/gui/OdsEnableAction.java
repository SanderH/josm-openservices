package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.ods.ModuleActivationException;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class OdsEnableAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final OpenDataServicesPlugin ods;
    private final OdsModule module;

    public OdsEnableAction(OpenDataServicesPlugin ods, OdsModule module) {
        super(module.getName());
        super.putValue("description",
                "Switch ODS between enabled and disabled state");
        this.ods = ods;
        this.module = module;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            ods.activate(module);
            Layer activeLayer = null;
            if (MainApplication.getMap() != null) {
                activeLayer = MainApplication.getLayerManager().getActiveLayer();
            }
            if (activeLayer != null) {
                MainApplication.getLayerManager().setActiveLayer(activeLayer);
            }
            Bounds bounds = new Bounds(
                    Main.pref.get("openservices.download.bounds"), ";");
            // Zoom to the last used bounds
            MainApplication.getMap().mapView.zoomTo(bounds);
        }
        catch (ModuleActivationException e) {
            if (e == ModuleActivationException.CANCELLED) {
                return;
            }
            Logging.error(e);
            String msg = I18n.tr("The module could not be activated because of the following error(s):") +
                    "\n" + e.getMessage();
            JOptionPane.showMessageDialog(MainApplication.getMainPanel(), msg, "Module not available", JOptionPane.ERROR_MESSAGE);
        }
    }
}
