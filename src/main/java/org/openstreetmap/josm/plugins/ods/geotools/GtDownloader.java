package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;
import java.util.List;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.geotools.impl.GtFeatureReaderImpl;
import org.openstreetmap.josm.plugins.ods.io.DefaultPrepareResponse;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.PrepareResponse;
import org.openstreetmap.josm.plugins.ods.properties.EntityMapper;
import org.openstreetmap.josm.plugins.ods.storage.Repository;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class GtDownloader<T extends EntityType> implements FeatureDownloader {
    private final GtDataSource dataSource;
    private List<PropertyName> properties;
    private final CRSUtil crsUtil;
    private DownloadRequest request;
    @SuppressWarnings("unused")
    private DownloadResponse response;
    private SimpleFeatureSource featureSource;
    private Query baseQuery;
    //    DefaultFeatureCollection downloadedFeatures = new DefaultFeatureCollection();
    DefaultFeatureCollection downloadedFeatures;
    private final Repository repository;
    private final EntityMapper<SimpleFeature, T> entityMapper;
    private Normalisation normalisation = Normalisation.FULL;

    @SuppressWarnings("unchecked")
    public GtDownloader(OdsModule module, GtDataSource dataSource,
            T EntityType) {
        this.dataSource = dataSource;
        this.crsUtil = module.getCrsUtil();
        this.entityMapper = (EntityMapper<SimpleFeature, T>) dataSource.getEntityMapper();
        this.repository = module.getRepository();
    }

    @Override
    public void setNormalisation(Normalisation normalisation) {
        this.normalisation = normalisation;
    }


    @Override
    public void setup(DownloadRequest request) {
        this.request = request;
    }

    @Override
    public void setResponse(DownloadResponse response) {
        this.response = response;
    }

    @Override
    public PrepareResponse prepare() throws OdsException {
        Thread.currentThread().setName(dataSource.getFeatureType() + " prepare");
        DefaultPrepareResponse prepareResponse = new DefaultPrepareResponse();

        GtFeatureSource gtFeatureSource = (GtFeatureSource) dataSource.getOdsFeatureSource();
        gtFeatureSource.initialize();
        featureSource = gtFeatureSource.getFeatureSource();
        if (!isRequestInsideBoundary()) {
            prepareResponse.setOutsideBoundary(true);
            return prepareResponse;
        }
        // Create the base query
        baseQuery = createBaseQuery();
        return prepareResponse;
    }

    /**
     * Create the base query for this request. The base query is based on the dataSource query with the
     * following additions:
     * - Add geometry filter for the boundary.
     * - Add the query CRS
     * - Add the list of properties
     * @return
     */
    private Query createBaseQuery() {
        // Clone the query, so we can moderate the filter by setting the download area.
        Query query = new Query(dataSource.getQuery());
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        Name geometryProperty = featureSource.getSchema()
                .getGeometryDescriptor().getName();
        Filter filter = query.getFilter();
        filter = ff.intersects(ff.property(geometryProperty), ff.literal(getArea()));
        Filter dataFilter = dataSource.getQuery().getFilter();
        if (dataFilter != null) {
            filter = ff.and(filter, dataFilter);
        }
        query.setFilter(filter);
        query.setCoordinateSystem(featureSource.getSchema().getCoordinateReferenceSystem());
        if (properties != null) {
            baseQuery.setProperties(properties);
        }
        return query;
    }

    private boolean isRequestInsideBoundary() {
        ReferencedEnvelope bounds = featureSource.getInfo().getBounds();
        if (bounds.isNull()) {
            Main.info(I18n.tr("Feature source '{0}' doesn't report a boundary", featureSource.getName()));
            return true;
        }
        Envelope wgsBounds = crsUtil.toOsm(bounds, bounds.getCoordinateReferenceSystem());
        return wgsBounds.intersects(request.getBoundary().getEnvelope());
    }

    /**
     * Get the download area and transform to the desired
     * CoordinateReferenceSystem
     *
     * @return The transformed geometry
     */
    private Geometry getArea() {
        CoordinateReferenceSystem targetCRS = featureSource.getInfo().getCRS();
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

    //    @Override
    //    public void download() throws ExecutionException {
    //        Thread.currentThread().setName(dataSource.getFeatureType() + " download");
    //        String key = dataSource.getOdsFeatureSource().getIdAttribute();
    //        downloadedFeatures = new DefaultFeatureCollection(key);
    //        SimpleFeatureCollection featureCollection;
    //        try {
    //            featureCollection = featureSource.getFeatures(baseQuery);
    //        }
    //        catch (IOException e) {
    //            throw new ExecutionException("WrappedException", e);
    //        }
    //        if (featureCollection.isEmpty()) {
    //            return;
    //        }
    //        try (
    //            SimpleFeatureIterator it = featureCollection.features();
    //        )  {
    //           while (it.hasNext() && !Thread.currentThread().isInterrupted()) {
    //               SimpleFeature feature = it.next();
    //               FeatureUtil.normalizeFeature(feature, normalisation);
    //               downloadedFeatures.add(feature);
    //           }
    //           if (Thread.currentThread().isInterrupted()) {
    //               downloadedFeatures.clear();
    //           }
    //        }
    //        if (downloadedFeatures.isEmpty()) {
    //            if (dataSource.isRequired()) {
    //                String featureType = dataSource.getFeatureType();
    //                Main.info(I18n.tr("The selected download area contains no {0} objects.",
    //                            featureType));
    //            }
    //        }
    //        else {
    //            // Check if the data is complete
    //            Host host = dataSource.getOdsFeatureSource().getHost();
    //            Integer maxFeatures = host.getMaxFeatures();
    //            if (maxFeatures != null && downloadedFeatures.size() >= maxFeatures) {
    //                String featureType = dataSource.getFeatureType();
    //                throw new ExecutionException(I18n.tr(
    //                    "To many {0} objects. Please choose a smaller download area.", featureType), null);
    //            }
    //        }
    //    }

    @Override
    public void download() throws OdsException {
        Thread.currentThread().setName(dataSource.getFeatureType() + " download");
        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
        FeatureVisitor consumer = new FeatureVisitor() {
            @Override
            public void visit(Feature feature) {
                featureCollection.add((SimpleFeature) feature);
            }
        };
        consumer = dataSource.createVisitor(consumer);
        int maxFeatures = dataSource.getOdsFeatureSource().getHost().getMaxFeatures();
        GtFeatureReader reader = new GtFeatureReaderImpl(featureSource, baseQuery, maxFeatures);
        try {
            reader.read(consumer, null);
        } catch (IOException e) {
            throw new OdsException(e);
        }
        downloadedFeatures = new DefaultFeatureCollection();
        if (featureCollection.isEmpty()) {
            return;
        }
        try (
                SimpleFeatureIterator it = featureCollection.features();
                )  {
            while (it.hasNext() && !Thread.currentThread().isInterrupted()) {
                SimpleFeature feature = it.next();
                FeatureUtil.normalizeFeature(feature, normalisation);
                downloadedFeatures.add(feature);
            }
            if (Thread.currentThread().isInterrupted()) {
                downloadedFeatures.clear();
            }
        }
        if (downloadedFeatures.isEmpty()) {
            if (dataSource.isRequired()) {
                String featureType = dataSource.getFeatureType();
                Main.info(I18n.tr("The selected download area contains no {0} objects.",
                        featureType));
            }
        }
        else {
            // Check if the data is complete
            //          Host host = dataSource.getOdsFeatureSource().getHost();
            //          Integer maxFeatures = host.getMaxFeatures();
            //          if (maxFeatures != null && downloadedFeatures.size() >= maxFeatures) {
            //              String featureType = dataSource.getFeatureType();
            //              throw new ExecutionException(I18n.tr(
            //                  "To many {0} objects. Please choose a smaller download area.", featureType), null);
            //          }
        }
    }


    @Override
    public void process() {
        Thread.currentThread().setName(dataSource.getFeatureType() + " process");
        for (SimpleFeature feature : downloadedFeatures) {
            entityMapper.mapAndConsume(feature, repository::add);
            if (Thread.currentThread().isInterrupted()) {
                repository.clear();
                break;
            }
        }
        // Clean-up
        downloadedFeatures.clear();
    }

    public OdsDataSource getDataSource() {
        return dataSource;
    }


    @Override
    public void cancel() {
        Thread.currentThread().interrupt();
    }
}
