package org.openstreetmap.josm.plugins.ods.primitives;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface OdsPrimitive<T extends OsmPrimitive> {
    public T getPrimitive();
    public Entity getEntity();
}
