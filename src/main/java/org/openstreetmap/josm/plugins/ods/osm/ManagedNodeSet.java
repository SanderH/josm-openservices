package org.openstreetmap.josm.plugins.ods.osm;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.plugins.ods.LayerManager;
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
    private final LayerManager layerManager;
    private final Map<LatLon, ManagedNode> nodes = new HashMap<>();

    public ManagedNodeSet(LayerManager layerManager) {
        super();
        this.layerManager = layerManager;
    }

    public ManagedNode add(LatLon latLon, Map<String, String> tags, boolean merge) {
        LatLon ll = latLon.getRoundedToOsmPrecision();
        ManagedNode odsNode = (merge ? nodes.get(ll) : null);
        if (odsNode == null) {
            Node osmNode = new Node(ll);
            if (tags != null) {
                osmNode.setKeys(tags);
            }
            odsNode = new ManagedNodeImpl(layerManager, osmNode);
            nodes.put(ll, odsNode);
        }
        else {
            odsNode.putAll(tags);
        }
        return odsNode;
    }

    public void reset() {
        nodes.clear();
    }
}
