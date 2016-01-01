package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.List;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.plugins.ods.osm.update.NodeMatch;

public interface OdsNode extends OdsPrimitive<Node> {
    public NodeMatch getMatch();

    public void setMatch(NodeMatch nodeMatch);
    
    public void addReferrer(NodeReferrer referrer);
    public List<NodeReferrer> getReferrers();
    public Integer getIndex(OdsWay referringWay);

    public static class NodeReferrer {
        private OdsWay way;
        private int index;

        public NodeReferrer(OdsWay way, int index) {
            super();
            this.way = way;
            this.index = index;
        }

        public OdsWay getWay() {
            return way;
        }

        public int getIndex() {
            return index;
        }
    }
}
