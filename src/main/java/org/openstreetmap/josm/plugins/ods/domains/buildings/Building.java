package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.Optional;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.places.City;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface Building extends Entity {

    /**
     * Get the (optional) address of this building.
     * @return
     */
    public Optional<Address> getAddress();

    public City getCity();

    /**
     * Return the address nodes associated with this building.
     *
     * @return empty collection if no address nodes are associated with this
     *         building.
     */
    public Set<? extends AddressNode> getAddressNodes();

    public TypeOfBuilding getBuildingType();

    // Setters
    public void setBuildingType(TypeOfBuilding typeOfBuilding);
}
