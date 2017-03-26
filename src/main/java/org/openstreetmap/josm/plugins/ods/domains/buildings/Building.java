package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.domains.addresses.AddressNode;
import org.openstreetmap.josm.plugins.ods.domains.addresses.Addressable;
import org.openstreetmap.josm.plugins.ods.domains.places.City;

public interface Building extends Addressable {

    public City getCity();

    /**
     * Return the address nodes associated with this building.
     * 
     * @return empty collection if no address nodes are associated with this
     *         building.
     */
    public Set<AddressNode> getAddressNodes();

    public Set<? extends Addressable> getAddressables();

    public List<HousingUnit> getHousingUnits();
    
    public void addHousingUnit(HousingUnit housingUnit);

    public BuildingType getBuildingType();

    @Override
    public Class<Building> getBaseType();
    
    // Setters
    public void setBuildingType(BuildingType buildingType);

    @Override
    public void setIncomplete(boolean incomplete);
    
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
