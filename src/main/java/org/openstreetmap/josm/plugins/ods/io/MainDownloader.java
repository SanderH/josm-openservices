package org.openstreetmap.josm.plugins.ods.io;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.MainLayerManager;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.ods.OdsModule;
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
public abstract class MainDownloader {
    private static final int NTHREADS = 10;

    private final OdsModule module;

    private List<LayerDownloader> enabledDownloaders;

    private ExecutorService executorService;

    private Status status = new Status();

    public abstract void initialize() throws Exception;

    protected abstract LayerDownloader getOsmLayerDownloader();

    protected abstract LayerDownloader getOpenDataLayerDownloader();

    public MainDownloader(OdsModule module) {
        super();
        this.module = module;
    }

    public OdsModule getModule() {
        return module;
    }

    public void run(ProgressMonitor pm, DownloadRequest request) {
        status.clear();
        // Switch to the Open data layer before downloading.
        MainLayerManager layerManager = MainApplication.getLayerManager();
        layerManager.setActiveLayer(getModule().getOpenDataLayerManager().getOsmDataLayer());

        pm.indeterminateSubTask(I18n.tr("Setup"));
        setup(request);
        if (status.isCancelled()) {
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Preparing"));
        prepare();
        if (status.isCancelled()) {
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Downloading"));
        download();
        if (status.isCancelled()) {
            return;
        }
        if (!status.isSucces()) {
            pm.finishTask();
            JOptionPane.showMessageDialog(MainApplication.getMainFrame(), I18n.tr(
                    "An error occurred: " + status.getMessage()));
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Processing data"));
        DownloadResponse response = new DownloadResponse(request);
        process(response);
        if (!status.isSucces()) {
            pm.finishTask();
            JOptionPane.showMessageDialog(MainApplication.getMainFrame(), I18n.tr(
                    "An error occurred: " + status.getMessage()));
            return;
        }

        Bounds bounds = request.getBoundary().getBounds();
        computeBboxAndCenterScale(bounds);
        pm.finishTask();
    }

    /**
     * Setup the download jobs. One job for the Osm data and one for imported data.
     * Setup the download tasks. Maybe more than 1 per job.
     */
    private void setup(DownloadRequest request) {
        status.clear();
        enabledDownloaders = new LinkedList<>();
        if (request.isGetOsm()) {
            enabledDownloaders.add(getOsmLayerDownloader());
        }
        if (request.isGetOds()) {
            enabledDownloaders.add(getOpenDataLayerDownloader());
        }
        for (LayerDownloader downloader : enabledDownloaders) {
            downloader.setup(request);
        }
    }

    private void prepare() {
        status.clear();
        executorService = Executors.newFixedThreadPool(NTHREADS);
        for (final LayerDownloader downloader : enabledDownloaders) {
            executorService.execute(downloader::prepare);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
                status.setTimedOut(true);
                return;
            }
        }
        catch (InterruptedException e) {
            executorService.shutdownNow();
            for (LayerDownloader downloader : enabledDownloaders) {
                downloader.cancel();
            }
            status.setException(e);
            status.setFailed(true);
        }
        for (LayerDownloader downloader : enabledDownloaders) {
            Status st = downloader.getStatus();
            if (!st.isSucces()) {
                this.status = st;
            }
        }
    }

    private void download() {
        status.clear();
        executorService = Executors.newFixedThreadPool(NTHREADS);
        for (final LayerDownloader downloader : enabledDownloaders) {
            executorService.execute(downloader::download);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
                status.setTimedOut(true);
                return;
            }
        }
        catch (InterruptedException e) {
            executorService.shutdownNow();
            for (LayerDownloader downloader : enabledDownloaders) {
                downloader.cancel();
            }
            Logging.error(e);
            status.setException(e);
            status.setFailed(true);
        }
        List<String> failureMessages = new LinkedList<>();
        List<String> cancelMessages = new LinkedList<>();
        boolean timedOut = false;
        for (LayerDownloader downloader : enabledDownloaders) {
            Status st = downloader.getStatus();
            if (st.isFailed()) {
                failureMessages.add(st.getMessage());
            }
            if (st.isCancelled()) {
                cancelMessages.add(st.getMessage());
            }
            if (st.isTimedOut()) {
                timedOut = true;
            }
        }
        //                this.status.setMessage(this.status.getMessage() + "\n" + status.getMessage());
        if (!failureMessages.isEmpty()) {
            String message = String.join("\n", failureMessages);
            this.status.setFailed(true);
            this.status.setMessage(message);
            JOptionPane.showMessageDialog(MainApplication.getMainFrame(), I18n.tr("The download failed because of the following reason(s):\n" +
                    message));
            cancel();
        }
        else if (timedOut) {
            status.setTimedOut(true);
            JOptionPane.showMessageDialog(MainApplication.getMainFrame(), I18n.tr("The download timed out"));
        }
        else if (!cancelMessages.isEmpty()) {
            String message = String.join("\n", cancelMessages);
            this.status.setCancelled(true);
            this.status.setMessage(message);
            JOptionPane.showMessageDialog(MainApplication.getMainFrame(), I18n.tr("The download was cancelled because of the following reason(s):\n" +
                    message));

        }
    }

    /**
     * Run the tasks that depend on more than one entity store.
     *
     */
    protected void process(DownloadResponse response) {
        if (status.isFailed()) {
            return;
        }
        status.clear();
        executorService = Executors.newFixedThreadPool(NTHREADS);
        for (final LayerDownloader downloader : enabledDownloaders) {
            downloader.setResponse(response);
            executorService.execute(downloader::process);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
                status.setTimedOut(true);
                return;
            }
        }
        catch (InterruptedException e) {
            executorService.shutdownNow();
            //            for (LayerDownloader downloader : enabledDownloaders) {
            //                downloader.cancel();
            //            }
            status.setException(e);
            status.setFailed(true);
        }
        for (LayerDownloader downloader : enabledDownloaders) {
            Status st = downloader.getStatus();
            if (!st.isSucces()) {
                this.status = st;
            }
        }
    }

    protected static void computeBboxAndCenterScale(Bounds bounds) {
        BoundingXYVisitor v = new BoundingXYVisitor();
        if (bounds != null) {
            v.visit(bounds);
            MainApplication.getMap().mapView.zoomTo(bounds);
        }
    }

    public void cancel() {
        status.setCancelled(true);
        for (LayerDownloader downloader : enabledDownloaders) {
            downloader.cancel();
        }
        executorService.shutdownNow();
    }
}
