package org.openstreetmap.josm.plugins.ods.io;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.MatchTask;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

/**
 * The MainDownloader retrieves data within a bounding box from on one hand
 * the OSM server and on the other hand one or more external sources providing
 * open data.
 * There is a difference between OSM and most other GIS data providers in how
 * they handle layers. The OSM has a single layer containing all features which
 * may or may not be interconnected.
 * Regular GIS data sources contain one feature per layer.
 * Josm can handle multiple OSM layers.
 * The ODS plug-in uses the word layer to reference an OSM layer. Two OSM
 * layers are managed by the plug-in. The layer called OsmLayer contains the data
 * from the OSM server, an can be updated with data from the other layer
 * called OpenDataLayer (or OdLayer).
 * The term featureLayer is used for data from the external sources containing one
 * feature per layer. The data from all featureLayers together is used to compose
 * the data on the OpenDataLayer.
 *
 * Downloading takes place in 3 stages.
 * - A prepare stage that takes care of any necessary preparation tasks.
 * - A read stage that actually reads the data from the sources.
 * - A processing stage that performs all the processing of the retrieved data.
 * If any of the preceding stages fails, the subsequent states are ignored.
 *
 *
 *
 * @author Gertjan Idema
 *
 */
public class MainDownloader {
    private static final int NTHREADS = 10;
    //    private boolean initialized = false;
    //    private final OdsModule module;
    private final OpenDataLayerDownloader openDataLayerDownloader;
    private final OsmLayerDownloader osmLayerDownloader;

    private final List<MatchTask> matchTasks;

    private final ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
    private final TaskRunner prepareTaskRunner = new TaskRunner();
    private final TaskRunner readTaskRunner = new TaskRunner();
    private final TaskRunner processTaskRunner = new TaskRunner();
    private List<FeatureLayerDownloader> enabledDownloaders;

    //    private Status status = new Status();

    public MainDownloader(OpenDataLayerDownloader openDataLayerDownloader,
            OsmLayerDownloader osmLayerDownloader,
            List<MatchTask> matchTasks) {
        super();
        this.openDataLayerDownloader = openDataLayerDownloader;
        this.osmLayerDownloader = osmLayerDownloader;
        this.matchTasks = matchTasks;
        Thread.currentThread().setName("Main downloader");
    }

    public void initialize() throws OdsException {
        //        if (!initialized) {
        //            List<String> messages = new LinkedList<>();
        //            if (osmLayerDownloader != null) {
        //                try {
        //                    osmLayerDownloader.initialize();
        //                }
        //                catch (OdsException e) {
        //                    messages.add(e.getMessage());
        //                }
        //            }
        //            if (openDataLayerDownloader != null) {
        //                try {
        //                    openDataLayerDownloader.initialize();
        //                }
        //                catch (OdsException e) {
        //                    messages.add(e.getMessage());
        //                }
        //            }
        //            if (!messages.isEmpty()) {
        //                throw new OdsException("", messages);
        //            }
        //        }
        //        initialized = true;
    }

    public void run(ProgressMonitor pm, DownloadRequest request) throws OdsException, InterruptedException {
        pm.indeterminateSubTask(I18n.tr("Setup"));
        try {
            setup(request);
            pm.indeterminateSubTask(I18n.tr("Preparing"));
            TaskStatus status = prepare();
            if (status.isCancelled() || status.isFailed()) {
                return;
            }
            pm.indeterminateSubTask(I18n.tr("Downloading"));
            download();
            if (status.isCancelled() || status.isFailed()) {
                return;
            }
            pm.indeterminateSubTask(I18n.tr("Processing data"));
            DownloadResponse response = new DownloadResponse(request);
            process(response);
            if (status.isCancelled() || status.isFailed()) {
                return;
            }

            Bounds bounds = request.getBoundary().getBounds();
            computeBboxAndCenterScale(bounds);
            pm.finishTask();
        } catch (OdsException e) {
            Logging.error(e);
            throw e;
        } catch (CancellationException e) {
            pm.finishTask();
            return;
        }
    }

    /**
     * Setup the download jobs. One job for the Osm data and one for imported data.
     * Setup the download tasks. Maybe more than 1 per job.
     * @throws OdsException
     */
    private void setup(DownloadRequest request) throws OdsException {
        enabledDownloaders = new ArrayList<>();
        if (request.isGetOsm()) {
            enabledDownloaders.add(osmLayerDownloader);
        }
        if (request.isGetOds()) {
            enabledDownloaders.add(openDataLayerDownloader);
        }
        List<String> messages = new LinkedList<>();
        for (FeatureLayerDownloader downloader : enabledDownloaders) {
            try {
                downloader.setup(request);
            }
            catch (OdsException e) {
                messages.add(e.getMessage());
            }
            if (!messages.isEmpty()) {
                throw new OdsException(messages);
            }
        }
    }

    private TaskStatus prepare() throws CancellationException {
        List<Task> tasks = new ArrayList<>(2);
        for (FeatureLayerDownloader downloader : enabledDownloaders) {
            tasks.add(downloader.prepare());
        }
        prepareTaskRunner.runTasks(tasks, false);
        return prepareTaskRunner.getStatus();
    }

    private TaskStatus download() throws CancellationException {
        List<Task> tasks = new ArrayList<>(2);
        for (FeatureLayerDownloader downloader : enabledDownloaders) {
            tasks.add(downloader.download());
        }
        readTaskRunner.runTasks(tasks, false);
        return readTaskRunner.getStatus();
    }

    /**
     * Run the processing tasks.
     * @throws ExecutionException
     * @throws InterruptedException
     *
     */
    private TaskStatus process(DownloadResponse response) throws CancellationException, InterruptedException {
        List<Task> tasks = new ArrayList<>(2);
        for (FeatureLayerDownloader downloader : enabledDownloaders) {
            tasks.add(downloader.prepare());
        }
        tasks.addAll(matchTasks);
        // TODO add tag update tasks
        processTaskRunner.runTasks(tasks, false);
        TaskStatus status = processTaskRunner.getStatus();
        if (status.isCancelled() || status.isFailed()) {
            return status;
        }
        return status;
    }

    protected static void computeBboxAndCenterScale(Bounds bounds) {
        if (bounds != null) {
            new BoundingXYVisitor().visit(bounds);
            MainApplication.getMap().mapView.zoomTo(bounds);
        }
    }

    public void cancel() {
        for (FeatureLayerDownloader downloader : enabledDownloaders) {
            downloader.cancel();
        }
        executor.shutdownNow();
    }
}
