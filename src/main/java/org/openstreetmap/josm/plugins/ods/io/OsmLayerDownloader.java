package org.openstreetmap.josm.plugins.ods.io;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.OsmApiException;
import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntitiesBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.jts.MultiPolygonFilter;
import org.openstreetmap.josm.plugins.ods.processing.OsmEntityRelationManager;
import org.openstreetmap.josm.tools.I18n;

public class OsmLayerDownloader implements LayerDownloader {
    DownloadRequest request;
    DownloadResponse response;
    private final DownloadSource downloadSource=  DownloadSource.OVERPASS;
    private OsmServerReader osmServerReader;
    OsmLayerManager layerManager;
    private final OdsModule module;
    private OsmHost host;
    DataSet dataSet;

    static enum DownloadSource {
        OSM,
        OVERPASS;
    }

    public OsmLayerDownloader(OdsModule module) {
        super();
        this.module = module;
    }

    @Override
    public void initialize() throws OdsException {
        this.layerManager = module.getOsmLayerManager();
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
    public void download() throws OdsException {
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
                    throw new OdsException(
                            I18n.tr("You tried to download too much Openstreetmap data. Please select a smaller download area."), e);
                case 404:
                    throw new OdsException(
                            I18n.tr("No OSM server could be found at this location: {0}",
                                    host.getHostString().toString()), e);
                case 504:
                    throw new OdsException(
                            I18n.tr("A timeout occurred for the OSM server at this location: {0}",
                                    host.getHostString().toString()), e);
                default:
                    throw new OdsException(I18n.tr(e.getMessage()), e);
                }
            }
            else if (e.getCause() instanceof UnknownHostException) {
                throw new OdsException(I18n.tr("Could not connect to OSM server ({0}). Please check your Internet connection.",  host.getHostString()), e);
            }
        }
    }

    //    @Override
    //    public PrepareResponse prepare() {
    //        // Nothing to prepare
    //        return null;
    //    }
    //
    //
    /**
     * Process the down loaded OSM primitives
     * @return
     *
     * @see org.openstreetmap.josm.plugins.ods.io.Downloader#process()
     */
    @Override
    public List<Task> process() {
        return Collections.singletonList(new InnerProcessTask());
    }

    private DataSet parseDataSet() throws OsmTransferException {
        return osmServerReader.parseOsm(NullProgressMonitor.INSTANCE);
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    @Override
    public void cancel() {
        osmServerReader.cancel();
    }

    @Override
    public List<PrepareTask> prepare() {
        // Nothing to prepare
        return Collections.emptyList();
    }

    public class InnerProcessTask extends ProcessTask {

        @Override
        public Void call() throws Exception {
            try {
                merge();
                buildEntities();
                updateRelations();
                return null;
            } catch (Exception e) {
                Main.error(e);
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
            layerManager.getOsmDataLayer().data.addDataSource(ds);
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
