package org.openstreetmap.josm.plugins.ods.entities.actual.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Addressable;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.BuildingType;
import org.openstreetmap.josm.plugins.ods.entities.actual.City;
import org.openstreetmap.josm.plugins.ods.entities.actual.HousingUnit;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

public class BuildingImpl extends AbstractEntity implements Building {
    private Address address;
    private List<HousingUnit> housingUnits = new LinkedList<>();
    private Set<AddressNode> addressNodes = new HashSet<>();
    private BuildingType buildingType = BuildingType.UNCLASSIFIED;
    private String startDate;
    private City city;
    
    @Override
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String getStartDate() {
         return startDate;
    }

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
//        ManagedPrimitive mPrimitive = this.getPrimitive();
//        if (mPrimitive != null) {
//            mPrimitive.putAll(buildingType.getTags());
//        }
    }

    @Override
    public Building getBuilding() {
        return this;
    }

    @Override
    public Set<Object> getBuildingIds() {
        return Collections.singleton(getReferenceId());
    }

    @Override
    public City getCity() {
        return city;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }
    
    @Override
    public Address getAddress() {
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
    public Set<? extends Addressable> getAddressables() {
        if (getAddress() == null) {
            return getAddressNodes();
        }
        Set<Addressable> result = new HashSet<>(getAddressNodes());
        result.add(this);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Building ");
        sb.append(getReferenceId() == null ? "without id" : getReferenceId());
        if (address != null) {
            sb.append("\n").append(address.getFullHouseNumber());
        }
        for (AddressNode a : getAddressNodes()) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }
}
