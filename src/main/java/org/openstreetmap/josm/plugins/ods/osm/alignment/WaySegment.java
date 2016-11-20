package org.openstreetmap.josm.plugins.ods.osm.alignment;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

public class WaySegment {
    private final Way way;
    private final int index;

    public WaySegment(Way way, int index) {
        super();
        this.way = way;
        this.index = index;
    }

    public Node getNode1() {
        return way.getNode(index);
    }

    public Node getNode2() {
        return way.getNode(index + 1);
    }

    public boolean isFirst() {
        return index == 0;
    }
    
    public boolean isPartOfWay(Way w) {
        if (! (getNode1().getReferrers().contains(w) &&
                getNode2().getReferrers().contains(w))) {
            return false;
        }
        return w.getNeighbours(getNode1()).contains(getNode2());
    }

}
