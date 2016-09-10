package org.openstreetmap.josm.plugins.ods.osm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedNode;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedOgcMultiPolygon;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedOgcMultiPolygonImpl;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPolygon;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPolygonImpl;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRelation;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRelationImpl;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRelationMember;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRing;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedWay;
import org.openstreetmap.josm.plugins.ods.primitives.SimpleManagedWay;
import org.openstreetmap.josm.plugins.ods.primitives.SimpleManagedPolygon;
import org.openstreetmap.josm.plugins.ods.primitives.SimpleManagedRing;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * The default implementation of PrimitiveBuilder.
 * 
 * @author Gertjan Idema
 * 
 */
public class DefaultPrimitiveBuilder implements OsmPrimitiveFactory {
    private final LayerManager layerManager;
    private ManagedNodeSet nodeSet;

    public DefaultPrimitiveBuilder(LayerManager layerManager) {
        this.layerManager = layerManager;
        this.nodeSet = layerManager.getManagedNodes();
    }

    @Override
    public LayerManager getLayerManager() {
        return layerManager;
    }
    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Geometry)
     */
    @Override
    public ManagedPrimitive create(Geometry geometry, Map<String, String> tags) {
        tags.put(ODS.KEY.BASE, "true");
        switch (geometry.getGeometryType()) {
        case "Polygon":
            return buildArea((Polygon)geometry, tags);
        case "MultiPolygon":
            return buildArea((MultiPolygon)geometry, tags);
        case "Point":
            return build((Point)geometry, tags);
        case "MultiPoint":
            return build((MultiPoint)geometry, tags);
        case "LineString":
            return build((LineString)geometry, tags);
        case "MultiLineString":
            return build((MultiLineString)geometry, tags);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public ManagedPrimitive create(Polygon polygon, Map<String, String> tags) {
        return buildArea(polygon, tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
    public ManagedPrimitive build(MultiPolygon mpg, Map<String, String> tags) {
        return buildArea(mpg, tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#build(com.vividsolutions.jts.geom.Point)
     */
    @Override
    public ManagedNode build(Point point, Map<String, String> tags) {
        ManagedNode node = buildNode(point, tags, false);
        return node;
    }

    public ManagedNode build(MultiPoint points, Map<String, String> tags) {
        ManagedNode node = buildNode((Point)points.getGeometryN(0), tags, false);
        return node;
    }

    @Override
    public ManagedPrimitive build(LineString ls, Map<String, String> tags) {
        int numPoints = ls.getNumPoints();
        if (numPoints < 1600) {
            ManagedWay way = buildWay(ls, tags);
            return way;
        }
        // Implement handling of lineStrings with more than200 points
        throw new UnsupportedOperationException(I18n.tr(
            "Lines with more than 2000 nodes are not supported"));
    }
    
    @Override
    public ManagedPrimitive build(MultiLineString mls, Map<String, String> tags) {
        // TODO implement this by creating an OdsPrimitiveGroup relation
        //        OsmPrimitive primitive = build((LineString)mls.getGeometryN(i), tags));
        return build((LineString)mls.getGeometryN(0), tags);
    }
    
    
    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildArea(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
    public ManagedPrimitive buildArea(MultiPolygon mpg, Map<String, String> tags) {
        ManagedPrimitive primitive;
        if (mpg.getNumGeometries() > 1) {
            Map<String, String> keys = new HashMap<>();
            keys.putAll(tags);
            keys.put("type", "multipolygon");
            primitive = buildMultiPolygon(mpg, keys);
        } else {
            primitive = buildArea((Polygon) mpg.getGeometryN(0), tags);
        }
        return primitive;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildArea(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public ManagedPrimitive buildArea(Polygon polygon, Map<String, String> tags) {
        ManagedPrimitive managedPrimitive;
        if (polygon.getNumInteriorRing() > 0) {
            Map<String, String> keys = new HashMap<>();
            keys.putAll(tags);
            keys.put("type", "multipolygon");
            managedPrimitive = buildMultiPolygon(polygon, keys);
        }
        else {
            managedPrimitive = buildSimplePolygon(polygon.getExteriorRing(), tags);
        }
        return managedPrimitive;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildMultiPolygon(com.vividsolutions.jts.geom.Polygon)
     */
    @Override
    public ManagedRelation buildMultiPolygon(Polygon polygon, Map<String, String> tags) {
        MultiPolygon multiPolygon = polygon.getFactory().createMultiPolygon(
                new Polygon[] { polygon });
        return buildMultiPolygon(multiPolygon, tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildMultiPolygon(com.vividsolutions.jts.geom.MultiPolygon)
     */
    @Override
    public ManagedOgcMultiPolygon buildMultiPolygon(MultiPolygon mpg, Map<String, String> tags) {
        List<ManagedPolygon> managedPolygons = new ArrayList<>(mpg.getNumGeometries());
        for (int i = 0; i < mpg.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) mpg.getGeometryN(i);
            ManagedWay way = buildWay(polygon.getExteriorRing(), Collections.emptyMap());
            ManagedRing exteriorRing = new SimpleManagedRing(way);
            List<ManagedRing> interiorRings = new ArrayList<>(polygon.getNumInteriorRing());
            for (int j = 0; j < polygon.getNumInteriorRing(); j++) {
                way = buildWay(polygon.getInteriorRingN(j), null);
                ManagedRing interiorRing = new SimpleManagedRing(way);
                interiorRings.add(interiorRing);
            }
            ManagedPolygon managedPolygon = new ManagedPolygonImpl(layerManager,
                    exteriorRing, interiorRings, null);
            managedPolygons.add(managedPolygon);
        }
        return new ManagedOgcMultiPolygonImpl(managedPolygons, tags);
    }

    public SimpleManagedPolygon buildSimplePolygon(LineString line, Map<String, String> tags) {
        ManagedWay way = buildWay(line, tags);
        return new SimpleManagedPolygon(way, tags);
    }
    
    @Override
    public ManagedWay buildWay(Polygon polygon, Map<String, String> tags) {
        return buildWay(polygon.getExteriorRing(), tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildWay(com.vividsolutions.jts.geom.LineString)
     */
    @Override
    public ManagedWay buildWay(LineString line, Map<String, String> tags) {
        return buildWay(line.getCoordinateSequence(), tags);
    }

    public ManagedWay buildWay(Coordinate start, Coordinate end, Map<String, String> tags) {
        Coordinate[] coordinates = new Coordinate[] {start, end};
        return buildWay(coordinates, tags);
    }

    private ManagedWay buildWay(CoordinateSequence points, Map<String, String> tags) {
        return buildWay(points.toCoordinateArray(), tags);
    }

    private ManagedWay buildWay(Coordinate[] points, Map<String, String> tags) {
        LatLon previousCoor = null;
        List<ManagedNode> managedNodes = new ArrayList<>(points.length);
        List<Node> osmNodes = new ArrayList<>(points.length);
        for (int i = 0; i < points.length; i++) {
            ManagedNode node = buildNode(points[i], Collections.emptyMap(), true);
            // Remove duplicate nodes in ways
            if (!node.getCoor().equals(previousCoor)) {
                managedNodes.add(node);
                osmNodes.add(node.getNode());
            }
            previousCoor = node.getCoor();
        }
        Way way = new Way();
        way.setNodes(osmNodes);
        way.setKeys(tags);
        return new SimpleManagedWay(layerManager, way);
    }

    @Override
    public ManagedWay buildWay(Coordinate[] coordinates, int from, int to,
            Map<String, String> tags) {
        if (to > coordinates.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return buildWay(Arrays.copyOfRange(coordinates, from, to + 1), tags);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildNode(com.vividsolutions.jts.geom.Coordinate, boolean)
     */
    @Override
    public ManagedNode buildNode(Coordinate coordinate, Map<String, String> tags, boolean merge) {
        LatLon latLon = new LatLon(coordinate.y, coordinate.x);
        return nodeSet.add(latLon, tags, merge);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.PrimitiveBuilder#buildNode(com.vividsolutions.jts.geom.Point, boolean)
     */
    @Override
    public ManagedNode buildNode(Point point, Map<String, String> tags, boolean merge) {
        if (point == null)
            return null;
        return buildNode(point.getCoordinate(), tags, merge);
    }

    public ManagedRelation buildRelation(List<ManagedRelationMember> members,
            HashMap<String, String> tags) {
        return new ManagedRelationImpl(layerManager, members, tags);
    }
}
