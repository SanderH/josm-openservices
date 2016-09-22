package org.openstreetmap.josm.plugins.ods.osm.alignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.osm.alignment.NodeMerger.TargetLocation;

public class ModifiableWay {
    private Way way;
    private int index;
    private boolean closed;
    private List<Command> commands = new LinkedList<>();
    /*
     *  Keep a detailed list of undo commands so we can step back in
     *  small steps during testing.
     */
    private final UndoMode undoMode;
    
    public ModifiableWay(Way way, UndoMode undoMode) {
        this(way, 0, undoMode);
    }

    public ModifiableWay(Way way, int startIndex, UndoMode undoMode) {
        this.way = way;
        this.index = startIndex;
        this.undoMode = undoMode;
        this.closed = way.isClosed();
    }
    
    @Deprecated
    public void reset() {
        index = 0;
    }
    
    
    /**
     * Check if there is at least 1 node after the current node.
     * 
     * @return true if there is at least one next node. false otherwise
     */
    public boolean hasNextNode() {
        return hasNextNodes(1);
    }
    
    /**
     * Check if there are at least n nodes after the current node.
     * 
     * @return true if there are at least n next nodes. false otherwise
     */
    public boolean hasNextNodes(int n) {
        return index + n < way.getNodesCount();
    }
    
    /**
     * Check if there is at least 1 node before the current node.
     * 
     * @return true if there is at least one previous node. false otherwise
     */
    public boolean hasPreviousNode() {
        return hasPreviousNodes(1);
    }
    
    /**
     * Check if there are at least n nodes before the current node.
     * 
     * @return true if there are at least n previous nodes. false otherwise
     */
    public boolean hasPreviousNodes(int n) {
        return index > n - 1;
    }
    
    public Node next() {
        if(hasNextNode()) {
            index++;
            return way.getNode(index);
        }
        return null;
    }
    
    public Node previous() {
        if(hasPreviousNode()) {
            index--;
            return way.getNode(index);
        }
        return null;
    }
    
    /**
     * Get the node at the current index
     *
     * @return
     */
    public Node getCurrentNode() {
        return way.getNode(index);
    }
    
    /**
     * Get the node after the current index
     * 
     * @return The node after the current index or null if
     *     the index is at the end of the way.
     */
    public Node getNextNode() {
        if (hasNextNode()) {
            return way.getNode(index + 1);
        }
        return null;
    }
    
    public Node peekPrevious() {
        if (hasPreviousNode()) {
            return way.getNode(index - 1);
        }
        return null;
    }
    
    /**
     * Get the Way segment at the current index.
     * 
     * @return The current segment or null if the index is at the
     *     end of the way.
     */
    public WaySegment getCurrentSegment() {
        if (hasNextNode()) {
            return new WaySegment(way, index);
        }
        return null;
    }
    
    /**
     * Insert the given node after the current index;
     * 
     * @param node The node to insert
     * After the node has been inserted, the index points to the new inserted node.
     * @return True on success; False when trying to add a node to the end of a closed line;
     */
    public boolean insertNodeAfterCurrent(Node node) {
        if (!hasNextNode() && closed) return false;
        List<Node> newNodes = new ArrayList<>(way.getNodesCount() + 1);
        for (int i = 0; i <= index; i++) {
            newNodes.add(way.getNode(i));
        }
        newNodes.add(node);
        for (int i = index + 1; i < way.getNodesCount(); i++) {
            newNodes.add(way.getNode(i));
        }
        Command cmd = new ChangeNodesCommand(way, newNodes);
        runCommand(cmd);
        index++;
        return true;
    }

    /**
     * Align a node to the current segment.
     * If the node is near to the start or end node of the segment,
     * align the nodes. Otherwise align the node with the actual segment.
     * @param node
     * @param segment
     */
    public void align(Node node, NodeDWithin dWithin) {
        if (dWithin.check(node, getCurrentNode())) {
            /*
             * If the nearby node is near to the first node of the segment
             * the alignment has already been done against the last node
             * of the previous segment.
             * Unless this is the first segment
             */
            if (index == 0) {
                align(node, getCurrentNode());
            }
        }
        else if (dWithin.check(node, getNextNode())) {
            align(node, getNextNode());
        }
        else {
            // If the node and the segment share the same way, we should skip the alignment
            WaySegment segment = getCurrentSegment();
            if (onSameWay(node, segment)) {
                return;
            }
            alignMiddle(node);
        }
    }

