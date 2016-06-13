package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.NodeData;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.osm.update.NodeMatch;

import com.vividsolutions.jts.geom.Envelope;

public class ManagedNodeImpl extends AbstractManagedPrimitive<Node> implements ManagedNode {
    private NodeData nodeData;
    private NodeMatch nodeMatch;
    private Envelope envelope;
    private List<NodeReferrer> referrers = new LinkedList<>();
    
    public ManagedNodeImpl(Node primitive) {
        super(primitive);
    }

    public ManagedNodeImpl(NodeData nodeData) {
        super(nodeData.getKeys());
        this.nodeData = nodeData;
    }

    @Override
    public void putAll(Map<String, String> tags) {
        if (tags != null) {
            for (Entry<String, String> entry : tags.entrySet()) {
                nodeData.put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    @Override
    public void addReferrer(NodeReferrer referrer) {
        referrers.add(referrer);
    }

    @Override
    public List<NodeReferrer> getReferrers() {
        return referrers;
    }

    @Override
    public Map<String, String> getKeys() {
        OsmPrimitive primitive = this.getPrimitive();
        if (primitive != null) {
            return primitive.getKeys();
        }
        return nodeData.getKeys();
    }

    @Override
    public Envelope getEnvelope() {
        if (envelope == null) {
            envelope = GeoUtil.toEnvelope(getCoor());
        }
        return envelope;
    }

    @Override
    public BBox getBBox() {
        return new BBox(getCoor().getX(), getCoor().getY());
    }

    @Override
    public LatLon getCoor() {
        if (getPrimitive() != null) {
            return getPrimitive().getCoor();
        }
        return nodeData.getCoor();
    }

    @Override
    public Integer getIndex(ManagedWay referringWay) {
        for (NodeReferrer referrer : getReferrers()) {
            if (referrer.getWay().equals(referringWay)) {
                return referrer.getIndex();
            }
        }
        return null;
    }
    
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
        Node node = getPrimitive();
        if (node == null) {
            node = new Node();
            node.load(nodeData);
            dataSet.addPrimitive(node);
            setPrimitive(node);
        }
        return node;
    }
}
