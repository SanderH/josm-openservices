package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.tools.Geometry;
import org.openstreetmap.josm.tools.I18n;

public class OdsWayImpl extends OdsPrimitiveImpl<Way> implements OdsWay {
    private List<OdsNode> nodes;
    private Boolean isClockWise; // True if the nodes in this way are oriented clockwise.

    public OdsWayImpl(Way osmWay, LayerManager layerManager) {
        this(osmWay, layerManager, null);
    }

    public OdsWayImpl(Way osmWay, LayerManager layerManager, Boolean isCCW) {
        super(osmWay);
        this.isClockWise = isCCW;
        nodes = new ArrayList<>(osmWay.getNodesCount());
        for (Node node : osmWay.getNodes()) {
            OdsNode odsNode = (OdsNode) layerManager.getOdsPrimitive(node);
            assert odsNode != null;
        }
    }
    
    @Override
    public List<OdsNode> getNodes() {
        return nodes;
    }

    @Override
    public boolean isClosed() {
        return getPrimitive().isClosed();
    }

    @Override
    public boolean isClockWise() {
        if (!isClosed()) {
            throw new UnsupportedOperationException(I18n.tr("This operation is only supported for closed ways"));
        }
        if (isClockWise == null) {
            isClockWise = Geometry.isClockwise(getPrimitive());
        }
        return isClockWise;
    }

    @Override
    public int getNodesCount() {
        return nodes.size();
    }

    @Override
    public OdsNode getNode(int index) {
        return nodes.get(index);
    }
}
