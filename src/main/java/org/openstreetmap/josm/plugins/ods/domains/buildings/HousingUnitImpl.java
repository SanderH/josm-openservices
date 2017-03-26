package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;

public class HousingUnitImpl extends AbstractEntity implements HousingUnit {
    private List<AddressNode> addressNodes = new LinkedList<>();
    private BuildingType type;
    private Double area;
    private Object buildingRef;
    private Building building;
    
    @Override
    public void setMainAddressNode(AddressNode addressNode) {
        if (addressNodes.isEmpty()) {
            addressNodes.add(addressNode);
        }
        else {
            addressNodes.set(0, addressNode);
        }
    }
    
    @Override
    public AddressNode getMainAddressNode() {
        if (addressNodes.isEmpty()) {
            return null;
        }
        return addressNodes.get(0);
    }

    @Override
    public List<AddressNode> getAddressNodes() {
        return addressNodes;
    }

    @Override
    public Double getArea() {
        return area;
    }

    @Override
    public void setArea(Double area) {
        this.area = area;
    }

    @Override
    public void setType(BuildingType type) {
        this.type = type;
    }

    @Override
    public BuildingType getType() {
        return type;
    }

    @Override
    public void setBuildingRef(Object reference) {
        this.buildingRef = reference;
    }

    @Override
    public Object getBuildingRef() {
        return buildingRef;
    }

    @Override
    public void setBuilding(Building building) {
        this.building = building;
    }

    @Override
    public Building getBuilding() {
        return building;
    }

    @Override
    public Class<HousingUnit> getBaseType() {
        return HousingUnit.class;
    }

//    @Override
//    public void setGeometry(Point point) {
//        super.setGeometry(point);
//    }
//
//    @Override
//    public Point getGeometry() {
//        return (Point) super.getGeometry();
//    }
}
