package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;

public class OdsResetAction extends OdsAction {

    private static final long serialVersionUID = 1L;
    private final OdsModule module;

    public OdsResetAction(OdsModule module) {
        super(module, "Reset", "Reset");
        super.putValue("description",
                "Reset ODS.");
        this.module = module;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            module.reset();
        } catch (OdsException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
