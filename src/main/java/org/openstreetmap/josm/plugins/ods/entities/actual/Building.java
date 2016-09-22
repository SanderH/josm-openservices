package org.openstreetmap.josm.plugins.ods.entities.actual;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
//import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatch;

public interface Building extends Entity {

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

    @Override
    public BuildingMatch getMatch();

    @Override
    public Class<Building> getBaseType();
    
    // Setters
    public void setBuildingType(BuildingType buildingType);

    @Override
    public void setIncomplete(boolean incomplete);
    
    public void setAddress(Address address);
    
    public final static Predicate<OsmPrimitive> IsBuilding = new Predicate<OsmPrimitive>() {
        @Override
        public boolean test(OsmPrimitive primitive) {
            return ((primitive.hasKey("building") || primitive.hasKey("building:part")) &&
                    (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
                    || primitive.getDisplayType() == OsmPrimitiveType.MULTIPOLYGON 
                    || primitive.getDisplayType() == OsmPrimitiveType.RELATION));
        }
    };
    
    public final static Predicate<Way> IsBuildingWay = new Predicate<Way>() {
        @Override
        public boolean test(Way way) {
            if (isBuilding(way)) {
                return true;
            }
            for (OsmPrimitive osm: way.getReferrers()) {
                if (isBuilding(osm)) {
                    return true;
                }
            }
            return false;
        }
        
        private boolean isBuilding(OsmPrimitive osm) {
            return Building.IsBuilding.test(osm);
        }
    };
}
