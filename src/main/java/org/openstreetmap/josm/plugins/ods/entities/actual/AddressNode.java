package org.openstreetmap.josm.plugins.ods.entities.actual;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Point;

//public interface AddressNode extends Entity, Address {
public interface AddressNode extends Entity {

    public void setAddress(Address address);

    public Address getAddress();

//	public Object getBuildingRef();

    public void setBuilding(Building building);

    public Building getBuilding();

    public void setGeometry(Point point);
    
    public Point getGeometry();
    
    public Class<AddressNode> getBaseType();
    
    public static boolean isAddressNode(OsmPrimitive primitive) {
        return (primitive.hasKey("addr:housenumber") &&
                (primitive.getDisplayType() == OsmPrimitiveType.NODE));
    }
}
