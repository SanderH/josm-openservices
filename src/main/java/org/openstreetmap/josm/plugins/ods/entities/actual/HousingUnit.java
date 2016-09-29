package org.openstreetmap.josm.plugins.ods.entities.actual;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
//import org.openstreetmap.josm.plugins.ods.entities.EntityType;

import com.vividsolutions.jts.geom.Point;

/**
 * A housing unit is a separate part of a building, that is used independently from other
 * housing units in the same building. Typical housing units are:
 *   an apartment in an apartment building.
 *   a shop in a shopping centre.
 *   etc.
 * 
 * Dutch: verblijfsobject
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface HousingUnit extends Entity {

    /**
     * Set the main (first) address node. In most cases this is the only addressNode.
     * 
     * @param addressNode
     */
    public void setMainAddressNode(AddressNode addressNode);
    
    public AddressNode getMainAddressNode();
    
    public List<AddressNode> getAddressNodes();
    
    public void setArea(Double area);
    
    public Double getArea();

    public void setBuildingRef(Object reference);

    public Object getBuildingRef();

    public void setBuilding(Building building);

    public Building getBuilding();
    
    public void setType(BuildingType buildingType);
    
    public BuildingType getType();

//    public void setGeometry(Point point);
//    
//    @Override
//    public Point getGeometry();
    
    @Override
    public Class<HousingUnit> getBaseType();
}
