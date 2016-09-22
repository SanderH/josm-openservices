package org.openstreetmap.josm.plugins.ods.osm.alignment;

import java.util.Collections;

import org.openstreetmap.josm.actions.MergeNodesAction;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;

@Deprecated
public class NodeMerger {
    private OsmDataLayer dataLayer;
    
    public NodeMerger(OsmDataLayer dataLayer) {
        super();
        this.dataLayer = dataLayer;
    }

    public Command getMergeCommand(Node first, Node second, TargetLocation target) {
        assert first.getUniqueId() != second.getUniqueId();
        assert first.getDataSet() == second.getDataSet();
        Node targetPosition = getTargetLocation(first, second, target);
        Node targetNode;
        Node nodeToReplace;
        if (first.getUniqueId() > second.getUniqueId()) {
            targetNode = first;
            nodeToReplace = second;
        }
        else {
            targetNode = second;
            nodeToReplace = first;
        }
        Command cmd = MergeNodesAction.mergeNodes(dataLayer,
                Collections.singleton(nodeToReplace), targetNode, targetPosition);
        return cmd;
    }
    
    private static Node getTargetLocation(Node first, Node second, TargetLocation target) {
        switch (target) {
        case FIRST:
            return first;
        case SECOND:
            return second;
        case AUTO:
            if (first.getReferrers().size() == second.getReferrers().size()) {
                return getCenter(first, second);
            }
            else if (first.getReferrers().size() > second.getReferrers().size()) {
                return first;
            }
            return second;
        case MIDDLE:
        default:
            return getCenter(first,  second);
        }
    }
    
    private static Node getCenter(Node node1, Node node2) {
        LatLon targetLatLon = node1.getCoor().getCenter(node2.getCoor()).getRoundedToOsmPrecision();
        return new Node(targetLatLon);
    }

    /**
     * Target location of the resulting node after merging
     * @author Gertjan Idema <mail@gertjanidema.nl>
     *
     */
    public enum TargetLocation {
        FIRST, // Take the location of the first node as the target location
        SECOND, // Take the location of the second node
        MIDDLE, // Set the target location to the middle of the two nodes
        AUTO  // Determine the target location by analyzing the geometries
    }
}
