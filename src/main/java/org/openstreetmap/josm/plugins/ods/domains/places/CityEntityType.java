package org.openstreetmap.josm.plugins.ods.domains.places;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;

public class CityEntityType implements EntityType {
    public final static CityEntityType INSTANCE = new CityEntityType();

    private CityEntityType() {
        // Hide public constructor because of singleton
    }

    @Override
    public boolean canHandle(OsmPrimitive primitive) {
        return "administrative".equals(primitive.get("boundary")) &&
                "10".equals(primitive.get("admin_level"));
    }


    @Override
    public String toString() {
        return "City";
    }

}
