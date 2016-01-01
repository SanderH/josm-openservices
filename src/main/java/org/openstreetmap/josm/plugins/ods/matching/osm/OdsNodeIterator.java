package org.openstreetmap.josm.plugins.ods.matching.osm;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.osm.NodeDWithin;
import org.openstreetmap.josm.plugins.ods.primitives.OdsNode;
import org.openstreetmap.josm.plugins.ods.primitives.OdsWay;

public class OdsNodeIterator {
    private OdsWay way;
    private List<OdsNode> nodes;
    private int index;
//    private boolean closed;
    private boolean reversed;
//    private boolean modified = false;
//    private List<Command> movedNodes = new LinkedList<>();
    
    public OdsNodeIterator(OdsWay way, int startIndex) {
//        this.way = way;
        this.index = startIndex;
        this.reversed = !way.isClockWise();
        this.nodes = new ArrayList<OdsNode>(way.getNodesCount());
        this.nodes.addAll(way.getNodes());
//        this.closed = way.isClosed();
    }

    public void reset() {
        index = (reversed ? nodes.size() - 1 : 0);
    }
    
    /**
     * @return true if the iterator runs in clockwise direction
     */
    public boolean isClockWise() throws UnsupportedOperationException {
        return (way.isClockWise() != reversed);
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
        if (reversed) {
            return index > n - 1;
        }
        return index + n < nodes.size();
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
        if (reversed) {
            return index + n < nodes.size();
        }
        return index > n - 1;
    }
    
    public OdsNode next() {
        if(hasNextNode()) {
            index = (reversed ? index - 1 : index + 1);
            return nodes.get(index);
        }
        return null;
    }
    
    public OdsNode previous() {
        if(hasPreviousNode()) {
            index = (reversed ? index + 1 : index - 1);
            return nodes.get(index);
        }
        return null;
    }
    
    public OdsNode peek() {
        return nodes.get(index);
    }
    
    public OdsNode peekNext() {
        if (hasNextNode()) {
            return (reversed ? nodes.get(index - 1) : nodes.get(index + 1));
        }
        return null;
    }
    
    public OdsNode peekPrevious() {
        if (hasPreviousNode()) {
            return (reversed ? nodes.get(index + 1) : nodes.get(index - 1));
        }
        return null;
    }
    
    
//    /**
//     * Insert the given node to list of nodes after the current index;
//     * 
//     * @param node The node to insert
//     * After the node has been inserted, the index points to the new inserted node.
//     * @return True on success; False when trying to add a node to the end of a closed line;
//     */
//    public boolean insertNodeAfter(OdsNode node) {
//        if (!hasNextNode() && closed) return false;
//        int pos = (reversed ? index : index+1);
//        nodes.add(pos, node);
//        modified = true;
//        if (!reversed) {
//            next();
//        }
//        return true;
//    }
//    
//    /*
//     * Replace the node at index with the provided node.
//     * If the way is closed and the index is at the first or last node
//     * then replace the node at the other end as well
//     */
//    public boolean updateNode(int index, Node node) {
//        if (index < 0 || index >= nodes.size()) return false;
//        nodes.set(index, node);
//        modified = true;
//        if (closed) {
//            if (index == 0) {
//                nodes.set(nodes.size() - 1, node);
//            }
//            else if (index == nodes.size() - 1) {
//                nodes.set(0,  node);
//            }
//        }
//        return true;
//    }
//    
//    public boolean isModified() {
//        return modified;
//    }

    protected void setReversed(boolean reversed) {
        this.reversed = reversed;
        if (index == 0) {
            index = nodes.size() - 1;
        }
    }

    protected int getIndex() {
         return index;
    }

    public Integer nextIndex() {
        if (hasNextNode()) {
            return (reversed ? index - 1 : index + 1);
        }
        return null;
    }

    public Integer previousIndex() {
        if (hasPreviousNode()) {
            return (reversed ? index - 1 : index + 1);
        }
        return null;
    }

    protected OdsNode getNode(int index) {
        return nodes.get(index);
    }

    public boolean dWithin(NodeDWithin dWithin, OdsNode n) {
        return dWithin.check(peek(), n);
    }

