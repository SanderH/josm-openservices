package org.openstreetmap.josm.plugins.ods.domains.places;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface City extends Entity {
    public String getName();

    public void setName(String name);
}
