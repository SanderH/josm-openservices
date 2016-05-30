package org.openstreetmap.josm.plugins.ods.entities.actual;

import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
//import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatch;
import org.openstreetmap.josm.tools.Predicate;

import com.vividsolutions.jts.geom.Geometry;

public interface Building extends Entity {
    public Geometry getGeometry();

    public City getCity();

    /**
     * Return the address information associated with this building.
     * 
     * @return null if no address is associated with the building
     */
    public Address getAddress();

    /**
     * Return the address nodes associated with this building.
     * 
     * @return empty collection if no address nodes are associated with this
     *         building.
     */
    public List<AddressNode> getAddressNodes();
    
    public List<HousingUnit> getHousingUnits();
    
    public void addHousingUnit(HousingUnit housingUnit);

    public Set<Building> getNeighbours();

    public void setStartDate(String string);
    
    public String getStartDate();

    public BuildingType getBuildingType();

    public BuildingMatch getMatch();

    public Class<Building> getBaseType();
    
    // Setters
    public void setBuildingType(BuildingType buildingType);

    public void setIncomplete(boolean incomplete);
    
    public static boolean isBuilding(OsmPrimitive primitive) {
        return ((primitive.hasKey("building") || primitive.hasKey("building:part")) &&
                (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
                || primitive.getDisplayType() == OsmPrimitiveType.MULTIPOLYGON 
                || primitive.getDisplayType() == OsmPrimitiveType.RELATION));
    }
    
    public final static Predicate<OsmPrimitive> IsBuilding = new Predicate<OsmPrimitive>() {
        @Override
        public boolean evaluate(OsmPrimitive primitive) {
            return isBuilding(primitive);
        }
    };

    public void setAddress(Address address);
}
