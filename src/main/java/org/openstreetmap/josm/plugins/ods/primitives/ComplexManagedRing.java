package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Relation;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A Managed ring that is constructed from a collection of ManagedWays.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class ComplexManagedRing extends AbstractManagedPrimitive<Relation> implements ManagedRing<Relation> {
    private boolean clockWise;
    private final List<RingMember> members;
    private int nodesCount;
    private Envelope envelope;

    public ComplexManagedRing(List<RingMember> members) {
        this(members, new HashMap<>());
    }
    
    public ComplexManagedRing(List<RingMember> members, Map<String, String> keys) {
        super(keys);
        this.members = members;
        nodesCount = 0;
        for (RingMember member : members) {
            nodesCount += member.getManagedWay().getNodesCount() - 1;
        }
    }


    @Override
    public Envelope getEnvelope() {
        if (envelope == null) {
            envelope = new Envelope();
            for (RingMember member : members) {
                envelope = envelope.intersection(member.getManagedWay().getEnvelope());
            }
        }
        return envelope;
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
    public Iterator<ManagedNode> getNodeIterator() {
        return new RingNodeIterator();
    }

    private class RingNodeIterator implements Iterator<ManagedNode> {
        private Iterator<RingMember> memberIterator;
        private Iterator<ManagedNode> memberNodeIterator;
        
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
        public ManagedNode next() {
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
        private boolean reversed;

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
        
        public Iterator<ManagedNode> nodeIterator() {
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
    private static class MemberNodeIterator implements Iterator<ManagedNode> {
        private final boolean reversed;
        private final int nodesCount;
        private final ListIterator<ManagedNode> iterator;

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
        public ManagedNode next() {
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
        // TODO Implement this functionality
        throw new UnsupportedOperationException();
    }
}
