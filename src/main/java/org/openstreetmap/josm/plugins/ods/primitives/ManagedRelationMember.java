package org.openstreetmap.josm.plugins.ods.primitives;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public interface ManagedRelationMember {
    public String getRole();
    public ManagedPrimitive<? extends OsmPrimitive> getPrimitive();
}
