package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.update.DefaultEntityUpdater;

public class BuildingUpdater extends DefaultEntityUpdater<Building> {
    public BuildingUpdater(OdsModule module) {
        super(module, Building.class, new BuildingGeometryUpdater(module));
    }

    @Override
    protected void updateAttributes(Building odEntity, Building osmEntity) {
        super.updateAttributes(odEntity, osmEntity);
        ManagedPrimitive osmPrimitive = osmEntity.getPrimitive();
        osmEntity.setStartDate(odEntity.getStartDate());
        osmPrimitive.put("start_date", odEntity.getPrimitive().get("start_date"));
    }
}
