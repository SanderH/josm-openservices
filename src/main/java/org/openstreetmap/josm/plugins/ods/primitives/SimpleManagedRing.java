package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.tools.Geometry;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A ManagedRing implementation that is based on a single closed ManagedWay;
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class SimpleManagedRing extends AbstractManagedPrimitive implements ManagedRing {
    final ManagedWay managedWay;
    private Boolean isClockWise; // True if the nodes in this way are oriented clockwise.
    private double area = 0;

    public SimpleManagedRing(ManagedWay way) {
        this(way, null);
    }

    public SimpleManagedRing(ManagedWay managedWay, Boolean isClockWise) {
        super(managedWay.getLayerManager());
        this.managedWay = managedWay;
        this.isClockWise = isClockWise;
        if (!managedWay.isClosed()) {
            throw new UnsupportedOperationException(I18n.tr("This operation is only supported for closed ways"));
        }
    }

    @Override
    public Envelope getEnvelope() {
        return managedWay.getEnvelope();
    }

    @Override
    public BBox getBBox() {
        return managedWay.getBBox();
    }

    @Override
    public Map<String, String> getKeys() {
        return managedWay.getKeys();
    }

    @Override
    public boolean isClockWise() {
        if (isClockWise == null) {
            if (getPrimitive() != null) {
                isClockWise = Geometry.isClockwise((Way)getPrimitive());
            }
            else {
                isClockWise = isClockwise(this);
            }
        }
        return isClockWise;
    }

    @Override
    public int getNodesCount() {
        return managedWay.getNodesCount() - 1;
    }

    @Override
    public Iterator<ManagedNode> getNodeIterator() {
        return new RingNodeIterator();
    }
    
    /**
     * Determines whether path from nodes list is oriented clockwise.
     * @param nodes ManagedNodes list to be checked.
     * @return true if and only if way is oriented clockwise.
     * @throws IllegalArgumentException if way has less than 3 nodes
     */
    public static boolean isClockwise(ManagedRing ring) {
        double area2 = 0.;
        if (ring.getNodesCount() < 3) {
            throw new IllegalArgumentException("Way must be closed to check orientation.");
        }
        Iterator<ManagedNode> nodeIterator = ring.getNodeIterator();
        ManagedNode first = nodeIterator.next();
        LatLon coorPrev = first.getCoor();
        LatLon coorCurr;
        while (nodeIterator.hasNext()) {
            coorCurr = nodeIterator.next().getCoor();
            area2 += coorPrev.lon() * coorCurr.lat();
            area2 -= coorCurr.lon() * coorPrev.lat();
            coorPrev = coorCurr;
        }
        coorCurr = first.getCoor();
        area2 += coorPrev.lon() * coorCurr.lat();
        area2 -= coorCurr.lon() * coorPrev.lat();
        return area2 < 0;
    }
    
    private class RingNodeIterator implements Iterator<ManagedNode> {
        private ListIterator<ManagedNode> iterator;

        RingNodeIterator() {
            this.iterator = managedWay.getNodes().listIterator();
        }
        
        @Override
        public boolean hasNext() {
            return iterator.nextIndex() < managedWay.getNodesCount();
        }

        @Override
        public ManagedNode next() {
            if (iterator.nextIndex() >= managedWay.getNodesCount()) {
                throw new IndexOutOfBoundsException();
            }
            return iterator.next();
        }
    }

    @Override
    public double getArea() {
        if (area == 0) updateArea();
        return area;
    }

    private void updateArea() {
        this.area = Geometry.computeArea(managedWay.getPrimitive());
    }

    @Override
    public OsmPrimitive create(DataSet dataSet) {
        return managedWay.create(dataSet);
    }

    @Override
    public void putAll(Map<String, String> tags) {
        managedWay.putAll(tags);
    }

    @Override
    public void remove(String key) {
        managedWay.remove(key);
    }

    @Override
    public void put(String key, String value) {
        managedWay.put(key, value);
    }

    @Override
    public String get(String key) {
        return managedWay.get(key);
    }
    
    
}
