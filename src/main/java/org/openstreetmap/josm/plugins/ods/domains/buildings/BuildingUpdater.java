package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.update.DefaultEntityUpdater;

public class BuildingUpdater extends DefaultEntityUpdater<Building> {
    public BuildingUpdater(OdsModule module) {
        super(module, Building.class, new BuildingGeometryUpdater(module));
    }
}
