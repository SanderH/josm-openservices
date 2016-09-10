package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Iterator;

public interface ManagedRing extends ManagedPrimitive {
    public boolean isClockWise();
    public int getNodesCount();
    
    /**
     * Iterator over all the nodes in this ring. Except for the last one, because it is
     * the same as the first node
     * @return
     */
    public Iterator<ManagedNode> getNodeIterator();
}
