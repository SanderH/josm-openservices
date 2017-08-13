package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.domains.addresses.OpenDataAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

/**
 * A building unit is a separate part of a building, that is used independently from other
 * building units in the same building. Typical building units are:
 *   an apartment in an apartment building.
 *   a shop in a shopping centre.
 *   etc.
 *
 * Dutch: verblijfsobject
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface BuildingUnit extends Entity<BuildingUnitEntityType> {

    /**
     * Set the main (first) address node. In most cases this is the only addressNode.
     *
     * @param addressNode
     */
    public void setMainAddressNode(OpenDataAddressNode addressNode);

    public OpenDataAddressNode getMainAddressNode();

    public List<OpenDataAddressNode> getAddressNodes();

    public void setArea(Double area);

    public Double getArea();

    public void setBuildingRef(Object reference);

    public Object getBuildingRef();

    public void setBuilding(Building building);

    public Building getBuilding();

    public void setType(TypeOfBuilding typeOfBuilding);

    public TypeOfBuilding getType();

    //    public void setGeometry(Point point);
    //
    //    @Override
    //    public Point getGeometry();
}
