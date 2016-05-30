package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;

import com.vividsolutions.jts.index.quadtree.Quadtree;

public class ManagedDataSet {
    public Quadtree nodeIndex = new Quadtree();
    public Quadtree wayIndex = new Quadtree();
    public Quadtree areaIndex = new Quadtree();
    public Map<Node, ManagedNode> nodeMap = new HashMap<>();
    
    public void addNode(ManagedNode managedNode) {
        ManagedNode existingNode = (ManagedNode) nodeIndex.query(managedNode.getEnvelope());
        if (existingNode == null) {
            nodeIndex.insert(managedNode.getEnvelope(), managedNode);
        }
    }
    
    public void addNode(Node node, ManagedNode managedNode) {
        nodeMap.put(node, managedNode);
        nodeIndex.insert(managedNode.getEnvelope(), managedNode);
    }
}
