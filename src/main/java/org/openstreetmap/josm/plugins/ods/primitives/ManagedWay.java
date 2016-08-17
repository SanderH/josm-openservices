package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.List;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.Way;

public interface ManagedWay extends ManagedPrimitive<Way> {
    public ManagedNode getNode(int index);
    public int getNodesCount();
    public void setNodes(List<ManagedNode> nodes);
    public List<ManagedNode> getNodes();
    public boolean isClosed();
    public BBox getBBox();
}
