package org.openstreetmap.josm.plugins.ods.domains.addresses;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.domains.buildings.HousingUnit;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedNode;

public class AddressNodeImpl extends AbstractEntity implements AddressNode {
    private Address address;
    private HousingUnit housingUnit;
    private Building building;
    private Set<Building> buildings;

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
    public EntityStatus getStatus() {
        if (getBuilding() == null) {
            return EntityStatus.UNKNOWN;
        }
        EntityStatus status = super.getStatus();
        if ((status == EntityStatus.CONSTRUCTION || status == EntityStatus.UNKNOWN) &&
                getBuilding().getStatus() == EntityStatus.PLANNED) {
            return EntityStatus.PLANNED;
        }
        if (getBuilding().getStatus() == EntityStatus.REMOVAL_DUE) {
            return EntityStatus.REMOVAL_DUE;
        }
        return status;
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
    public void addBuilding(Building b) {
        if (building == null) {
            if (buildings != null) {
                buildings.add(b);
            }
            else {
                building = b;
            }
        }
        else {
            if (!building.equals(b)) {
                this.buildings = new HashSet<>();
                this.buildings.add(building);
                this.buildings.add(b);
                this.building = null;
            }
        }
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
    public Set<Building> getBuildings() {
        return buildings;
    }

    @Override
    public Set<Object> getBuildingIds() {
        if (building != null) {
            return Collections.singleton(building.getReferenceId());
        }
        if (buildings == null) {
            return Collections.emptySet();
        }
        Set<Object> result = new HashSet<>();
        for (Building b : getBuildings()) {
            if (b.getReferenceId() != null) {
                result.add(b.getReferenceId());
            }
        }
        return result;
    }

    @Override
    public ManagedNode getPrimitive() {
        return (ManagedNode) super.getPrimitive();
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

    @Override
    public String toString() {
        return getAddress().toString();
    }
}