    /**
     * Align a node to the current segment. We may trust that the node is not
     * near to either of the segment's end points.
     * If alignment can be achieved by removing the node and the node
     * has no tags. Then opt for this solution in favor of adding the node
     * to the segment.
     * 
     * @param node
     * @param segment
     */
    private void alignMiddle(Node node) {
        insertNodeAfterCurrent(node);
    }

    /**
     * Align two nearby nodes, unless the tagging is incompatible. 
     * If either of the nodes is part of a very short segment, removal
     * of the node may be the best solution.
     * If the first node is a new node (id == 0) and the second node is an
     * existing node, then merge the existing node on to the new node,
     * keeping the location of the new node and any tags from the old node. 
     * 
     * @param node
     * @param node1
     */
    private void align(Node node, Node node1) {
        TargetLocation targetLocation = TargetLocation.AUTO;
        if (node1.getId() != 0) {
            targetLocation = TargetLocation.FIRST;
        }
        merge(node, node1, targetLocation);
    }

    private void merge(Node first, Node second, TargetLocation targetLocation) {
        assert first.getUniqueId() != second.getUniqueId();
        assert first.getDataSet() == second.getDataSet();
        LatLon targetCoor = getTargetCoor(first, second, targetLocation);
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
        Command cmd = new MergeNodesCommand(
                Collections.singleton(nodeToReplace), targetNode, targetCoor);
        runCommand(cmd);
    }
    
    /**
     * Check if the node and the segments endpoints are all on one closed way.
     * @param node
     * @param segment
     * @return
     */
    private static boolean onSameWay(Node node, WaySegment segment) {
        for (OsmPrimitive primitive : node.getReferrers()) {
            if (primitive.getType() == OsmPrimitiveType.WAY &&
                segment.isPartOfWay((Way)primitive)) {
                return true;
            }
        }
        return false;
    }

    
    public boolean isModified() {
        return !commands.isEmpty();
    }

    protected int getIndex() {
         return index;
    }

    public Integer nextIndex() {
        if (hasNextNode()) {
            return index + 1;
        }
        return null;
    }

    public Integer previousIndex() {
        if (hasPreviousNode()) {
            return index - 1;
        }
        return null;
    }

    protected Node getNode(int idx) {
        return way.getNode(idx);
    }

    public boolean dWithin(NodeDWithin dWithin, Node n) {
        return dWithin.check(getCurrentNode(), n);
    }

//    public boolean dSegmentWithin(NodeDWithin dWithin, Node n) {
//        return dWithin.check(n, getCurrentNode(), peekNext());
//    }

    /*
     * Close the modifiable way.
     */
    public void close() {
        if (!isModified()) return;
        if (undoMode != UndoMode.NORMAL) return;
        // Undo all commands and redo them as a single sequence command
        for (Command cmd : commands) {
            cmd.undoCommand();
        }
        Command cmd = new SequenceCommand("Align way", commands);
        Main.main.undoRedo.add(cmd);
        if (!commands.isEmpty()) {
            Main.map.mapView.repaint();
        }
    }


    private void runCommand(Command cmd) {
        if (undoMode == UndoMode.DETAILED) {
            Main.main.undoRedo.add(cmd);
        }
        else {
            cmd.executeCommand();
        }
        commands.add(cmd);
    }

    private static LatLon getTargetCoor(Node first, Node second, TargetLocation target) {
        switch (target) {
        case FIRST:
            return first.getCoor();
        case SECOND:
            return second.getCoor();
        case AUTO:
            if (first.getReferrers().size() == second.getReferrers().size()) {
                return getCenter(first, second);
            }
            else if (first.getReferrers().size() > second.getReferrers().size()) {
                return first.getCoor();
            }
            return second.getCoor();
        case MIDDLE:
        default:
            return getCenter(first,  second);
        }
    }
    
    private static LatLon getCenter(Node node1, Node node2) {
        return node1.getCoor().getCenter(node2.getCoor()).getRoundedToOsmPrecision();
    }

    /**
     * Calculate the angle between the current segment and the current segment
     * of the provide NodeIterator
     *  
     * @param it
     * @return
     */
//    public Double angle(ModifiableWay it) {
//        return angle() - it.angle();
//    }
    
    /**
     * Calculate the angle of the current segment to the x-axis
     * 
     * @return
     */
//    public Double angle() {
//        Double x1 = this.getCurrentNode().getEastNorth().east();
//        Double y1 = this.getCurrentNode().getEastNorth().north();
//        Double x2 = this.peekNext().getEastNorth().east();
//        Double y2 = this.peekNext().getEastNorth().north();
//        return Math.atan2(y1 - y2, x1 - x2);
//    }
    
    public static enum UndoMode {
        NONE,
        NORMAL,
        DETAILED
    }
}
