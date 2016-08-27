package org.openstreetmap.josm.plugins.ods.io;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

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
    private OdsModule module;
    private OpenDataLayerDownloader openDataLayerDownloader;
    private OsmLayerDownloader osmLayerDownloader;

    private List<LayerDownloader> enabledDownloaders;
    
    private List<Matcher<?>> matchers = new LinkedList<>();
    
    private ExecutorService executorService;

    private Status status = new Status();

    public MainDownloader(OdsModule module) {
        super();
        this.module = module;
    }

    public final void setOpenDataLayerDownloader(
            OpenDataLayerDownloader openDataLayerDownloader) {
        this.openDataLayerDownloader = openDataLayerDownloader;
    }

    public final void setOsmLayerDownloader(OsmLayerDownloader osmLayerDownloader) {
        this.osmLayerDownloader = osmLayerDownloader;
    }

    public void addMatcher(Matcher<?> matcher) {
        matchers.add(matcher);
    }
    
    public OdsModule getModule() {
        return module;
    }

    public void initialize() throws OdsException {
        if (osmLayerDownloader != null) {
            osmLayerDownloader.initialize();
        }
        if (openDataLayerDownloader != null) {
            openDataLayerDownloader.initialize();
        }
        for (Matcher<?> matcher : matchers) {
            matcher.initialize();
        }
    }
    
    public void run(ProgressMonitor pm, DownloadRequest request) {
        status.clear();
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
            JOptionPane.showMessageDialog(Main.parent, I18n.tr(
                "An error occurred: " + status.getMessage()));
            return;
        }
        pm.indeterminateSubTask(I18n.tr("Processing data"));
        DownloadResponse response = new DownloadResponse(request);
        process(response);
        if (!status.isSucces()) {
            pm.finishTask();
            JOptionPane.showMessageDialog(Main.parent, I18n.tr(
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
            enabledDownloaders.add(osmLayerDownloader);
        }
        if (request.isGetOds()) {
            enabledDownloaders.add(openDataLayerDownloader);
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
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            executorService.shutdownNow();
            for (LayerDownloader downloader : enabledDownloaders) {
                downloader.cancel();
            }
            status.setCancelled(true);
        }
        this.status = getStatus(enabledDownloaders);
    }

    private void download() {
        status.clear();
        executorService = Executors.newFixedThreadPool(NTHREADS);
        for (final LayerDownloader downloader : enabledDownloaders) {
            executorService.execute(downloader::download);
        }
        
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            executorService.shutdownNow();
            for (LayerDownloader downloader : enabledDownloaders) {
                downloader.cancel();
            }
            status.setCancelled(true);
            return;
        }
        status = getStatus(enabledDownloaders);
        if (this.status.isFailed()) {
            JOptionPane.showMessageDialog(Main.parent, I18n.tr("The download failed because of the following reason(s):\n" +
                status.getMessage()));
        }
        else if (this.status.isTimedOut()) {
            JOptionPane.showMessageDialog(Main.parent, I18n.tr("The download timed out"));
        }
        else if (this.status.isCancelled()) {
            JOptionPane.showMessageDialog(Main.parent, I18n.tr("The download was cancelled because of the following reason(s):\n" +
                status.getMessage()));
        }
    }
    
    /**
     * Run the tasks that depend on more than one entity store.
     * 
     */
    protected void process(DownloadResponse response) {
        status.clear();
        executorService = Executors.newFixedThreadPool(NTHREADS);
        for (final LayerDownloader downloader : enabledDownloaders) {
            downloader.setResponse(response);
            executorService.execute(downloader::process);
        }
        
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            executorService.shutdownNow();
//            for (LayerDownloader downloader : enabledDownloaders) {
//                downloader.cancel();
//            }
//            status.setException(e);
//            status.setFailed(true);
            return;
        }
        status = getStatus(enabledDownloaders);
        if (status.isSucces()) {
            for (Matcher<?> matcher : matchers) {
                matcher.run();
            }
        }
    }

    protected static void computeBboxAndCenterScale(Bounds bounds) {
        BoundingXYVisitor v = new BoundingXYVisitor();
        if (bounds != null) {
            v.visit(bounds);
            Main.map.mapView.zoomTo(bounds);
        }
    }

    public void cancel() {
        status.setCancelled(true);
        for (LayerDownloader downloader : enabledDownloaders) {
            downloader.cancel();
        }
        executorService.shutdownNow();
    }
    
    private Status getStatus(List<LayerDownloader> downloaders) {
        List<Status> statusses = enabledDownloaders.stream().map(dl->dl.getStatus())
                .filter(st->st.isFailed()).collect(Collectors.toList());
        return Status.getAggregate(statusses);
    }
}
