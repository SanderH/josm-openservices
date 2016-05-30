package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Iterator;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

public interface ManagedRing<T extends OsmPrimitive> extends ManagedPrimitive<T> {
    public boolean isClockWise();
    public int getNodesCount();
//    public BBox getBBox();
    
    /**
     * Iterator over all the nodes in this ring. Except for the last one, because it is
     * the same as the first node
     * @return
     */
    public Iterator<ManagedNode> getNodeIterator();
}
