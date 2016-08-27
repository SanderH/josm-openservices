package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.List;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Envelope;

public class ManagedWayImpl extends AbstractManagedPrimitive<Way> implements ManagedWay {
    private List<ManagedNode> nodes;
//    private Set<ManagedWay> adjacentWays = new HashSet<>();
    private BBox bbox;

    public ManagedWayImpl(LayerManager layerManager, Way way) {
        super(layerManager, way);
//        this.nodes = new ArrayList<>(nodes);
//        int last = nodes.size() - 1;
//        if (this.nodes.get(0).getCoor().equals(this.nodes.get(last).getCoor())) {
//            this.nodes.set(0, this.nodes.get(last));
//        }
//        findAdjacentWays();
    }

//    public ManagedWayImpl(LayerManager layerManager, List<ManagedNode> nodes, Map<String, String> tags) {
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

//    public ManagedWayImpl(Way osmWay, LayerManager layerManager) {
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
        if (getPrimitive() != null) {
            return getPrimitive().isClosed();
        }
        ManagedNode startNode = nodes.get(0);
        ManagedNode endNode = nodes.get(nodes.size() - 1);
        return startNode == endNode;
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
    public Way create(DataSet dataSet) {
        Way way = getPrimitive();
        for (Node node : way.getNodes()) {
            if (node.getDataSet() == null) {
                dataSet.addPrimitive(node);
            }
        }
        if (way.getDataSet() == null) {
            dataSet.addPrimitive(way);
        }
        return way;
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
    
    
}
