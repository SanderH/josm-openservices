package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.List;

public interface ManagedWay extends ManagedPrimitive {
    public ManagedNode getNode(int index);
    public int getNodesCount();
    public void setNodes(List<ManagedNode> nodes);
    public List<ManagedNode> getNodes();
    public boolean isClosed();
}
