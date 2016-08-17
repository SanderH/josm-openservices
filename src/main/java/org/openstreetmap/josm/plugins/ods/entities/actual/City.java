package org.openstreetmap.josm.plugins.ods.entities.actual;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.MultiPolygon;

public interface City extends Entity {
    public String getName();
    public MultiPolygon getGeometry();

    public Class<City> getBaseType();

    public void setName(String name);
}
