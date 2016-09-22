package org.openstreetmap.josm.plugins.ods.osm.alignment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.command.ChangeCommand;
import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Merges a collection of nodes into one node.
 *
 * The "surviving" node will be the one with the lowest positive id. (I.e. it
 * was uploaded to the server and is the oldest one.)
 *
 * However we use the location of the node that was selected *last*. The
 * "surviving" node will be moved to that location if it is different from the
 * last selected node.
 */
public class MergeNodesCommand extends SequenceCommand {

    /**
     * Constructs a new {@code MergeNodesCommand}.
     * 
     * @param targetPosition
     * @param targetNode
     * @param set
     */
    public MergeNodesCommand(Set<Node> nodes, Node targetNode,
            LatLon targetLocation) {
        super("Merge nodes", createCommands(nodes, targetNode, targetLocation));
    }

    private static List<Command> createCommands(Set<Node> nodes,
            Node targetNode, LatLon targetLocation) {
        // the nodes we will have to delete
        //
        Collection<Node> nodesToDelete = new HashSet<>(nodes);
        nodesToDelete.remove(targetNode);

        // fix the ways referring to at least one of the merged nodes
        //
        List<Command> wayFixCommands = fixParentWays(nodesToDelete, targetNode);
        if (wayFixCommands == null) {
            return Collections.emptyList();
        }
        List<Command> cmds = new LinkedList<>(wayFixCommands);

        // build the commands
        //
        if (!targetNode.getCoor().equals(targetLocation)) {
            Node newTargetNode = new Node(targetNode);
            newTargetNode.setCoor(targetLocation);
            cmds.add(new ChangeCommand(targetNode, newTargetNode));
        }
        if (!nodesToDelete.isEmpty()) {
            cmds.add(new DeleteCommand(nodesToDelete));
        }
        return cmds;
    }

    /**
     * Fixes the parent ways referring to one of the nodes.
     *
     * Replies null, if the ways could not be fixed, i.e. because a way would
     * have to be deleted which is referred to by a relation.
     *
     * @param nodesToDelete
     *            the collection of nodes to be deleted
     * @param targetNode
     *            the target node the other nodes are merged to
     * @return a list of commands; null, if the ways could not be fixed
     */
    protected static List<Command> fixParentWays(Collection<Node> nodesToDelete,
            Node targetNode) {
        List<Command> cmds = new ArrayList<>();
        Set<Way> waysToDelete = new HashSet<>();

        for (Way w : OsmPrimitive.getFilteredList(
                OsmPrimitive.getReferrer(nodesToDelete), Way.class)) {
            List<Node> newNodes = new ArrayList<>(w.getNodesCount());
            for (Node n : w.getNodes()) {
                if (!nodesToDelete.contains(n) && !n.equals(targetNode)) {
                    newNodes.add(n);
                } else if (newNodes.isEmpty()) {
                    newNodes.add(targetNode);
                } else
                    if (!newNodes.get(newNodes.size() - 1).equals(targetNode)) {
                    // make sure we collapse a sequence of deleted nodes
                    // to exactly one occurrence of the merged target node
                    newNodes.add(targetNode);
                }
                // else: drop the node
            }
            if (newNodes.size() < 2) {
                if (w.getReferrers().isEmpty()) {
                    waysToDelete.add(w);
                } else {
                    return null;
                }
            } else {
                cmds.add(new ChangeNodesCommand(w, newNodes));
            }
        }
        if (!waysToDelete.isEmpty()) {
            cmds.add(new DeleteCommand(waysToDelete));
        }
        return cmds;
    }

    /**
     * Merges the nodes in {@code nodes} at the specified node's location. Uses
     * the dataset managed by {@code layer} as reference.
     * 
     * @param layer
     *            layer the reference data layer. Must not be null
     * @param nodes
     *            the collection of nodes. Ignored if null
     * @param targetLocationNode
     *            this node's location will be used for the target node
     * @throws IllegalArgumentException
     *             if {@code layer} is null
     */
    // @Deprecated
    // public static void doMergeNodes(OsmDataLayer layer, Collection<Node>
    // nodes, Node targetLocationNode) {
    // if (nodes == null) {
    // return;
    // }
    // Set<Node> allNodes = new HashSet<>(nodes);
    // allNodes.add(targetLocationNode);
    // Node target;
    // if (nodes.contains(targetLocationNode) && !targetLocationNode.isNew()) {
    // target = targetLocationNode; // keep existing targetLocationNode as
    // target to avoid unnecessary changes (see #2447)
    // } else {
    // target = selectTargetNode(allNodes);
    // }
    //
    // Command cmd = mergeNodes(layer, nodes, target, targetLocationNode);
    // if (cmd != null) {
    // Main.main.undoRedo.add(cmd);
    // layer.data.setSelected(target);
    // }
    // }

}
