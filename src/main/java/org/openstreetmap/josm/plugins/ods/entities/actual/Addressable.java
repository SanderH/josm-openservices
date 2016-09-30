package org.openstreetmap.josm.plugins.ods.entities.actual;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface Addressable extends Entity {
    public void setAddress(Address address);
    /**
     * Return the address information.
     * 
     * @return null if no address is associated with this object
     */
    public Address getAddress();
    
    public Building getBuilding();
}
