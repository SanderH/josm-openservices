package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.List;

import org.openstreetmap.josm.data.osm.Way;

public interface OdsWay extends OdsPrimitive<Way> {
    public OdsNode getNode(int index);
    public int getNodesCount();
    public List<OdsNode> getNodes();
    public boolean isClosed();
    public boolean isClockWise();
}
