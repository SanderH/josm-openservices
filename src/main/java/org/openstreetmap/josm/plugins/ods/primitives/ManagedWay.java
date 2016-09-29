package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.List;

import org.openstreetmap.josm.data.osm.Node;

public interface ManagedWay extends ManagedPrimitive {
    public Node getNode(int index);
    public int getNodesCount();
//    public void setNodes(List<ManagedNode> nodes);
    public List<Node> getNodes();
    public boolean isClosed();
}
