package org.openstreetmap.josm.plugins.ods.domains.addresses;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;

public class AddressNodeEntityType implements EntityType {
    public static final AddressNodeEntityType INSTANCE = new AddressNodeEntityType();

    private AddressNodeEntityType() {
        // Hide constructor because this is a singleton class
    }

    public static boolean isAddressNode(OsmPrimitive primitive) {
        return (primitive.hasKey("addr:housenumber") &&
                (primitive.getDisplayType() == OsmPrimitiveType.NODE));
    }

    @Override
    public boolean canHandle(OsmPrimitive primitive) {
        return isAddressNode(primitive);
    }

    @Override
    public String toString() {
        return "Address node";
    }
}
