package org.openstreetmap.josm.plugins.ods.matching.osm;

import org.openstreetmap.josm.plugins.ods.osm.alignment.NodeDWithin;
import org.openstreetmap.josm.plugins.ods.osm.update.NodeMatch;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedNode;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRing;

/**
 * Compare the geometry of 2 OdsWay's
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class RingComparator {
    private ManagedRing odRing;
    private ManagedRing osmRing;
    private NodeDWithin dWithin;
    private boolean matchingNodes = false;
    private Boolean fullMatch = true;
    
    public RingComparator(ManagedRing odRing, ManagedRing osmRing, NodeDWithin dWithin) {
        super();
        this.odRing = odRing;
        this.osmRing = osmRing;
        this.dWithin = dWithin;
    }
    
    /**
     * 
     */
    public void compare() {
        OdsNodeIterator itOd = new OdsNodeIterator(odRing, 0);
        OdsNodeIterator itOsm = new OdsNodeIterator(osmRing, 0);
        // Try to find a matching node between the 2 ways
        NodeMatch nodeMatch = null;
        while (nodeMatch == null && itOd.hasNextNode()) {
            // Start searching at the start of the Osm way
            itOsm.reset();
            while (nodeMatch == null && itOsm.hasNextNode()) {
                ManagedNode odNode = itOd.peek();
                ManagedNode osmNode = itOsm.peek();
                if (dWithin.check(odNode, osmNode)) {
                    nodeMatch = odNode.getMatch();
                    // Make sure we don't create a new nodeMatch if we already have one.
                    if (nodeMatch != null) {
                        if (nodeMatch.getOdNode().equals(osmNode)) {
                            odNode.setMatch(nodeMatch);
                        }
                        else {
                            // TODO Unexpected match.
                            // TODO Not sure in which situation this might happen and how to handle it
                            nodeMatch = null;
                        }
                    }
                    else {
                        nodeMatch = new NodeMatch(odNode, osmNode);
                    }
                }
            }
        }
        if (nodeMatch != null) {
            // We have a starting point for the comparison.
            matchingNodes = true;
            compare(itOd, itOsm, nodeMatch);
        }
    }
        /**
         * Compare the 2 ways using the two iterators and the given nodeMatch
         * @param itOd The iterator for the Open data way.
         * @param itOsm The iterator for the Osm way
         * @param nodeMatch The node match for the starting points of both iterators
         */
        private static void compare(OdsNodeIterator itOd, OdsNodeIterator itOsm,
                NodeMatch nodeMatch) {
            boolean matchingNodes = true; /* True if both ways have the same number of nodes and all 
                node pairs are within the tolerated distance of each other */
            boolean matchingTopology = true; /* True is the edges of both way are within the tolerated
                distance of each other. Both ways may have nodes that have no match in the other way,
                as long as these nodes are close enough the the edge of the other way */ 
            
            assert itOd.isClockWise();
            assert itOsm.isClockWise();
            assert itOd.peek().equals(nodeMatch.getOdNode());
            assert itOsm.peek().equals(nodeMatch.getOsmNode());
        // TODO Auto-generated method stub
        
    }

    /**
     * Compare the ways with know start indexes.
     * 
     * @param wayMatch
     * @param indexOd
     * @param indexOsm
     */
    private void compare(RingMatch wayMatch, int indexOd, int indexOsm) {
        boolean fullMatch = true;
        OdsNodeIterator itOd = new OdsNodeIterator(wayMatch.odRing, indexOd);
        OdsNodeIterator itOsm = new OdsNodeIterator(wayMatch.osmRing, indexOsm);
        if (dWithin.check(itOd.peekNext(), itOsm.peek())) {
            
        }
        
        
    }

}
