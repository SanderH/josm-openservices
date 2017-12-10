package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.storage.IdentitySet;

public interface OsmBuildingDao {

    public void add(OsmBuilding building);

    public IdentitySet<? extends OsmBuilding> listById(Object id);

}
