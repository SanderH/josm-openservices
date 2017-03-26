package org.openstreetmap.josm.plugins.ods.domains.places;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface City extends Entity {
    public String getName();

    @Override
    public Class<City> getBaseType();

    public void setName(String name);
}
