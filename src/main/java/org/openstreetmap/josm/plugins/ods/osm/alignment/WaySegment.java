package org.openstreetmap.josm.plugins.ods.osm.alignment;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

public class WaySegment {
    private static Way way;
    private static int index;

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
    
    public boolean isPartOfWay(Way way) {
        if (! (getNode1().getReferrers().contains(way) &&
                getNode2().getReferrers().contains(way))) {
            return false;
        }
        return way.getNeighbours(getNode1()).contains(getNode2());
    }

}
