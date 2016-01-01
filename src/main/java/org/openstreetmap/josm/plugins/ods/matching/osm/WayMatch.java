package org.openstreetmap.josm.plugins.ods.matching.osm;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.osm.update.NodeMatch;
import org.openstreetmap.josm.plugins.ods.primitives.OdsWay;

public class WayMatch {
    public final OdsWay odWay;
    public final OdsWay osmWay;
    public final List<NodeMatch> matchedNodes;
    
    public WayMatch(OdsWay odWay, OdsWay osmWay) {
        super();
        this.osmWay = osmWay;
        this.odWay = odWay;
        this.matchedNodes = new ArrayList<>(odWay.getNodesCount());
    }
    
    public void setNodeMatch(int index, NodeMatch match) {
        matchedNodes.set(index, match);
    }
}
