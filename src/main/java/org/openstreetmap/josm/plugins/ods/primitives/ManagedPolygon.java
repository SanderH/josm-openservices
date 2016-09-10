package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Collection;

public interface ManagedPolygon extends ManagedPrimitive {
    public ManagedRing getExteriorRing();

    public Collection<ManagedRing> getInteriorRings();

}
