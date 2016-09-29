package org.openstreetmap.josm.plugins.ods.entities;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Represent an entity in the remote (open data) layer.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface RemoteEntity extends Entity {
    public Geometry getGeometry();
}
