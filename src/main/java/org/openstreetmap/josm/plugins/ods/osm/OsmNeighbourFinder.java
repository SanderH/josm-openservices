package org.openstreetmap.josm.plugins.ods.osm;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedOgcMultiPolygon;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPolygon;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRing;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedWay;
import org.openstreetmap.josm.plugins.ods.primitives.SimpleManagedRing;

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
    private NodeDWithin dWithin;
//    private BuildingAligner buildingAligner;
//    private WayAligner wayAligner;
    
    public OsmNeighbourFinder(OdsModule module) {
        super();
        this.module = module;
        this.layerManager = module.getOsmLayerManager();
        dWithin = new NodeDWithinLatLon(module.getTolerance());
    }

    public void findNeighbours(ManagedPrimitive<?> primitive) {
        // Look for connected neighbours
        findConnectedNeighbours(primitive);
        Entity entity = primitive.getEntity();
        if (entity.getBaseType() != Building.class) {
            return;
        }
        if (primitive instanceof SimpleManagedRing) {
            findWayNeighbourBuildings((SimpleManagedRing)primitive);
        }
        else if (primitive instanceof ManagedOgcMultiPolygon) {
            ManagedOgcMultiPolygon mpg = (ManagedOgcMultiPolygon) primitive;
            ManagedPolygon polygon = mpg.getPolygons().iterator().next();
            ManagedRing<?> ring = polygon.getExteriorRing();
            if (ring instanceof SimpleManagedRing) {
                findWayNeighbourBuildings((SimpleManagedRing)ring);
            }
        }
    }
    
    private void findConnectedNeighbours(ManagedPrimitive<?> primitive) {
        if (primitive instanceof ManagedWay) {
            
        }
//        primitive.getPrimitive().get;
        
    }

    public void findWayNeighbourBuildings(SimpleManagedRing ring1) {
        DataSet dataSet = layerManager.getOsmDataLayer().data;
        BBox bbox = extend(ring1.getBBox(), module.getTolerance());
        for (Way way2 : dataSet.searchWays(bbox)) {
            if (way2.equals(ring1.getPrimitive())) {
                continue;
            }
            if (isBuildingExterior(way2)) {
                ManagedPrimitive<?> mPrimitive = layerManager.getManagedPrimitive(way2);
                if (mPrimitive != null && mPrimitive instanceof SimpleManagedRing) {
                    SimpleManagedRing ring2 = (SimpleManagedRing) mPrimitive;
                    WayAligner wayAligner = new WayAligner(ring1, ring2, dWithin, true);
                    wayAligner.run();
                }
            }
        }
    }

    private boolean isBuildingExterior(Way way) {
        if (isBuilding.test(way)) {
            return true;
        }
        for (OsmPrimitive osm :way.getReferrers()) {
            Relation relation = (Relation)osm;
            if (isBuilding.test(relation)) {
                for (RelationMember member : relation.getMembers()) {
                    if ("outer".equals(member.getRole()) &&
                            member.getMember() == way) {
                        return true;
                    }
                }
            }
        }
        return false;
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
