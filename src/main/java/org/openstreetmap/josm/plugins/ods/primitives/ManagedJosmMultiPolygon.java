package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Collection;

/**
 * Managed MultiPolygon representation conform the Josm model of a multipolygon,
 * where a multipolygon consists of a collection of outer rings and a collection of inner rings.
 * 
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface ManagedJosmMultiPolygon extends ManagedPrimitive {
    public Collection<ManagedRing> outerRings();
    public Collection<ManagedRing> innerRings();
}
