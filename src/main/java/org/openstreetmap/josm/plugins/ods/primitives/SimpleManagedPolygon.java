package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.openstreetmap.josm.data.osm.Way;

/**
 * Managed polygon implementation with only an outer ring.
 * Represented as a single osm Way.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class SimpleManagedPolygon extends SimpleManagedRing implements ManagedPolygon<Way>, ManagedRing<Way> {
    
    public SimpleManagedPolygon(ManagedWay exteriorWay,
            Map<String, String> tags) {
        super(exteriorWay);
    }

    @Override
    public ManagedRing<Way> getExteriorRing() {
        return this;
    }
    
    @Override
    public Collection<ManagedRing<?>> getInteriorRings() {
        return Collections.emptyList();
    }

//    @Override
//    public Way create(DataSet dataSet) {
//        Way way = getPrimitive();
//        if (way == null) {
//            ManagedRing<Way> outer  = getExteriorRing();
//            way = outer.create(dataSet);
//            way.setKeys(getKeys());
//            setPrimitive(way);
//            dataSet.addPrimitive(way);
//        }
//        return way;
//    }
}
