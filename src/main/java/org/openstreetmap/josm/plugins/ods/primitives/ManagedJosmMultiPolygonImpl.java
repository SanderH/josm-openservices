package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.plugins.ods.LayerManager;

/**
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class ManagedJosmMultiPolygonImpl extends AbstractManagedPrimitive implements ManagedJosmMultiPolygon {
    private Collection<ManagedRing> outerRings;
    private Collection<ManagedRing> innerRings;
    private double area;
    private boolean incomplete = false;
    
    public ManagedJosmMultiPolygonImpl(LayerManager layerManager, Collection<ManagedRing> outerRings,
            Collection<ManagedRing> innerRings, Relation relation) {
        super(layerManager, relation);
        this.outerRings = outerRings;
        this.innerRings = innerRings;
    }

    public ManagedJosmMultiPolygonImpl(LayerManager layerManager, Relation multiPolygon, boolean incomplete) {
        super(layerManager, multiPolygon);
        this.incomplete = incomplete;
    }

    @Override
    public boolean isIncomplete() {
        return incomplete;
    }

    @Override
    public Collection<ManagedRing> outerRings() {
        return outerRings;
    }

    @Override
    public Collection<ManagedRing> innerRings() {
        return innerRings;
    }

    
    @Override
    public BBox getBBox() {
        return getPrimitive().getBBox();
    }

    @Override
    public Relation create(DataSet dataSet) {
        // TODO Implement this method
        throw new UnsupportedOperationException();
    }

    @Override
    public double getArea() {
        if (area == 0) updateArea();
        return area;
    }

    private synchronized void updateArea() {
        area = 0;
        for (ManagedRing ring : outerRings()) {
            area += ring.getArea();
        }
        for (ManagedRing ring : innerRings()) {
            area -= ring.getArea();
        }
    }
}
