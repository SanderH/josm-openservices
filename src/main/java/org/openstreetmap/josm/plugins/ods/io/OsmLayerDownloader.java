package org.openstreetmap.josm.plugins.ods.io;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.OsmApiException;
import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntitiesBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.jts.MultiPolygonFilter;
import org.openstreetmap.josm.plugins.ods.processing.OsmEntityRelationManager;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class OsmLayerDownloader implements FeatureLayerDownloader {
    private final OsmLayerManager layerManager;
    final DownloadSource downloadSource=  DownloadSource.OVERPASS;
    private final Task prepareTask = new Task.IdleTask();
    private final Task downloadTask = new DownloadTask();
    private final Task processTask = new ProcessTask();
    DownloadRequest request;
    DownloadResponse response;
    OsmServerReader osmServerReader;

    OsmHost host;
    DataSet dataSet;

    static enum DownloadSource {
        OSM,
        OVERPASS;
    }

    public OsmLayerDownloader(OsmLayerManager layerManager) {
        super();
        this.layerManager = layerManager;
    }

    @Override
    public void setResponse(DownloadResponse response) {
        this.response = response;
    }

    @Override
    public void setup(DownloadRequest request) throws OdsException {
        this.request = request;
        switch (downloadSource) {
        case OSM:
            host = new PlainOsmHost();
            break;
        case OVERPASS:
            host = new OverpassHost();
            break;
        default:
            return;
        }
        try {
            osmServerReader = host.getServerReader(request);
        } catch (MalformedURLException e) {
            throw new OdsException(e);
        }
    }

    @Override
    public Task prepare() {
        return prepareTask;
    }

    @Override
    public Task download() {
        return downloadTask;
    }

    /**
     * Process the downloaded OSM primitives
     * @return
     *
     * @see org.openstreetmap.josm.plugins.ods.io.Downloader#process()
     */
    @Override
    public Task process() {
        return processTask;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    @Override
    public void cancel() {
        osmServerReader.cancel();
    }

    class DownloadTask extends AbstractTask {

        @Override
        public Void call() throws Exception {
            try {
                dataSet = parseDataSet();
                if (downloadSource == DownloadSource.OSM) {
                    MultiPolygonFilter filter = new MultiPolygonFilter(request.getBoundary().getMultiPolygon());
                    dataSet = filter.filter(dataSet);
                }
            }
            catch(OsmTransferException e) {
                if (e instanceof OsmApiException) {
                    switch (((OsmApiException) e).getResponseCode()) {
                    case 400:
                        getStatus().failed(I18n.tr("You tried to download too much Openstreetmap data. Please select a smaller download area."));
                        break;
                    case 404:
                        getStatus().failed(I18n.tr("No OSM server could be found at this location: {0}",
                                host.getHostString().toString()));
                        break;
                    case 504:
                        getStatus().failed(I18n.tr("A timeout occurred for the OSM server at this location: {0}",
                                host.getHostString().toString()));
                        break;
                    default:
                        getStatus().failed(I18n.tr(e.getMessage()));
                        Logging.error(e);
                    }
                }
                else if (e.getCause() instanceof UnknownHostException) {
                    getStatus().failed((I18n.tr("Could not connect to OSM server ({0}). Please check your Internet connection.",  host.getHostString())));
                }
            }
            catch (Exception e) {
                Logging.error(e);
                throw e;
            }
            return null;
        }

        private DataSet parseDataSet() throws OsmTransferException {
            return osmServerReader.parseOsm(NullProgressMonitor.INSTANCE);
        }
    }
    public class ProcessTask extends AbstractTask {

        @Override
        public Void call() throws Exception {
            try {
                merge();
                buildEntities();
                updateRelations();
                return null;
            } catch (Exception e) {
                Logging.error(e);
                throw e;
            }
        }

        /**
         * Merge the down loaded OSM primitives into the existing
         * OSM layer.
         */
        private void merge() {
            layerManager.getOsmDataLayer().mergeFrom(dataSet);
            Boundary boundary = request.getBoundary();
            DataSource ds = new DataSource(boundary.getBounds(), "OSM");
            layerManager.getOsmDataLayer().getDataSet().addDataSource(ds);
        }

        /**
         * Build ods Entities from the down loaded OSM primitives.
         */
        private void buildEntities() {
            OsmEntitiesBuilder entitiesBuilder = layerManager.getEntitiesBuilder();
            entitiesBuilder.build();
        }

        /**
         * Update relations between the down loaded entities.
         */
        private void updateRelations() {
            for (OsmEntityRelationManager relationManager : layerManager.getRelationManagers()) {
                relationManager.createRelations();
            }
        }
    }
}
