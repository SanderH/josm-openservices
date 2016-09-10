package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Envelope;

public class ComplexManagedWay extends AbstractManagedPrimitive implements ManagedWay {
    private List<ManagedNode> nodes;
    private List<DirectedWay> ways = new LinkedList<>();
    private BBox bbox;

    public ComplexManagedWay(LayerManager layerManager, Way way) {
        super(layerManager, way);
//        this.nodes = new ArrayList<>(nodes);
//        int last = nodes.size() - 1;
//        if (this.nodes.get(0).getCoor().equals(this.nodes.get(last).getCoor())) {
//            this.nodes.set(0, this.nodes.get(last));
//        }
//        findAdjacentWays();
    }

//    public SimpleManagedWay(LayerManager layerManager, List<ManagedNode> nodes, Map<String, String> tags) {
//        super(layerManager, tags);
//        this.nodes = new ArrayList<>(nodes);
//        int last = nodes.size() - 1;
//        if (this.nodes.get(0).getCoor().equals(this.nodes.get(last).getCoor())) {
//            this.nodes.set(0, this.nodes.get(last));
//        }
//    }
    
    @Override
    public void setNodes(List<ManagedNode> nodes) {
        this.nodes = nodes;
    }
    
//    private void findAdjacentWays() {
//        for (ManagedNode node : nodes) {
//            for (NodeReferrer referrer : node.getReferrers()) {
//                ManagedWay way = referrer.getWay();
//                if (!way.equals(this)) {
//                    adjacentWays.add(way);
//                }
//            }
//        }
//    }

//    public SimpleManagedWay(Way osmWay, LayerManager layerManager) {
//        super(osmWay);
//        nodes = new ArrayList<>(osmWay.getNodesCount());
//        for (Node node : osmWay.getNodes()) {
//            ManagedNode odsNode = (ManagedNode) layerManager.getManagedPrimitive(node);
//            assert odsNode != null;
//        }
//    }
//
//    @Override
//    public Entity getEntity() {
//        return entity;
//    }

    
    @Override
    public List<ManagedNode> getNodes() {
        return nodes;
    }

    @Override
    public Envelope getEnvelope() {
        return GeoUtil.toEnvelope(getBBox());
    }

    @Override
    public BBox getBBox() {
        if (getPrimitive() != null) {
            return getPrimitive().getBBox();
        }
        if (bbox == null) {
            bbox = createBBox();
        }
        return null;
    }

    @Override
    public boolean isClosed() {
        if (ways.size() == 1) {
            return ways.get(0).getWay().isClosed();
        }
        Iterator<DirectedWay> it = ways.iterator();
        DirectedWay firstWay = it.next();
        DirectedWay currentWay = firstWay;
        DirectedWay nextWay = it.next();
        if (currentWay.getLastNode() != nextWay.getFirstNode()) {
            return false;
        }
        while (it.hasNext()) {
            currentWay = nextWay;
            nextWay = it.next();
            if (currentWay.getLastNode() != nextWay.getFirstNode()) {
                return false;
            }
        }
        return nextWay.getLastNode() == firstWay.getFirstNode();
    }

    @Override
    public int getNodesCount() {
        return nodes.size();
    }

    @Override
    public ManagedNode getNode(int index) {
        return nodes.get(index);
    }
    
    private BBox createBBox() {
        double minLat = -90;
        double maxLat = 90;
        double minLon = -180;
        double maxLon = 180;
        
        for (ManagedNode node : nodes) {
            LatLon latLon = node.getCoor();
            minLat = Math.min(minLat, latLon.lat());
            minLon = Math.min(minLon, latLon.lon());
            maxLat = Math.max(maxLat, latLon.lat());
            maxLon = Math.min(maxLon, latLon.lon());
        }
        return new BBox(minLon, minLat, maxLon, maxLat);
    }

//    @Override
//    public Way create(DataSet dataSet) {
//        Way way = getPrimitive();
//        if (way == null) {
//            List<Node> nodes = new ArrayList<>(getNodes().size());
//            for (ManagedNode mNode : getNodes()) {
//                nodes.add(mNode.create(dataSet));
//            }
//            way = new Way();
//            way.setNodes(nodes);
//            way.setKeys(getKeys());
//            setPrimitive(way);
//            dataSet.addPrimitive(way);
//        }
//        return way;
//    }

    @Override
    public Relation create(DataSet dataSet) {
        // TODO implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return getUniqueId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ManagedWay)) {
            return false;
        }
        return getUniqueId().equals(((ManagedWay)obj).getUniqueId());
    }

    @Override
    public double getArea() {
        // A line has no area. Closed ways should be wrapped in a ManagedRing
        return 0;
    }
    
    private class DirectedWay {
        private final Way way;
        private final Node firstNode;
        private final Node lastNode;
        private final Direction direction;
        
        public DirectedWay(Way way, Direction direction) {
            super();
            this.way = way;
            this.direction = direction;
            this.firstNode = way.getNode(0);
            this.lastNode = way.getNode(way.getNodesCount() - 1);
        }

        public Way getWay() {
            return way;
        }

        public Direction getDirection() {
            return direction;
        }
        
        public Node getFirstNode() {
            switch (direction) {
            case BACKWARD:
                return lastNode;
            case FORWARD:
            case UNDETERMINED:
            default:
                return firstNode;
            }
        }

        public Node getLastNode() {
            switch (direction) {
            case BACKWARD:
                return firstNode;
            case FORWARD:
            case UNDETERMINED:
            default:
                return lastNode;
            }
        }

    }
    
    private enum Direction {
        FORWARD,
        BACKWARD,
        UNDETERMINED
    }
}