    public boolean dSegmentWithin(NodeDWithin dWithin, OdsNode n) {
        return dWithin.check(n, peek(), peekNext());
    }

//    /**
//     * Collapse the segment at the current index.
//     * The start and end nodes of the segments will be replaced with
//     * 1 node in the middle of the segment.
//     * TODO handle tags
//     * 
//     */
//    public void collapseSegment() {
//        if (hasNextNode()) {
//            if (peek().getReferrers().size() > peekNext().getReferrers().size()) {
//                mergeAdjacentNodes(nextIndex(), getIndex(), false);
//            }
//            else if (peekNext().getReferrers().size() > peek().getReferrers().size()) {
//                mergeAdjacentNodes(getIndex(), nextIndex(), false);
//            }
//            else {
//                mergeAdjacentNodes(nextIndex(), getIndex(), true);
//            }
//            modified = true;
//        }
//        // TODO implement else
//    }
    
//    private static EastNorth middle(EastNorth en1, EastNorth en2) {
//        double east = (en1.east() + en2.east())/2;
//        double north = (en1.north() + en2.north())/2;
//        return new EastNorth(east, north);
//    }
//    
//    /*
//     * Close the iterator and perform the necessary updates.
//     */
//    public void close(boolean undoable) {
//        if (!modified) return;
//        List<Command> commands = new LinkedList<>(); 
//        List<Node> oldNodes = way.getNodes();
//        Command command = new ChangeNodesCommand(way, nodes);
//        command.executeCommand();
//        commands.add(command);
//        if (!movedNodes.isEmpty()) {
//            command = new SequenceCommand("Move nodes", movedNodes);
//            command.executeCommand();
//            commands.add(command);
//        }
//        List<Node> orphanNodes = new LinkedList<>();
//        // Check for nodes that are not relevant anymore
//        for (Node node : oldNodes) {
//            if (node.getReferrers().isEmpty() && !node.hasKeys()) {
//                orphanNodes.add(node);
//            }
//        }
//        if (!orphanNodes.isEmpty()) {
//            command = new DeleteCommand(orphanNodes);
//            command.executeCommand();
//            commands.add(command);
//        }
//        // If undoable, undo the commands in reverse order and the execute them as 1 SequenceCommand.
//        if (undoable) {
//            if (undoable && commands != null) {
//                for (int i = commands.size() -1; i>=0; i--) {
//                    commands.get(i).undoCommand();
//                }
//                Main.main.undoRedo.add(new SequenceCommand("Align buildings", commands));
//                
//            }
//        }
//        if (commands != null && Main.map != null) {
//            Main.map.mapView.repaint();
//        }
//    }

//    /*
//     * Move the node at index to the given coordinates.
//     */
//    public void moveNode(int index, LatLon coor) {
//        Node node = nodes.get(index);
//        moveNode(node, coor);
//    }
//    
//    public void moveNode(Node node, LatLon coor) {
//        movedNodes.add(new MoveCommand(node, coor));
//    }
//    
//    public void moveNode(int index, EastNorth en) {
//        Node node = nodes.get(index);
//        moveNode(node, en);
//    }
//    
//    public void moveNode(Node node, EastNorth en) {
//        movedNodes.add(new MoveCommand(node, node.getEastNorth(), en));
//    }
//    
//    private void mergeAdjacentNodes(int index1, int index2, boolean toMiddle) {
//        EastNorth middle = middle(nodes.get(index1).getEastNorth(), nodes.get(index2).getEastNorth());
//        nodes.remove(index1);
//        int index = (index2 > index1 ? index2 - 1 : index2);
//        if (closed && index1 == 0) {
//            nodes.set(nodes.size() -1, nodes.get(0));
//        }
//        else if (closed && index1 == nodes.size()) {
//            nodes.set(0, nodes.get(nodes.size() -1));
//        }
//        if (toMiddle) {
//            moveNode(nodes.get(index), middle);
//        }
//    }

    /**
     * Calculate the angle between the current segment and the current segment
     * of the provide NodeIterator
     *  
     * @param it
     * @return
     */
    public Double angle(OdsNodeIterator it) {
        return angle() - it.angle();
    }
    /**
     * Calculate the angle of the current segment to the x-axis
     * 
     * @return
     */
    public Double angle() {
        Double x1 = this.peek().getPrimitive().getEastNorth().east();
        Double y1 = this.peek().getPrimitive().getEastNorth().north();
        Double x2 = this.peekNext().getPrimitive().getEastNorth().east();
        Double y2 = this.peekNext().getPrimitive().getEastNorth().north();
        return Math.atan2(y1 - y2, x1 - x2);
    }    
}
