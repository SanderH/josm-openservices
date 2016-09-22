package org.openstreetmap.josm.plugins.ods.osm.alignment;

import java.util.List;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedNode;

/**
 * Node distance within.
 * <p>There are several methods to check if two nodes are within a certain distance of each other.
 * Each method has is own tradeoffs with respect to speed and accuracy.</p>
 * 
 * <p>The comparison can be based on EastWest or LatLon coordinates.<br/>
 * The coordinates can be compared using a square/rectangle or a circle/ellipse around a coordinate<br/>
 * When using LatLon coordinates, the X offset in degrees can be the same as the Y offset, or the X offset
 * could be calculated from the latitude, thus making sure that the offset in meters is
 * approximately the same in both directions<br/>
 * When recalculating the X-offset, the offset could be calculated once with a reasonable
 * latitude for all nodes or, for more accuracy, it could be calculated for each individual node.</p>
 *  
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface NodeDWithin {
    /**
     * Check if node2 is within the tolerated distance around node1.
     * @param node1
     * @param node2
     * @return
     */
    public boolean check(Node node1, Node node2);
    
    /**
     * Check if the distance between node n and the line between node1 and node2 is within
     *  the tolerated distance.
     *  
     * @param node1
     * @param node2
     * @return
     */
    public boolean check(Node n, Node node1, Node node2);

    public boolean check(ManagedNode node, ManagedNode node2);

    public boolean check(ManagedNode n, ManagedNode node1, ManagedNode node2);
    
    public BBox getBBox(OsmPrimitive osm);

    public BBox getBBox(Node node1, Node node2);

    public List<Node> nearByNodes(Node node1, Node node2);

    public List<Node> nearByNodes(WaySegment waySegment);
}
