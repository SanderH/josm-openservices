package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

import org.locationtech.jts.geom.Geometry;

public interface OsmBuilding extends OsmEntity, Building {
    public static boolean IsBuilding(OsmPrimitive primitive) {
        boolean taggedAsBuilding = primitive.hasKey("building") || primitive.hasKey("building:part")
                || primitive.hasKey("no:building");
        boolean validGeometry = (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
                || primitive.getDisplayType() == OsmPrimitiveType.MULTIPOLYGON
                || primitive.getDisplayType() == OsmPrimitiveType.RELATION);
        return taggedAsBuilding && validGeometry;
    }

    /**
     * <p>In most municipalities, the staanplaatsen have geometry of the kavel 
     * which are tagged as landuse=static_caravan with all the BAG tags with 
     * a separate building (if any) with only building=static_caravan which 
     * is usually not in BAG (in contrast to some the sheds which usually 
     * have their own BAG object.
     * </p>
     * <p>Other municipalities have the geometry of the staanplaats based
     * on the current static caravan, these will have the BAG tags on the building without a landuse tag.
     * </p> 
     * 
     * @param primitive
     * @return
     */
    public static boolean IsStaticCaravan(OsmPrimitive primitive) {
        boolean taggedAsLanduseStaticCaravan = primitive.hasTag("landuse", "static_caravan");
        boolean validGeometry = (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
                || primitive.getDisplayType() == OsmPrimitiveType.MULTIPOLYGON
                || primitive.getDisplayType() == OsmPrimitiveType.RELATION);
        return taggedAsLanduseStaticCaravan && validGeometry;
    }
    
    /**
     * <p>In most municipalities, the ligplaatsen have geometry of the spot 
     * where the houseboat can be moored, sometimes including solid ground 
     * where a shed may be located.
     * </p>
     * <p>Other municipalities have the geometry of the ligplaats based 
     * on the current houseboat (or museum ship).
     * </p>
     * <p>In Hellevoetsluis, there are many overlapping ligplaatsen with 
     * a geometry of the entire marina. Here the houseboats don't 
     * have a fixed spot and may moor everywhere within the marina.
     * </p>
     * 
     * @param primitive
     * @return
     */
    public static boolean IsMooring(OsmPrimitive primitive) {
        boolean taggedAsMooring = primitive.hasTag("mooring", "yes");
        boolean validGeometry = (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
                || primitive.getDisplayType() == OsmPrimitiveType.MULTIPOLYGON
                || primitive.getDisplayType() == OsmPrimitiveType.RELATION);
        return taggedAsMooring && validGeometry;
    }

    @Override
    public Geometry getGeometry();

    public OsmCity getCity();

    /**
     * Return the address information associated with this building.
     *
     * @return null if no address is associated with the building
     */
    public OsmAddress getAddress();

    /**
     * Return the address nodes associated with this building.
     *
     * @return empty collection if no address nodes are associated with this
     *         building.
     */
    public List<OsmAddressNode> getAddressNodes();

    public Set<OsmBuilding> getNeighbours();

    /**
     * Check is the full area of this building has been loaded. This is true if
     * the building is completely covered by the downloaded area.
     *
     * @return
     */

    public void setStartDate(String string);
}
