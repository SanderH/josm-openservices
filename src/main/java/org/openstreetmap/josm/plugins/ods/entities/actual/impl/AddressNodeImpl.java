package org.openstreetmap.josm.plugins.ods.entities.actual.impl;

import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.HousingUnit;

import com.vividsolutions.jts.geom.Point;

public class AddressNodeImpl extends AbstractEntity implements AddressNode {
    private Address address;
    private HousingUnit housingUnit;
    private Building building;
    
    public AddressNodeImpl() {
        super();
    }
    
    @Override
    public Class<AddressNode> getBaseType() {
        return AddressNode.class;
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
    public boolean isIncomplete() {
        return getBuilding() == null || getBuilding().isIncomplete();
    }

    
    @Override
    public void setHousingUnit(HousingUnit housingUnit) {
        this.housingUnit = housingUnit;
    }

    @Override
    public HousingUnit getHousingUnit() {
        return housingUnit;
    }

    @Override
    public void setBuilding(Building building) {
        this.building = building;
    }

    @Override
    public Building getBuilding() {
        if (building != null) {
            return building;
        }
        if (housingUnit != null) {
            return housingUnit.getBuilding();
        }
        return null;
    }

    @Override
    public void setGeometry(Point point) {
        super.setGeometry(point);
    }

    @Override
    public Point getGeometry() {
        return (Point) super.getGeometry();
    }
    
    public String toString() {
        return getAddress().toString();
    }
}
