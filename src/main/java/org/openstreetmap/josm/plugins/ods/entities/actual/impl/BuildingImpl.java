package org.openstreetmap.josm.plugins.ods.entities.actual.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.BuildingType;
import org.openstreetmap.josm.plugins.ods.entities.actual.City;
import org.openstreetmap.josm.plugins.ods.entities.actual.HousingUnit;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatch;

public class BuildingImpl extends AbstractEntity implements Building {
    private Address address;
    private List<HousingUnit> housingUnits = new LinkedList<>();
    private List<AddressNode> addressNodes = new LinkedList<>();
    private BuildingType buildingType = BuildingType.UNCLASSIFIED;
    private String startDate;
    private Set<Building> neighbours = new HashSet<>();
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

    public void setBuildingType(BuildingType buildingType) {
        this.buildingType = buildingType;
    }

    @Override
    public City getCity() {
        return city;
    }

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

    public List<AddressNode> getAddressNodes() {
        return addressNodes;
    }
    
    @Override
    public Set<Building> getNeighbours() {
        return neighbours;
    }

    @Override
    public BuildingMatch getMatch() {
        return (BuildingMatch) super.getMatch();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Building ").append(getReferenceId());
        if (address != null) {
            sb.append("\n").append(address.getFullHouseNumber());
        }
        for (AddressNode a : getAddressNodes()) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }
}
