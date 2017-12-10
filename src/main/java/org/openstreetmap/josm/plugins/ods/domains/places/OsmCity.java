package org.openstreetmap.josm.plugins.ods.domains.places;

import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntity;

public class OsmCity extends AbstractOsmEntity implements City {
    private String name;
    //    private MultiPolygon multiPolygon;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    //    @Override
    //    public void setGeometry(Geometry geometry) {
    //        switch (geometry.getGeometryType()) {
    //        case "MultiPolygon":
    //            multiPolygon = (MultiPolygon) geometry;
    //            break;
    //        case "Polygon":
    //            multiPolygon = geometry.getFactory().createMultiPolygon(
    //                new Polygon[] {(Polygon) geometry});
    //            break;
    //        default:
    //            // TODO intercept this exception or accept null?
    //        }
    //    }

    //    @Override
    //    public MultiPolygon getGeometry() {
    //        return multiPolygon;
    //    }

    @Override
    public boolean isIncomplete() {
        return false;
    }
}
