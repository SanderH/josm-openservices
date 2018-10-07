package org.openstreetmap.josm.plugins.ods.geotools;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityDao;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.geotools.impl.GtFeatureReaderImpl;
import org.openstreetmap.josm.plugins.ods.io.AbstractTask;
import org.openstreetmap.josm.plugins.ods.io.DefaultPrepareResponse;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.Task;
import org.openstreetmap.josm.plugins.ods.properties.EntityMapper;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class GtDownloader<T extends Entity> implements FeatureDownloader {
    // These fields are available to the subclasses
    final GtDataSource dataSource;
    final CRSUtil crsUtil;
    SimpleFeatureSource featureSource;
    Query baseQuery;
    DownloadRequest request;
    EntityDao<T> dao;

    //    private List<PropertyName> properties;
    @SuppressWarnings("unused")
    private DownloadResponse response;
    DefaultFeatureCollection downloadedFeatures;
    //    Repository repository;
    final EntityMapper<SimpleFeature, T> entityMapper;
    Normalisation normalisation = Normalisation.FULL;

    public GtDownloader(GtDataSource dataSource, CRSUtil crsUtil, EntityMapper<SimpleFeature, T> entityMapper,
            EntityDao<T> entityDao) {
        this.dataSource = dataSource;
        this.crsUtil = crsUtil;
        this.entityMapper = entityMapper;
        this.dao = entityDao;
    }

    //    @SuppressWarnings("unchecked")
    //    public GtDownloader(OdsModule module, GtDataSource dataSource) {
    //        this.dataSource = dataSource;
    //        this.crsUtil = module.getCrsUtil();
    //    }

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
    public Task prepare() {
        return new PrepareTask();
    }

    @Override
    public Task download() {
        return new DownloadTask();
    }

    @Override
    public Task process() {
        return new ProcessTask();
    }

    public OdsDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void cancel() {
        Thread.currentThread().interrupt();
    }

    public class PrepareTask extends AbstractTask {
        @Override
        public Void call() {
            try {
                Thread.currentThread().setName(dataSource.getFeatureType() + " prepare");
                DefaultPrepareResponse prepareResponse = new DefaultPrepareResponse();

                GtFeatureSource gtFeatureSource = dataSource.getOdsFeatureSource();
                gtFeatureSource.initialize();
                featureSource = gtFeatureSource.getFeatureSource();
                if (!isRequestInsideBoundary()) {
                    prepareResponse.setOutsideBoundary(true);
                    getStatus().failed(I18n.tr("The selected area is outside de boundary of the data source"));
                }
                // Create the base query
                baseQuery = dataSource.getQuery(request);
                return null;
            } catch (OdsException e) {
                getStatus().failed(e.getMessage());
                return null;
            } catch (Exception e) {
                Logging.error(e);
                throw e;
            }
        }

        private boolean isRequestInsideBoundary() {
            ReferencedEnvelope bounds = featureSource.getInfo().getBounds();
            if (bounds.isNull()) {
                Logging.info(I18n.tr("Feature source '{0}' doesn't report a boundary", featureSource.getName()));
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
    }

    public class DownloadTask extends AbstractTask {

        @Override
        public Void call() throws Exception {
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
            GtFeatureReader reader = new GtFeatureReaderImpl(dataSource, dataSource.getQuery(request));
            if (featureCollection.isEmpty()) {
                return null;
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
            return null;
        }
    }

    public class ProcessTask extends AbstractTask {
        @Override
        public Void call() throws Exception {
            Thread.currentThread().setName(dataSource.getFeatureType() + " process");
            try {
                for (SimpleFeature feature : downloadedFeatures) {
                    T entity = entityMapper.map(feature);
                    dao.insert(entity);
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                }
            }
            finally {
                // Clean-up
                downloadedFeatures.clear();
            }
            return null;
        }
    }
}
