package org.openstreetmap.josm.plugins.ods.matching.osm;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.osm.update.NodeMatch;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRing;

public class RingMatch {
    public final ManagedRing<?> odRing;
    public final ManagedRing<?> osmRing;
    public final List<NodeMatch> matchedNodes;
    
    public RingMatch(ManagedRing<?> odRing, ManagedRing<?> osmRing) {
        super();
        this.osmRing = osmRing;
        this.odRing = odRing;
        this.matchedNodes = new ArrayList<>(odRing.getNodesCount());
    }
    
    public void setNodeMatch(int index, NodeMatch match) {
        matchedNodes.set(index, match);
    }
}
