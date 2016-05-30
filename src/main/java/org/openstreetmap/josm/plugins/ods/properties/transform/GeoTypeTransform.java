package org.openstreetmap.josm.plugins.ods.properties.transform;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;

import com.vividsolutions.jts.geom.Geometry;

/**
 * TypeTransform implementation to transform Geometry object from an arbitrary crs to
 * the Josm CRS.
 * TODO Better exception handling in the apply() method and in the ETL process in general.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class GeoTypeTransform extends SimpleTypeTransform<Geometry, Geometry> {
    private final CRSUtil crsUtil = CRSUtil.getInstance();
    private final CoordinateReferenceSystem crs;
    
    public GeoTypeTransform(CoordinateReferenceSystem crs) {
        super(Geometry.class, Geometry.class, null);
        this.crs = crs;
    }

    @Override
    public Geometry apply(Geometry geometry) {
        if (geometry == null) return null;
        try {
            return crsUtil.toOsm(geometry, crs);
        } catch (CRSException e) {
            e.printStackTrace();
            return null;
        }
    }
}
