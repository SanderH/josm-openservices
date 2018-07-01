package org.openstreetmap.josm.plugins.ods.osm;

import java.util.function.Predicate;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;

/**
 * Find neighbours for a OdBuilding using the Osm primitive.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmNeighbourFinder {
    private final Predicate<OsmPrimitive> isBuilding = OsmBuilding::IsBuilding;
    private final OsmBuildingAligner osmBuildingAligner;
    private final Double tolerance;

    public OsmNeighbourFinder(OsmBuildingAligner osmBuildingAligner, Double tolerance) {
        super();
        this.osmBuildingAligner = osmBuildingAligner;
        this.tolerance = tolerance;
    }

    public void findNeighbours(OsmPrimitive osm) {
        if (!isBuilding.test(osm)) {
            return;
        }
        if (osm.getDisplayType().equals(OsmPrimitiveType.CLOSEDWAY)) {
            findWayNeighbourBuildings((Way)osm);
        }
    }

    public void findWayNeighbourBuildings(Way way1) {
        BBox bbox = extend(way1.getBBox(), tolerance);
        for (Way way2 : way1.getDataSet().searchWays(bbox)) {
            if (way2.equals(way1)) {
                continue;
            }
            if (isBuilding.test(way2)) {
                osmBuildingAligner.align(way1, way2);
                //                PolygonIntersection pi = Geometry.polygonIntersection(way1.getNodes(), way2.getNodes());
                //                if (pi.equals(PolygonIntersection.CROSSING)) {
                //                    neighbourBuildings.add(way2);
                //                }
            }
            for (OsmPrimitive osm2 :way2.getReferrers()) {
                Relation relation = (Relation)osm2;
                if (isBuilding.test(relation)) {
                    osmBuildingAligner.align(way1, way1);
                    //                    neighbourBuildings.add(relation);
                }
            }
        }
    }

    private static BBox extend(BBox bbox, Double delta) {
        return new BBox(bbox.getTopLeftLon() - delta,
                bbox.getBottomRightLat() - delta,
                bbox.getBottomRightLon() + delta,
                bbox.getTopLeftLat() + delta);
    }
}
