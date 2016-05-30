package org.openstreetmap.josm.plugins.ods.osm;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRing;
import org.openstreetmap.josm.plugins.ods.primitives.SimpleManagedRing;
import org.openstreetmap.josm.tools.Geometry;
import org.openstreetmap.josm.tools.Geometry.PolygonIntersection;
import org.openstreetmap.josm.tools.Predicate;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Find neighbours for a Building using the Osm primitive.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmNeighbourFinder {
    private final OdsModule module;
    private final LayerManager layerManager;
    private Predicate<OsmPrimitive> isBuilding = Building.IsBuilding;
    private List<OsmPrimitive> neighbourBuildings = new LinkedList<>();
    private BuildingAligner buildingAligner;
    
    public OsmNeighbourFinder(OdsModule module) {
        super();
        this.module = module;
        this.layerManager = module.getOsmLayerManager();
        this.buildingAligner = new BuildingAligner(module, layerManager);
    }

    public void findNeighbours(ManagedPrimitive<?> osm) {
        Entity entity = osm.getEntity();
        if (entity.getBaseType() != Building.class) {
            return;
        }
        if (osm instanceof SimpleManagedRing) {
            findWayNeighbourBuildings((SimpleManagedRing)osm);
        }
    }
    
    public void findWayNeighbourBuildings(SimpleManagedRing ring) {
        DataSet dataSet = layerManager.getOsmDataLayer().data;
        Envelope envelope = extend(ring.getEnvelope(), module.getTolerance());
        for (Way way2 : dataSet.searchWays(bbox)) {
            if (way2.equals(way1)) {
                continue;
            }
            if (isBuilding.evaluate(way2)) {
                buildingAligner.align(way1, way2);
//                PolygonIntersection pi = Geometry.polygonIntersection(way1.getNodes(), way2.getNodes());
//                if (pi.equals(PolygonIntersection.CROSSING)) {
//                    neighbourBuildings.add(way2);
//                }
            }
            for (OsmPrimitive osm2 :way2.getReferrers()) {
                Relation relation = (Relation)osm2;
                if (isBuilding.evaluate(relation)) {
                    buildingAligner.align(way1, way1);
//                    neighbourBuildings.add(relation);
                }
            }
        }
    }

    private BBox extend(BBox bbox, Double delta) {
        return new BBox(bbox.getTopLeftLon() - delta,
            bbox.getBottomRightLat() - delta,
            bbox.getBottomRightLon() + delta,
            bbox.getTopLeftLat() + delta);
    }
    
    private Envelope extend(Envelope bbox, Double delta) {
        return new Envelope(bbox.getMinX() - delta,
            bbox.getMaxX() + delta,
            bbox.getMinY() + delta,
            bbox.getMaxY() + delta);
    }

}
