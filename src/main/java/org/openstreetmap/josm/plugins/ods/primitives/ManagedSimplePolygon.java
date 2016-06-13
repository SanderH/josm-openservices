package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Managed polygon implementation with only an outer ring.
 * Represented as a single osm Way.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class ManagedSimplePolygon extends AbstractManagedPrimitive<Way> implements ManagedPolygon<Way> {
    private Entity entity;
    private ManagedRing<Way> exteriorRing;
    private Map<String, String> keys;
    
    public ManagedSimplePolygon(ManagedRing<Way> exteriorRing,
            Map<String, String> keys) {
        super();
        this.exteriorRing = exteriorRing;
        this.keys = keys;
    }

    @Override
    public Envelope getEnvelope() {
       return exteriorRing.getEnvelope();
    }

    @Override
    public BBox getBBox() {
        return exteriorRing.getBBox();
    }

    @Override
    public ManagedRing<Way> getExteriorRing() {
        return exteriorRing;
    }
    
    @Override
    public Collection<ManagedRing<?>> getInteriorRings() {
        return Collections.emptyList();
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public Map<String, String> getKeys() {
        return keys;
    }

    @Override
    public Way create(DataSet dataSet) {
        Way way = getPrimitive();
        if (way == null) {
            ManagedRing<Way> outer  = getExteriorRing();
            way = outer.create(dataSet);
            way.setKeys(getKeys());
            setPrimitive(way);
            dataSet.addPrimitive(way);
        }
        return way;
    }
}
