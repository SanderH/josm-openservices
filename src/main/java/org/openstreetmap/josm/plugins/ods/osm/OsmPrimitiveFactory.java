package org.openstreetmap.josm.plugins.ods.osm;

import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedNode;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRelation;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedWay;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Build OsmPrimitives from a JTS geometry and a set of tags and add them to a josm dataset.
 * The JTS geometries must be in the josm crs (epsg:4326)
 * The methods take care of removal of duplicate nodes in ways, and merging of nodes that refer to the same point.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface OsmPrimitiveFactory {
    public LayerManager getLayerManager();

    public ManagedPrimitive<?> create(Geometry geometry, Map<String, String> tags);

    public ManagedPrimitive<?> create(Polygon polygon, Map<String, String> tags);

    public ManagedPrimitive<?> build(MultiPolygon mpg, Map<String, String> tags);

    public ManagedNode build(Point point, Map<String, String> tags);

    public ManagedPrimitive<?> build(LineString ls, Map<String, String> tags);

    public ManagedPrimitive<?> build(MultiLineString mls, Map<String, String> tags);

    /**
     * Create a josm Object from a MultiPolygon object The resulting Object depends
     * on whether the input Multipolygon consists of multiple polygons. If so, the result will be a
     * Relation of type Multipolyon. Otherwise the single polygon will be built.
     */
    public ManagedPrimitive<?> buildArea(MultiPolygon mpg, Map<String, String> tags);

    /**
     * Create a josm Object from a Polygon object The resulting Object depends
     * on whether the input polygon has inner rings. If so, the result will be a
     * Relation of type Multipolyon. Otherwise the result will be a Way
     */
    public ManagedPrimitive<?> buildArea(Polygon polygon, Map<String, String> tags);

    /**
     * Create a josm MultiPolygon relation from a Polygon object.
     * 
     * @param polygon
     * @return the relation
     */
    public ManagedRelation buildMultiPolygon(Polygon polygon, Map<String, String> tags);

    /**
     * Create a josm MultiPolygon relation from a MultiPolygon object.
     * 
     * @param mpg
     * @return the relation
     */
    public ManagedRelation buildMultiPolygon(MultiPolygon mpg, Map<String, String> tags);

    /**
     * Create a josm Way from the exterior ring of a Polygon object
     * 
     * @param polygon
     * @return the way
     */
    public ManagedWay buildWay(Polygon polygon, Map<String, String> tags);

    /**
     * Create a josm way from a LineString object
     * 
     * @param line
     * @return
     */
    public ManagedWay buildWay(LineString line, Map<String, String> tags);

    /**
     * Create a josm way from 2 Coordinates
     * 
     * @param line
     * @return
     */
//    public ManagedWay buildWay(Coordinate start, Coordinate end, Map<String, String> tags);

    /**
     * Create a josm Node from a Coordinate object. Optionally merge with
     * existing nodes.
     * 
     * @param coordinate
     * @param merge
     *            if true, merge this node with an existing node
     * @return the node
     */
    public ManagedPrimitive<Node> buildNode(Coordinate coordinate, Map<String, String> tags, boolean merge);

    /**
     * Create a josm Node from a Point object. Optionally merge with existing
     * nodes.
     * 
     * @param point
     * @param merge
     * @return
     */
    public ManagedPrimitive<Node> buildNode(Point point, Map<String, String> tags, boolean merge);

    public ManagedWay buildWay(Coordinate[] coordinates, int i, int j, Map<String, String> tags);
}