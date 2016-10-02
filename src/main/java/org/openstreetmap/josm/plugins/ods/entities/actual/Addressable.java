package org.openstreetmap.josm.plugins.ods.entities.actual;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    
    public static Set<Object> getBuildingIds(Collection<Addressable> addressables) {
        if (addressables.size() == 1) {
            return addressables.iterator().next().getBuildingIds();
        }
        Set<Object> ids = new HashSet<>();
        for (Addressable addressable : addressables) {
            ids.addAll(addressable.getBuildingIds());
        }
        return ids;
    }
}
