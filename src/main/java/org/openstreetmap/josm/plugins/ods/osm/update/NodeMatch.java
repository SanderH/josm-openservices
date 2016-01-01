package org.openstreetmap.josm.plugins.ods.osm.update;

import org.openstreetmap.josm.plugins.ods.primitives.OdsNode;

/**
 * Keep track of a match between a Node in the Open data layer and an other node in the Osm Layer.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class NodeMatch {
    private final OdsNode odNode;
    private final OdsNode osmNode;
//    private final Envelope envelope;
    private final boolean hasTags;
    
    public NodeMatch(OdsNode odNode, OdsNode osmNode) {
        super();
        this.odNode = odNode;
        this.osmNode = osmNode;
//        this.envelope = envelope;
        this.hasTags = osmNode.getPrimitive().getInterestingTags().size() > 0;
    }

    public OdsNode getOdNode() {
        return odNode;
    }

    public OdsNode getOsmNode() {
        return osmNode;
    }
    
//    public Envelope getEnvelope() {
//        return envelope;
//    }

    public boolean hasTags() {
        return hasTags;
    }
    
    @Override
    public int hashCode() {
        return osmNode.hashCode();
    }

    public boolean equals(NodeMatch other) {
        if (other == this) return true;
        return other.getOsmNode().equals(getOsmNode());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof NodeMatch)) return false;
        return equals((NodeMatch)obj);
    }
    
    public static enum SpecialReferrers {
        NONE,
        BUILDING,
        OTHER,
        BOTH
    }
}
