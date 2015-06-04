package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Point;

public interface AddressNode extends Entity, Address {

    public Address getAddress();

	public Object getBuildingRef();

    public void setBuilding(Building building);

    public Building getBuilding();

    public void setGeometry(Point point);
    
    public Point getGeometry();
}