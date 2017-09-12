package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.List;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.LayerManager;

public class SimpleManagedWay extends AbstractManagedPrimitive implements ManagedWay {
    //    private Set<ManagedWay> adjacentWays = new HashSet<>();
    //    private BBox bbox;

    public SimpleManagedWay(LayerManager layerManager, Way way) {
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
    //
    //    @Override
    //    public void setNodes(List<ManagedNode> nodes) {
    //        this.nodes = nodes;
    //    }

    public Way getWay() {
        return (Way) getPrimitive();
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
    public List<Node> getNodes() {
        return getWay().getNodes();
    }

    @Override
    public BBox getBBox() {
        return getWay().getBBox();
    }

    @Override
    public boolean isClosed() {
        //        if (getPrimitive() != null) {
        return getWay().isClosed();
        //        }
        //        ManagedNode startNode = nodes.get(0);
        //        ManagedNode endNode = nodes.get(nodes.size() - 1);
        //        return startNode == endNode;
    }

    @Override
    public int getNodesCount() {
        return getWay().getNodesCount();
    }

    @Override
    public boolean contains(ManagedNode mNode) {
        return false;
    }

    @Override
    public Node getNode(int index) {
        return getWay().getNode(index);
    }

    //    private BBox createBBox() {
    //        double minLat = -90;
    //        double maxLat = 90;
    //        double minLon = -180;
    //        double maxLon = 180;
    //
    //        for (ManagedNode node : nodes) {
    //            LatLon latLon = node.getCoor();
    //            minLat = Math.min(minLat, latLon.lat());
    //            minLon = Math.min(minLon, latLon.lon());
    //            maxLat = Math.max(maxLat, latLon.lat());
    //            maxLon = Math.min(maxLon, latLon.lon());
    //        }
    //        return new BBox(minLon, minLat, maxLon, maxLat);
    //    }

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
    public Way create(DataSet dataSet) {
        Way way = getWay();
        if (way.getDataSet() != null) {
            return way;
        }
        for (Node node : way.getNodes()) {
            if (node.getDataSet() == null) {
                dataSet.addPrimitive(node);
            }
        }
        dataSet.addPrimitive(way);
        return way;
    }

    private static Node createNode(DataSet dataSet, Node node) {
        Node n = (Node) dataSet.getPrimitiveById(node);
        if (n != null) {
            return n;
        }

        Node clone = new Node(node);
        dataSet.addPrimitive(clone);
        return clone;
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
}
