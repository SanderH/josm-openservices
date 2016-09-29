package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Map;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.osm.update.NodeMatch;

public class ManagedNodeImpl extends AbstractManagedPrimitive implements ManagedNode {
    private NodeMatch nodeMatch;
    
    public ManagedNodeImpl(LayerManager layerManager, Node primitive) {
        super(layerManager, primitive);
    }

    
//    @Override
//    public void putAll(Map<String, String> tags) {
//        if (tags != null) {
//            for (Entry<String, String> entry : tags.entrySet()) {
//                getPrimitive().put(entry.getKey(), entry.getValue());
//            }
//        }
//    }
//    
//    @Override
//    public void addReferrer(ManagedPrimitive<?> referrer) {
//        referrers.add(referrer);
//    }
//
//    public void removeReferrer
//    @Override
//    public List<NodeReferrer> getReferrers() {
//        return referrers;
//    }

    @Override
    public Node getNode() {
        return (Node) getPrimitive();
    }

    @Override
    public Map<String, String> getKeys() {
        OsmPrimitive primitive = this.getPrimitive();
        return primitive.getKeys();
    }

    @Override
    public BBox getBBox() {
        if (getPrimitive() == null) {
            return null;
        }
        return getPrimitive().getBBox();
    }

    
    @Override
    public LatLon getCenter() {
        return getCoor();
    }


    @Override
    public LatLon getCoor() {
        return getNode().getCoor();
    }

//    @Override
//    public Integer getIndex(ManagedWay referringWay) {
//        for (OsmPrimitiveNodeReferrer referrer : getReferrers()) {
//            if (referrer.getWay().equals(referringWay)) {
//                return referrer.getIndex();
//            }
//        }
//        return null;
//    }
    
    @Override
    public NodeMatch getMatch() {
        return nodeMatch;
    }

    @Override
    public void setMatch(NodeMatch nodeMatch) {
        this.nodeMatch = nodeMatch;
    }

    @Override
    public Node create(DataSet dataSet) {
        Node node = getNode();
        if (node.getDataSet() == null) {
            dataSet.addPrimitive(node);
        }
        return node;
    }

    @Override
    public double getArea() {
        return 0;
    }
}
