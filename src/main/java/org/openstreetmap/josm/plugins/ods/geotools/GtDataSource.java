package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.Geometry;

public class GtDataSource implements OdsDataSource {
    private final GtFeatureSource featureSource;
    private final CRSUtil crsUtil;
    private final List<String> properties;
    private final List<FilterFactory> filters;
    private final int pageSize;

    public GtDataSource(GtFeatureSource featureSource, CRSUtil crsUtil, List<String> properties, List<FilterFactory> filters, int pageSize) {
        this.featureSource = featureSource;
        this.crsUtil = crsUtil;
        this.properties = properties;
        this.filters = filters;
        this.pageSize = pageSize;
    }

    @Override
    public GtFeatureSource getOdsFeatureSource() {
        return featureSource;
    }

    /**
     * Build a FeatureVisitor that chains any filters in the right order.
     *
     * @param consumer
     * @return
     */
    public FeatureVisitor createVisitor(FeatureVisitor consumer) {
        if (filters.isEmpty()) {
            return consumer;
        }
        ArrayList<FilterFactory> filterFactories = new ArrayList<>(filters);
        Collections.reverse(filterFactories);
        FeatureVisitor result = consumer;
        for (FilterFactory factory : filterFactories) {
            FilteringFeatureVisitor visitor = factory.instance();
            visitor.setConsumer(result);
            result = visitor;
        }
        return result;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }


    @Override
    public void initialize() throws OdsException {
        // TODO Auto-generated method stub
    }

    @Override
    public String getFeatureType() {
        return featureSource.getFeatureName();
    }

    @Override
    public Query getQuery(DownloadRequest request) {
        Geometry area = getArea(request);
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        Name geometryProperty = featureSource.getFeatureType()
                .getGeometryDescriptor().getName();
        Filter filter = ff.intersects(ff.property(geometryProperty), ff.literal(area));
        Query query = new Query(featureSource.getFeatureName(), filter, properties.toArray(null));
        query.setCoordinateSystem(featureSource.getFeatureType().getCoordinateReferenceSystem());
        return query;
    }

    /**
     * Get the download area and transform to the desired
     * CoordinateReferenceSystem
     *
     * @return The transformed geometry
     */
    private Geometry getArea(DownloadRequest request) {
        CoordinateReferenceSystem targetCRS = featureSource.getCrs();
        Geometry area = request.getBoundary().getMultiPolygon();
        if (!targetCRS.equals(CRSUtil.OSM_CRS)) {
            try {
                area = crsUtil.fromOsm(area, targetCRS);
            } catch (CRSException e) {
                throw new RuntimeException(e);
            }
        }
        return area;
    }


    @Override
    public MetaData getMetaData() {
        return featureSource.getMetaData();
    }
}