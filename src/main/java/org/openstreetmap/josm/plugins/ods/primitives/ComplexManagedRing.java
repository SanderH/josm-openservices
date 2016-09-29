package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.plugins.ods.LayerManager;

/**
 * A Managed ring that is constructed from a collection of ManagedWays.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class ComplexManagedRing extends AbstractManagedPrimitive implements ManagedRing {
    private boolean clockWise;
    final List<RingMember> members;
    private int nodesCount;

    public ComplexManagedRing(LayerManager layerManager, List<RingMember> members) {
        this(layerManager, members, new HashMap<>());
    }
    
    public ComplexManagedRing(LayerManager layerManager, List<RingMember> members, Map<String, String> keys) {
        super(layerManager, keys);
        this.members = members;
        nodesCount = 0;
        for (RingMember member : members) {
            nodesCount += member.getManagedWay().getNodesCount() - 1;
        }
    }

    @Override
    public BBox getBBox() {
        return getPrimitive().getBBox();
    }

    @Override
    public boolean isClockWise() {
        return clockWise;
    }

    @Override
    public int getNodesCount() {
        return nodesCount;
    }

    @Override
    public Iterator<Node> getNodeIterator() {
        return new RingNodeIterator();
    }

    private class RingNodeIterator implements Iterator<Node> {
        private Iterator<RingMember> memberIterator;
        private Iterator<Node> memberNodeIterator;
        
        public RingNodeIterator () {
            memberIterator = members.iterator();
            nextMember();
        }
        
        @Override
        public boolean hasNext() {
            if (memberNodeIterator.hasNext()) {
                return true;
            }
            return memberIterator.hasNext();
        }

        @Override
        public Node next() {
            if (!memberNodeIterator.hasNext()) {
                nextMember();
            }
            return memberNodeIterator.next();
        }
        
        private void nextMember() {
            RingMember member = memberIterator.next();
            memberNodeIterator = member.nodeIterator();
        }
    }
    
    public static class RingMember {
        private ManagedWay managedWay;
        boolean reversed;

        public RingMember(ManagedWay managedWay, boolean reversed) {
            super();
            this.managedWay = managedWay;
            this.reversed = reversed;
        }

        public ManagedWay getManagedWay() {
            return managedWay;
        }

        public boolean isReversed() {
            return reversed;
        }
        
        public Iterator<Node> nodeIterator() {
            return new MemberNodeIterator(this);
        }
    }
    
    /**
     * Iterator over the nodes in a Member way of the ring.
     * If the way is reversed, the nodes are return in opposite order.
     * The last node is ignored, because it's the same as the first node of the next
     * member way
     * 
     * @author Gertjan Idema <mail@gertjanidema.nl>
     *
     */
    private static class MemberNodeIterator implements Iterator<Node> {
        private final boolean reversed;
        private final int nodesCount;
        private final ListIterator<Node> iterator;

        public MemberNodeIterator(RingMember member) {
            super();
            this.reversed = member.reversed;
            ManagedWay way = member.getManagedWay();
            nodesCount = way.getNodesCount();
            if (member.isReversed()) {
                iterator = way.getNodes().listIterator(way.getNodesCount() - 2);
            }
            else {
                iterator = way.getNodes().listIterator();
            }
        }

        @Override
        public boolean hasNext() {
            if (!reversed && iterator.nextIndex() < (nodesCount - 1)) {
                return true;
            }
            return iterator.hasPrevious();
        }

        @Override
        public Node next() {
            if (!hasNext()) {
                throw new IndexOutOfBoundsException();
            }
            if (!reversed) {
                return iterator.next();
            }
            return iterator.previous();
        }
    }

    @Override
    public Relation create(DataSet dataSet) {
        // TODO Implement this, or at least report an unsupported type
        throw new UnsupportedOperationException();
    }

    @Override
    public double getArea() {
        // TODO Implement this, or at least report an unsupported type
        throw new UnsupportedOperationException();
    }
}
