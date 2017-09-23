package org.openstreetmap.josm.plugins.ods.io;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;
import org.openstreetmap.josm.plugins.ods.domains.addresses.processing.AddressNodeDistributor;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.processing.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.domains.buildings.processing.BuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.entities.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.osm.LayerUpdater;
import org.openstreetmap.josm.plugins.ods.storage.Repository;

/**
 * Downloader that retrieves open data objects from 1 or more services
 * and collects them in one layer.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public abstract class OpenDataLayerDownloader implements LayerDownloader {

    final OdsModule module;
    final List<FeatureDownloader> downloaders;
    DownloadRequest request;
    private DownloadResponse response;
    final Repository repository = new Repository();

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
    public Optional<Task> download() {
        List<Task> subTasks = FeatureDownloader.getDownloadTasks(downloaders);
        return Optional.of(new TaskGroup(subTasks, false));
    }

    @Override
    public Optional<Task> process() {
        List<Task> mainTasks = new LinkedList<>();
        repository.clear();
        downloaders.forEach(dl -> dl.setRepository(repository));
        List<Task> subTasks = FeatureDownloader.getProcessTasks(downloaders);
        mainTasks.add(new TaskGroup(subTasks, false));
        List<Class<? extends Task>> taskdefs = new LinkedList<>();
        taskdefs.add(MergeEntitiesTask.class);
        taskdefs.add(ExtractAddressNodeEntitiesTask.class);
        taskdefs.add(UpdateBoundariesTask.class);
        taskdefs.addAll(getProcessors());
        taskdefs.add(BuildPrimitivesTask.class);
        mainTasks.addAll(Task.createTasks(taskdefs, this));
        return Optional.of(new TaskGroup(mainTasks, false));
    }

    protected abstract List<Class<? extends Task>> getProcessors();

    @Override
    public void cancel() {
        //
    }

    protected PrimitiveBuilder getPrimitiveBuilder() {
        return new PrimitiveBuilder(getModule());
    }

    @Override
    public Optional<Task> prepare() {
        List<Task> tasks = FeatureDownloader.getPrepareTasks(downloaders);
        return Optional.of(new TaskGroup(tasks, false));
    }

    public class BuildPrimitivesTask extends AbstractTask {
        private final Collection<Class<? extends Task>> DEPENDENCIES = Arrays.asList(
                BuildingCompletenessEnricher.class,
                AddressNodeDistributor.class,
                BuildingTypeEnricher.class);

        @Override
        public Collection<Class<? extends Task>> getDependencies() {
            return DEPENDENCIES;
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
        private static Collection<Class<? extends Task>> DEPENDENCIES = Arrays.asList(OpenDataLayerDownloader.BuildPrimitivesTask.class);
        private final OdsModule module = OpenDataServicesPlugin.getModule();

        @Override
        public Collection<Class<? extends Task>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public Void call() throws Exception {
            LayerUpdater updater = new LayerUpdater(module);
            updater.run();
            return null;
        }
    }

    /**
     * Merge new entities into the main repository of this module.
     *
     * @author Gertjan Idema
     *
     */
    public class MergeEntitiesTask extends AbstractTask {
        @Override
        public Void call() throws Exception {
            Repository repo = getModule().getRepository();
            repository.query().forEach(repo::add);
            return null;
        }
    }

    /**
     * Extract Address node Entities from building units and merge them
     *  into the main repository of this module.
     *
     * @author Gertjan Idema
     *
     */
    public class ExtractAddressNodeEntitiesTask extends AbstractTask {
        @Override
        public Void call() throws Exception {
            Repository repo = getModule().getRepository();
            repository.query(BuildingUnit.class).forEach(bu -> {
                repo.add(bu.getMainAddressNode());
            });
            return null;
        }
    }
}
