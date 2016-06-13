package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public interface ManagedPolygon<T extends OsmPrimitive> extends ManagedPrimitive<T> {
    public ManagedRing<?> getExteriorRing();

    public Collection<ManagedRing<?>> getInteriorRings();

}
