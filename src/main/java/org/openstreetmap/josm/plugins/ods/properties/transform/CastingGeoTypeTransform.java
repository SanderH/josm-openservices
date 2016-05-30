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
public class CastingGeoTypeTransform<T extends Geometry> extends SimpleTypeTransform<Geometry, T> {
    private final CRSUtil crsUtil = CRSUtil.getInstance();
    private final CoordinateReferenceSystem crs;
    
    public CastingGeoTypeTransform(CoordinateReferenceSystem crs, Class<T> targetClass) {
        super(Geometry.class, targetClass, null);
        this.crs = crs;
    }

    @Override
    public T apply(Geometry geometry) {
        try {
            @SuppressWarnings("unchecked")
            T result = (T) crsUtil.toOsm(geometry, crs);
            return result;
        } catch (CRSException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }
}
