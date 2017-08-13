package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;

public class OpenDataBuildingUnit extends AbstractEntity<BuildingUnitEntityType> implements BuildingUnit {
    private final List<OpenDataAddressNode> addressNodes = new LinkedList<>();
    private TypeOfBuilding type;
    private Double area;
    private Object buildingRef;
    private Building building;

    @Override
    public void setMainAddressNode(OpenDataAddressNode addressNode) {
        if (addressNodes.isEmpty()) {
            addressNodes.add(addressNode);
        }
        else {
            addressNodes.set(0, addressNode);
        }
    }

    @Override
    public OpenDataAddressNode getMainAddressNode() {
        if (addressNodes.isEmpty()) {
            return null;
        }
        return addressNodes.get(0);
    }

    @Override
    public List<OpenDataAddressNode> getAddressNodes() {
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
    public void setType(TypeOfBuilding type) {
        this.type = type;
    }

    @Override
    public TypeOfBuilding getType() {
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
}
