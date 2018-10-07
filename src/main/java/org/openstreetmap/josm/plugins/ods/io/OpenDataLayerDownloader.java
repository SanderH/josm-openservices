package org.openstreetmap.josm.plugins.ods.io;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.osm.LayerUpdater;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

/**
 * Downloader that retrieves open data objects from 1 or more services
 * and collects them in one layer.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OpenDataLayerDownloader implements FeatureLayerDownloader {

    final List<? extends FeatureDownloader> featureDownloaders;
    final TaskGroup prepareTasks;
    final TaskGroup readTasks;
    final TaskGroup processTasks;

    DownloadRequest request;
    private DownloadResponse response;
    final Repository repository = new Repository();

    public OpenDataLayerDownloader(List<? extends FeatureDownloader> featureDownloaders,
            List<Task> mainProcessTasks) {
        this.featureDownloaders = featureDownloaders;
        this.prepareTasks = createPrepareTasks();
        this.readTasks = createReadTasks();
        this.processTasks = createProcessTasks(mainProcessTasks);
    }

    /**
     * Create the TaskGroup for the preparation stage.
     * As these tasks are independent of each other, they will be
     * run in parallel.
     *
     * @return
     */
    private TaskGroup createPrepareTasks() {
        List<Task> tasks = new ArrayList<>();
        for (FeatureDownloader downloader :featureDownloaders) {
            tasks.add(downloader.prepare());
        }
        return new TaskGroup(tasks, true);
    }

    /**
     * Create the TaskGroup for the read stage.
     * As these tasks are independent of each other, they will be
     * run in parallel.
     *
     * @return
     */
    private TaskGroup createReadTasks() {
        List<Task> tasks = new ArrayList<>();
        for (FeatureDownloader downloader :featureDownloaders) {
            tasks.add(downloader.download());
        }
        return new TaskGroup(tasks, true);
    }

    private TaskGroup createProcessTasks(List<Task> overallProcessTasks) {
        List<Task> tasks = new LinkedList<>();
        List<Task> perFeatureTasks = new LinkedList<>();
        // Create the per-feature process tasks. These task can be run in
        // parallel
        for (FeatureDownloader downloader :featureDownloaders) {
            perFeatureTasks.add(downloader.download());
        }
        tasks.add(new TaskGroup(perFeatureTasks, true));
        // Add the overall process tasks.
        // These tasks must be run in sequence and after the per-feature
        // tasks have finished.
        tasks.addAll(overallProcessTasks);
        return new TaskGroup(tasks, false);
    }

    @Override
    public void setResponse(DownloadResponse response) {
        this.response = response;
    }

    public DownloadResponse getResponse() {
        return response;
    }

    @Override
    public void setup(DownloadRequest request) throws OdsException {
        this.request = request;
        //        for (Host host : getHosts()) {
        //            host.initialize();
        //        }
        List<String> messages = new LinkedList<>();
        for (FeatureDownloader downloader : featureDownloaders) {
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

    @Override
    public TaskGroup download() {
        return readTasks;
    }

    @Override
    public TaskGroup process() {
        return processTasks;
    }

    @Override
    public void cancel() {
        //
    }

    @Override
    public TaskGroup prepare() {
        return prepareTasks;
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
