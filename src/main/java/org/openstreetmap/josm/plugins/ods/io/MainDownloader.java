package org.openstreetmap.josm.plugins.ods.io;

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
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

/**
 * Main downloader that retrieves data from multiple sources. Currently only a OSM source
 * and a single OpenData source are supported.
 * The
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class MainDownloader {
    private static final int NTHREADS = 10;
    private boolean initialized = false;
    private final OdsModule module;
    private OpenDataLayerDownloader openDataLayerDownloader;
    private OsmLayerDownloader osmLayerDownloader;

    private List<LayerDownloader> enabledDownloaders;

    private final ExecutorService executor;
    private final TaskRunner prepareTaskRunner = new TaskRunner();
    private final TaskRunner downloadTaskRunner = new TaskRunner();
    private final TaskRunner processTaskRunner = new TaskRunner();

    //    private Status status = new Status();

    public MainDownloader(OdsModule module) {
        super();
        this.module = module;
        this.executor = Executors.newFixedThreadPool(NTHREADS);
        Thread.currentThread().setName("Main downloader");
    }

    public final void setOpenDataLayerDownloader(
            OpenDataLayerDownloader openDataLayerDownloader) {
        this.openDataLayerDownloader = openDataLayerDownloader;
    }

    public final void setOsmLayerDownloader(OsmLayerDownloader osmLayerDownloader) {
        this.osmLayerDownloader = osmLayerDownloader;
    }

    public OdsModule getModule() {
        return module;
    }

    public void initialize() throws OdsException {
        if (!initialized) {
            List<String> messages = new LinkedList<>();
            if (osmLayerDownloader != null) {
                try {
                    osmLayerDownloader.initialize();
                }
                catch (OdsException e) {
                    messages.add(e.getMessage());
                }
            }
            if (openDataLayerDownloader != null) {
                try {
                    openDataLayerDownloader.initialize();
                }
                catch (OdsException e) {
                    messages.add(e.getMessage());
                }
            }
            if (!messages.isEmpty()) {
                throw new OdsException("", messages);
            }
        }
        initialized = true;
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
        enabledDownloaders = new LinkedList<>();
        if (request.isGetOsm()) {
            enabledDownloaders.add(osmLayerDownloader);
        }
        if (request.isGetOds()) {
            enabledDownloaders.add(openDataLayerDownloader);
        }
        List<String> messages = new LinkedList<>();
        for (LayerDownloader downloader : enabledDownloaders) {
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
        List<Task> tasks = LayerDownloader.getPrepareTasks(enabledDownloaders);
        prepareTaskRunner.runTasks(tasks, false);
        return prepareTaskRunner.getStatus();
    }

    private TaskStatus download() throws CancellationException {
        List<Task> tasks = LayerDownloader.getDownloadTasks(enabledDownloaders);
        downloadTaskRunner.runTasks(tasks, false);
        return downloadTaskRunner.getStatus();
    }

    /**
     * Run the processing tasks.
     * @throws ExecutionException
     * @throws InterruptedException
     *
     */
    private TaskStatus process(DownloadResponse response) throws CancellationException, InterruptedException {
        List<Task> tasks = LayerDownloader.getProcessTasks(enabledDownloaders);
        processTaskRunner.runTasks(tasks, false);
        TaskStatus status = processTaskRunner.getStatus();
        if (status.isCancelled() || status.isFailed()) {
            return status;
        }
        getModule().getMatchingProcessor().run();
        updateMatchTags();
        return status;
    }

    private void updateMatchTags() {
        getModule().getRepository().query(OdEntity.class).forEach(entity -> {
            entity.updateMatchTags();
        });
    }

    protected static void computeBboxAndCenterScale(Bounds bounds) {
        if (bounds != null) {
            new BoundingXYVisitor().visit(bounds);
            MainApplication.getMap().mapView.zoomTo(bounds);
        }
    }

    public void cancel() {
        for (LayerDownloader downloader : enabledDownloaders) {
            downloader.cancel();
        }
        executor.shutdownNow();
    }
}
