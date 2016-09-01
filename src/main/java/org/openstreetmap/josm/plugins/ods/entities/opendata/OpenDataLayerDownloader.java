package org.openstreetmap.josm.plugins.ods.entities.opendata;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.Host;
import org.openstreetmap.josm.plugins.ods.io.LayerDownloader;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.osm.LayerUpdater;

/**
 * Downloader that retrieves open data objects from 1 or more services
 * and collects them in one layer.
 *  
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public abstract class OpenDataLayerDownloader implements LayerDownloader {
    private static final int NTHREADS = 10;

    private final OdsModule module;
    private final List<FeatureDownloader> downloaders;
    private ExecutorService executor;
    private List<Future<Void>> futures;
    private DownloadRequest request;
    private DownloadResponse response;

    public OpenDataLayerDownloader(OdsModule module) {
        this.module = module;
        this.downloaders = new LinkedList<>();
    }

    public OdsModule getModule() {
        return module;
    }

    @Override
    public void setResponse(DownloadResponse response) {
        this.response = response;
    }
    
    public DownloadResponse getResponse() {
        return response;
    }

    protected void addFeatureDownloader(FeatureDownloader featureDownloader) {
        this.downloaders.add(featureDownloader);
    }

    @Override
    public void setup(DownloadRequest request) throws OdsException {
        this.request = request;
        for (Host host : getHosts()) {
            host.initialize();
        }
        List<String> messages = new LinkedList<>();
        for (FeatureDownloader downloader : downloaders) {
            try {
                downloader.setup(request);
            }
            catch (OdsException e) {
                messages.add(e.getMessage());
            }
        }
        if (!messages.isEmpty()) {
            throw new OdsException("", messages);
        }
    }
    
    protected Collection<? extends Host> getHosts() {
        return module.getConfiguration().getHosts();
    }

    @Override
    public void prepare() throws ExecutionException {
        runTasks(Downloader.getPrepareTasks(downloaders));
    }
    
    @Override
    public void download() throws ExecutionException {
        runTasks(Downloader.getDownloadTasks(downloaders));
        this.response = new DownloadResponse(request);
    }
    
    @Override
    public void process() throws ExecutionException {
        runTasks(Downloader.getProcessTasks(downloaders));
        Boundary boundary = request.getBoundary();
        DataSource ds = new DataSource(boundary.getBounds(), "Import");
        module.getOpenDataLayerManager().extendBoundary(request.getBoundary().getMultiPolygon());
        OsmDataLayer osmDataLayer = module.getOpenDataLayerManager().getOsmDataLayer();
        osmDataLayer.data.dataSources.add(ds);
    }

    private void runTasks(List<Callable<Void>> tasks) throws ExecutionException {
        executor = Executors.newFixedThreadPool(NTHREADS);
        try {
            futures = executor.invokeAll(tasks, 1, TimeUnit.MINUTES);
            List<String> messages = new LinkedList<>();
            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    messages.add(e.getCause().getMessage());
                }
            }
            if (!messages.isEmpty()) {
                cancel();
                throw new ExecutionException(String.join("\n",  messages), null);
            }
        } catch (InterruptedException e) {
            // TODO do we need this?
            for (Future<Void> future : futures) {
                future.cancel(true);
            }
        }
    }

    @Override
    public void cancel() {
        executor.shutdownNow();
    }
    
    protected void updateLayer() {
        LayerUpdater updater = new LayerUpdater(this.module);
        updater.run();
    }
}
