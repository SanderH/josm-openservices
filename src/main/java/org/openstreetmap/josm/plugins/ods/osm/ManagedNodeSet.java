package org.openstreetmap.josm.plugins.ods.osm;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.NodeData;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedNode;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedNodeImpl;

/**
 * Collection of Managed nodes. Newly added nodes will be merged with existing ones if
 * the coordinates are the same after they have been rounded to OSM precision..
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class ManagedNodeSet {
    private Map<LatLon, ManagedNode> nodes = new HashMap<>();
    
    public ManagedNode add(LatLon latLon, Map<String, String> tags, boolean merge) {
        LatLon ll = latLon.getRoundedToOsmPrecision();
        ManagedNode node = (merge ? nodes.get(ll) : null);
        if (node == null) {
            NodeData nodeData = new NodeData();
            nodeData.setCoor(ll);
            if (tags != null) {
                nodeData.setKeys(tags);
            }
            node = new ManagedNodeImpl(nodeData);
            nodes.put(ll, node);
        }
        else {
            node.putAll(tags);
        }
        return node;
    }
}
