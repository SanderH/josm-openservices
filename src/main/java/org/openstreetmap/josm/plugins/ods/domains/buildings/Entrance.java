package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface Entrance extends Entity {
    public String getType();

    public void setType(String type);
}
