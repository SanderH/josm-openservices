package org.openstreetmap.josm.plugins.ods.io;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.tools.I18n;

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
    private boolean cancelled = false;
    private OdsModule module;
    private OpenDataLayerDownloader openDataLayerDownloader;
    private OsmLayerDownloader osmLayerDownloader;

    private List<LayerDownloader> enabledDownloaders;
    
    private ExecutorService executor;

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
    
    public void run(ProgressMonitor pm, DownloadRequest request) throws ExecutionException, InterruptedException {
        pm.indeterminateSubTask(I18n.tr("Setup"));
        cancelled = false;
        try {
            setup(request);
            pm.indeterminateSubTask(I18n.tr("Preparing"));
            prepare();
            if (cancelled) return;
            pm.indeterminateSubTask(I18n.tr("Downloading"));
            download();
            if (cancelled) return;
            pm.indeterminateSubTask(I18n.tr("Processing data"));
            DownloadResponse response = new DownloadResponse(request);
            process(response);
            if (cancelled) return;
            
            Bounds bounds = request.getBoundary().getBounds();
            computeBboxAndCenterScale(bounds);
            pm.finishTask();
        } catch (OdsException e) {
            throw new ExecutionException(e);
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

    private void prepare() throws ExecutionException, InterruptedException {
        runTasks(Downloader.getPrepareTasks(enabledDownloaders));
    }

    private void download() throws ExecutionException, InterruptedException {
        runTasks(Downloader.getDownloadTasks(enabledDownloaders));
    }
    
    /**
     * Run the processing tasks.
     * @throws ExecutionException 
     * @throws InterruptedException 
     * 
     */
    protected void process(DownloadResponse response) throws ExecutionException, InterruptedException {
        runTasks(Downloader.getProcessTasks(enabledDownloaders));
        for (Matcher<?> matcher : getModule().getMatcherManager().getMatchers()) {
            matcher.run();
        }
    }

    private void runTasks(List<Callable<Void>> tasks) throws ExecutionException, InterruptedException {
        try {
            List<Future<Void>> futures = executor.invokeAll(tasks, 1, TimeUnit.MINUTES);
            List<String> messages = new LinkedList<>();
            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof NullPointerException) {
                        messages.add(I18n.tr("A null pointer exception occurred. This is allways a programming error. " +
                            "Please look at the log file for more details"));
                        Main.error(e.getCause());
                    }
                    else {
                        messages.add(e.getCause().getMessage());
                    }
                }
            }
            if (!messages.isEmpty()) {
                throw new ExecutionException(String.join("\n",  messages), null);
            }
        } catch (InterruptedException e) {
            for (Downloader dl : enabledDownloaders) {
                dl.cancel();
            }
            throw e;
        }
    }

    protected static void computeBboxAndCenterScale(Bounds bounds) {
        if (bounds != null) {
            new BoundingXYVisitor().visit(bounds);
            Main.map.mapView.zoomTo(bounds);
        }
    }

    public void cancel() {
        cancelled = true;
        for (LayerDownloader downloader : enabledDownloaders) {
            downloader.cancel();
        }
        executor.shutdownNow();
    }
    
}
