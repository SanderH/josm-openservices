package org.openstreetmap.josm.plugins.ods.io;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;
import org.openstreetmap.josm.plugins.ods.entities.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.osm.LayerUpdater;
import org.openstreetmap.josm.plugins.ods.storage.Repository;
import org.openstreetmap.josm.tools.Logging;

/**
 * Downloader that retrieves open data objects from 1 or more services
 * and collects them in one layer.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public abstract class OpenDataLayerDownloader implements LayerDownloader {
    private static final int NTHREADS = 10;

    final OdsModule module;
    final List<FeatureDownloader> downloaders;
    private ExecutorService executor;
    private List<Future<Void>> futures;
    DownloadRequest request;
    private DownloadResponse response;
    boolean cancelled = false;

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

    //    @Override
    //    public PrepareResponse prepare() throws OdsException {
    //        runTasks(Downloader.getPrepareTasks(downloaders));
    //        return new DefaultPrepareResponse();
    //    }

    @Override
    public void download() throws OdsException {
        runTasks(Downloader.getDownloadTasks(downloaders));
        this.response = new DownloadResponse(request);
    }

    @Override
    public List<? extends Task> process() {
        Thread.currentThread().setName("OpenDataLayerDownloader process");
        Repository repository = new Repository();
        List<Task> tasks = new LinkedList<>();
        for (Downloader downloader : downloaders) {
            tasks.addAll(downloader.process());
        }
        List<Class<? extends Task>> taskdefs = new LinkedList<>();
        taskdefs.addAll(getProcessors());
        taskdefs.add(BuildPrimitivesTask.class);
        taskdefs.add(UpdateBoundariesTask.class);
        return Task.createTasks(taskdefs, this);
    }

    //    private void runProcessors() throws OdsException {
    //        for (Class<? extends OdsProcessor> processorClass : getProcessors()) {
    //            OdsProcessor processor;
    //            try {
    //                processor = processorClass.newInstance();
    //                processor.run();
    //            } catch (InstantiationException | IllegalAccessException e) {
    //                throw new OdsException(e);
    //            }
    //        }
    //    }

    private void runTasks(List<Callable<Void>> tasks) throws OdsException {
        executor = Executors.newFixedThreadPool(NTHREADS);
        cancelled = false;
        try {
            futures = executor.invokeAll(tasks);
            List<String> messages = new LinkedList<>();
            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (CancellationException e1) {
                    cancelled = true;
                } catch (ExecutionException executionException) {
                    Exception e = (Exception) executionException.getCause();
                    if (e instanceof NullPointerException) {
                        messages.add("A null pointer exception occurred. This is allways a programming error\n" +
                                "please check the logs.");
                        Logging.error(e);
                    }
                    else {
                        messages.add(e.getMessage());
                    }
                }
            }
            if (!messages.isEmpty()) {
                //                cancel();
                throw new OdsException(String.join("\n",  messages));
            }
            executor.shutdownNow();
        } catch (InterruptedException e) {
            // TODO do we need this?
            for (Future<Void> future : futures) {
                future.cancel(true);
            }
        }
    }

    protected abstract List<Class<? extends Task>> getProcessors();

    @Override
    public void cancel() {
        executor.shutdownNow();
    }

    protected PrimitiveBuilder getPrimitiveBuilder() {
        return new PrimitiveBuilder(getModule());
    }

    @Override
    public List<PrepareTask> prepare() {
        List<PrepareTask> tasks = new LinkedList<>();
        for (FeatureDownloader downloader : downloaders) {
            tasks.addAll(downloader.prepare());
        }
        return tasks;
    }

    public class BuildPrimitivesTask extends AbstractTask {
        @Override
        public Collection<Class<? extends Task>> getDependencies() {
            return super.getDependencies();
        }

        @Override
        public Void call() throws Exception {
            getPrimitiveBuilder().run(getResponse());
            return null;
        }
    }

    public class UpdateBoundariesTask extends AbstractTask {

        @Override
        public Void call() throws Exception {
            Boundary boundary = request.getBoundary();
            DataSource ds = new DataSource(boundary.getBounds(), "Import");
            module.getOpenDataLayerManager().extendBoundary(request.getBoundary().getMultiPolygon());
            OsmDataLayer osmDataLayer = module.getOpenDataLayerManager().getOsmDataLayer();
            osmDataLayer.data.addDataSource(ds);
            return null;
        }
    }

    public static class UpdateLayerTask extends AbstractTask {
        private final OdsModule module = OpenDataServicesPlugin.getModule();
        @Override
        public Void call() throws Exception {
            LayerUpdater updater = new LayerUpdater(module);
            updater.run();
            return null;
        }
    }
}
