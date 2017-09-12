package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.statistics.EntityStatistics;

public class OdsStatisticsAction extends OdsAction {

    /**
     * Show statistics for debug purposes.
     */
    private static final long serialVersionUID = 1L;

    public OdsStatisticsAction(OdsModule module) {
        super(module, "Statistics", "Show statistics");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EntityStatistics statistics = new EntityStatistics();
        statistics.run();
    }

}
