package org.openstreetmap.josm.plugins.ods.domains.addresses;

import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.Building;
import org.openstreetmap.josm.plugins.ods.domains.buildings.HousingUnit;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedNode;

public interface AddressNode extends Addressable {

    @Override
    public ManagedNode getPrimitive();

    public void setHousingUnit(HousingUnit housingUnit);

    public HousingUnit getHousingUnit();

    /**
     * Add a building to this address node.
     * An address node is typically contained in exactly 1 building,
     * but there may be exceptions. Therefore there is a possibility to 
     * add multiple buildings. 
     * @param building
     */
    public void addBuilding(Building building);

    /**
     * Get this address node's building.
     * @return The building to which this address node belongs.
     *     null if there are 0 or more than 1 buildings
     */
    public Building getBuilding();

    /**
     * Get this address node's buildings in case the address is
     * contained in more than 1 building.
     * @return A set of buildings or null if there is not more than 1
     */
    public Set<Building> getBuildings();
    
//    public void setGeometry(Point point);
//    
//    @Override
//    public Point getGeometry();
    
    @Override
    public Class<AddressNode> getBaseType();
    
    public static boolean isAddressNode(OsmPrimitive primitive) {
        return (primitive.hasKey("addr:housenumber") &&
                (primitive.getDisplayType() == OsmPrimitiveType.NODE));
    }
}
