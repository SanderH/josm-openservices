package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.domains.addresses.Address;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.places.City;
import org.openstreetmap.josm.plugins.ods.entities.StartDate;
import org.openstreetmap.josm.plugins.ods.entities.opendata.AbstractOdEntity;

public class OpenDataBuilding extends AbstractOdEntity<BuildingEntityType> implements Building {
    private Optional<Address> address = Optional.empty();
    private final Set<BuildingUnit> buildingUnits = new HashSet<>();
    private final Set<OpenDataAddressNode> addressNodes = new HashSet<>();
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

    public void addBuildingUnit(BuildingUnit buildingUnit) {
        buildingUnits.add(buildingUnit);
        addressNodes.addAll(buildingUnit.getAddressNodes());
    }

    public Set<BuildingUnit> getBuildingUnits() {
        return buildingUnits;
    }

    @Override
    public Set<OpenDataAddressNode> getAddressNodes() {
        return addressNodes;
    }

    @Override
    public boolean isCreationRequired() {
        if (!isMissing()) return false;
        switch (getStatus()) {
        case PLANNED:
        case REMOVAL_DUE:
        case REMOVED:
        case NOT_REALIZED:
            return false;
        default:
            StartDate startDate = this.getStartDate();
            if (startDate != null) {
                return startDate.getAge().normalized().getMonths() <= 48;
            }
            return false;
        }
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
