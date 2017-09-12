package org.openstreetmap.josm.plugins.ods.gui;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.MainLayerManager.ActiveLayerChangeEvent;
import org.openstreetmap.josm.gui.layer.MainLayerManager.ActiveLayerChangeListener;
import org.openstreetmap.josm.plugins.ods.OdsModule;

public abstract class OdsAction extends AbstractAction implements ActiveLayerChangeListener {

    private static final long serialVersionUID = 1L;

    private final OdsModule module;

    public OdsAction(OdsModule module, String name, String description) {
        super(name);
        super.putValue("description", description);
        this.module = module;
        MainApplication.getLayerManager().addActiveLayerChangeListener(this);
    }

    public OdsAction(OdsModule module, String name, ImageIcon imageIcon) {
        super(name, imageIcon);
        this.module = module;
        MainApplication.getLayerManager().addActiveLayerChangeListener(this);
    }

    public OdsModule getModule() {
        return module;
    }

    @Override
    public void activeOrEditLayerChanged(ActiveLayerChangeEvent e) {
        // Override if the implementing action wants to know about this event.
    }
}
