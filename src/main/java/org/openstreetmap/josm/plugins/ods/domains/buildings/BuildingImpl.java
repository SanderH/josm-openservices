package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.places.City;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;

public class BuildingImpl extends AbstractEntity<Building> implements Building {
    private Optional<Address> address = Optional.empty();
    private final List<HousingUnit> housingUnits = new LinkedList<>();
    private final Set<AddressNode> addressNodes = new HashSet<>();
    private BuildingType buildingType = BuildingType.UNCLASSIFIED;
    private City city;

    @Override
    public Class<Building> getBaseType() {
        return Building.class;
    }

    @Override
    public BuildingType getBuildingType() {
        return buildingType;
    }

    @Override
    public void setBuildingType(BuildingType buildingType) {
        this.buildingType = buildingType;
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

    @Override
    public void setAddress(Address address) {
        this.address = Optional.of(address);
    }

    @Override
    public Optional<Address> getAddress() {
        return address;
    }

    @Override
    public void addHousingUnit(HousingUnit housingUnit) {
        housingUnits.add(housingUnit);
        addressNodes.addAll(housingUnit.getAddressNodes());
    }

    @Override
    public List<HousingUnit> getHousingUnits() {
        return housingUnits;
    }

    @Override
    public Set<AddressNode> getAddressNodes() {
        return addressNodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Building ");
        sb.append(getReferenceId() == null ? "without id" : getReferenceId());
        if (address.isPresent()) {
            sb.append("\n").append(address.get().getFullHouseNumber());
        }
        for (AddressNode a : getAddressNodes()) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }
}
