package org.openstreetmap.josm.plugins.ods.primitives;

import java.util.Collection;

/**
 * Managed MultiPolygon representation conform the OGC model of a multipolygon,
 * where a multipolygon is a collection of polygons. And a polygon contains the rings
 * inside that polygon.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface ManagedOgcMultiPolygon extends ManagedRelation {
    public Collection<ManagedPolygon> getPolygons();
}
