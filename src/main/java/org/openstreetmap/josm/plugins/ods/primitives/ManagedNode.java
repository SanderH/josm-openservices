package org.openstreetmap.josm.plugins.ods.primitives;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.plugins.ods.osm.update.NodeMatch;

public interface ManagedNode extends ManagedPrimitive<Node> {
    public NodeMatch getMatch();

    public void setMatch(NodeMatch nodeMatch);
    
//    public void addReferrer(ManagedPrimitive<?> referrer);
//    public Integer getIndex(ManagedWay referringWay);
    public LatLon getCoor();
    
    public static class NodeReferrer {
        private ManagedWay way;
        private int index;

        public NodeReferrer(ManagedWay way, int index) {
            super();
            this.way = way;
            this.index = index;
        }

        public ManagedWay getWay() {
            return way;
        }

        public int getIndex() {
            return index;
        }
    }
}
