package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.places.City;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntity;

public class OsmBuilding extends AbstractOsmEntity implements Building {
    private Optional<Address> address = Optional.empty();
    private final Set<OsmAddressNode> addressNodes = new HashSet<>();
    private TypeOfBuilding typeOfBuilding = TypeOfBuilding.UNCLASSIFIED;
    private City city;

    @Override
    public TypeOfBuilding getBuildingType() {
        return typeOfBuilding;
    }

    @Override
    public void setBuildingType(TypeOfBuilding typeOfBuilding) {
        this.typeOfBuilding = typeOfBuilding;
    }

    //    @Override
    //    public Set<Object> getBuildingIds() {
    //        return Collections.singleton(getReferenceId());
    //    }
    //
    @Override
    public City getCity() {
        return city;
    }

    public void setAddress(Address address) {
        this.address = Optional.of(address);
    }

    @Override
    public Optional<Address> getAddress() {
        return address;
    }

    @Override
    public Set<OsmAddressNode> getAddressNodes() {
        return addressNodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Building ");
        //        sb.append(getReferenceId() == null ? "without id" : getReferenceId());
        if (address.isPresent()) {
            sb.append("\n").append(address.get().getFullHouseNumber());
        }
        for (AddressNode a : getAddressNodes()) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }
}
