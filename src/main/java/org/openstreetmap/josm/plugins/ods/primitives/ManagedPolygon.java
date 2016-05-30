package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.Relation;

public interface ManagedPolygon extends ManagedPrimitive<Relation> {
    public ManagedRing<?> getExteriorRing();

    public Collection<ManagedRing<?>> getInteriorRings();

}
