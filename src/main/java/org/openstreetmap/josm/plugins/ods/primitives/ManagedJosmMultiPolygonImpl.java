package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Relation;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class ManagedJosmMultiPolygonImpl extends AbstractManagedPrimitive<Relation> implements ManagedJosmMultiPolygon {
    private Collection<ManagedRing<?>> outerRings;
    private Collection<ManagedRing<?>> innerRings;
    private Envelope envelope;
    private boolean incomplete = false;
    
    public ManagedJosmMultiPolygonImpl(Collection<ManagedRing<?>> outerRings,
            Collection<ManagedRing<?>> innerRings, Relation relation) {
        super(relation);
        this.outerRings = outerRings;
        this.innerRings = innerRings;
    }

    public ManagedJosmMultiPolygonImpl(Relation multiPolygon, boolean incomplete) {
        super(multiPolygon);
        this.incomplete = incomplete;
    }

    
    @Override
    public boolean isIncomplete() {
        return incomplete;
    }

    @Override
    public Collection<ManagedRing<?>> outerRings() {
        return outerRings;
    }

    @Override
    public Collection<ManagedRing<?>> innerRings() {
        return innerRings;
    }

    @Override
    public Envelope getEnvelope() {
        if (envelope == null) {
            envelope = new Envelope();
            for (ManagedRing<?> ring : outerRings) {
                envelope = envelope.intersection(ring.getEnvelope());
            }
        }
        return envelope;
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
}
