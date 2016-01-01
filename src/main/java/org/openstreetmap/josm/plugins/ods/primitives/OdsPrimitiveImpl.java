package org.openstreetmap.josm.plugins.ods.primitives;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public class OdsPrimitiveImpl<T extends OsmPrimitive> implements OdsPrimitive<T> {
    private final T osm;
    private Entity entity = null;
    
    public OdsPrimitiveImpl(T osmPrimitive) {
        super();
        this.osm = osmPrimitive;
    }

    @Override
    public T getPrimitive() {
        return osm;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }
    
}
