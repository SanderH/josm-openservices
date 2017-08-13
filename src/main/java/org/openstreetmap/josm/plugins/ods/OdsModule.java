package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
//import org.openstreetmap.josm.plugins.ods.entities.managers.DataManager;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;
import org.openstreetmap.josm.plugins.ods.io.Host;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.processing.OsmEntityRelationManager;
import org.openstreetmap.josm.plugins.ods.storage.GeoRepository;
import org.openstreetmap.josm.plugins.ods.update.EntityUpdater;
import org.openstreetmap.josm.tools.I18n;

/**
 * The OdsModule is the main component of the ODS plugin. It manages a pair of interrelated layers
 * which are a normal OSM layer and a ODS layer containing data retrieved from an external open data source.
 * A third layer containing polygons to manage download areas is optional.
 *
 * The data in the ODS layer may be retrieved from multiple dataSources.
 *
 * @author Gertjan Idema
 *
 */
public abstract class OdsModule implements LayerChangeListener {
    private OdsModulePlugin plugin;

    private final Map<Class<? extends EntityType>, EntityType> entityTypes = new HashMap<>();

    private final List<OdsAction> actions = new LinkedList<>();
    private final List<OsmEntityBuilder> entityBuilders = new LinkedList<>();
    private final List<OsmEntityRelationManager> osmEntityRelationManagers = new LinkedList<>();

    private final GeoRepository repository = new GeoRepository();

    private final Map<String, OdsDataSource> dataSources = new HashMap<>();
    private OpenDataLayerManager openDataLayerManager;
    private PolygonLayerManager polygonDataLayer;
    private OsmLayerManager osmLayerManager;
    private final MatcherManager matcherManager = new MatcherManager(this);

    //    String osmQuery;
    private boolean initialized = false;
    private boolean active = false;


    protected void setPlugin(OdsModulePlugin plugin) {
        this.plugin = plugin;
    }

    public abstract OdsModuleConfiguration getConfiguration();

    public void initialize() throws OdsException {
        if (!initialized) {
            initializeEntityTypes();
            initializeHosts();
            initializeFeatureSources();
            initializeDataSources();
            this.osmLayerManager = createOsmLayerManager();
            this.openDataLayerManager = createOpenDataLayerManager();
            initializeOsmEntityBuilders();
            initializeOsmRelationManagers();
            Main.getLayerManager().addLayerChangeListener(this);
            initialized = true;
        }
    }

    private void initializeEntityTypes() {
        for (EntityType entityType : getConfiguration().getEntityTypes()) {
            entityTypes.put(entityType.getClass(), entityType);
        }
    }

    private void initializeHosts() throws OdsException {
        List<String> messages = new LinkedList<>();
        for (Host host : getConfiguration().getHosts()) {
            try {
                host.initialize();
            }
            catch (OdsException e) {
                messages.add(e.getLocalizedMessage());
            }
        }
        if (!messages.isEmpty()) {
            throw new OdsException(I18n.tr("The following problems(s) occured while trying to initialize this module:"), messages);
        }
    }

    private void initializeFeatureSources() throws OdsException {
        StringBuilder sb = new StringBuilder(100);
        for (OdsFeatureSource featureSource : getConfiguration().getFeatureSources()) {
            try {
                featureSource.initialize();
            }
            catch (OdsException e) {
                if (sb.length() == 0) {
                    sb.append("The following problems(s) occured while trying to initialize this module:\n");
                }
                sb.append(e.getMessage()).append("\n");
            }
        }
        if (sb.length() > 0) {
            throw new OdsException(sb.toString());
        }
    }

    private void initializeDataSources() throws OdsException {
        List<String> problems = new LinkedList<>();
        for (OdsDataSource dataSource : getConfiguration().getDataSources()) {
            try {
                dataSource.initialize();
            }
            catch (OdsException e) {
                problems.add(e.getMessage());
            }
        }
        if (!problems.isEmpty()) {
            StringBuilder sb = new StringBuilder(100);
            for (String problem : problems) {
                if (sb.length() == 0) {
                    sb.append("The following problems(s) occured while trying to initialize this module:\n");
                }
                sb.append(problem).append("\n");

            }
            throw new OdsException(sb.toString());
        }
    }

    private void initializeOsmEntityBuilders() throws OdsException {
        for (Class<? extends OsmEntityBuilder> builderClass : getConfiguration().getOsmEntityBuilders()) {
            try {
                OsmEntityBuilder builder;
                builder = builderClass.newInstance();
                builder.initialize(this);
                entityBuilders.add(builder);
            } catch (Exception e) {
                throw new OdsException(e);
            }
        }
    }

    private void initializeOsmRelationManagers() throws OdsException {
        for (Class<? extends OsmEntityRelationManager> managerClass : getConfiguration().getOsmRelationManagers()) {
            try {
                OsmEntityRelationManager manager = managerClass.newInstance();
                manager.initialize(this);
                this.osmEntityRelationManagers.add(manager);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new OdsException(e);
            }
        }
    }



    public List<OsmEntityBuilder> getEntityBuilders() {
        return entityBuilders;
    }

    public List<OsmEntityRelationManager> getOsmEntityRelationManagers() {
        return osmEntityRelationManagers;
    }

    public abstract GeoUtil getGeoUtil();

    public abstract CRSUtil getCrsUtil();

    public abstract String getName();

    public abstract String getDescription();

    public final Map<String, OdsDataSource> getDataSources() {
        return dataSources;
    }

    protected abstract OpenDataLayerManager createOpenDataLayerManager();

    protected abstract OsmLayerManager createOsmLayerManager();

    public OpenDataLayerManager getOpenDataLayerManager() {
        return openDataLayerManager;
    }

    public OsmLayerManager getOsmLayerManager() {
        return osmLayerManager;
    }

    public MatcherManager getMatcherManager() {
        return matcherManager;
    }

    public LayerManager getLayerManager(Layer activeLayer) {
        if (!isActive()) return null;
        if (openDataLayerManager.getOsmDataLayer() == activeLayer) {
            return openDataLayerManager;
        }
        if (osmLayerManager.getOsmDataLayer() == activeLayer) {
            return osmLayerManager;
        }
        return null;
    }

    public void addDataSource(OdsDataSource dataSource) {
        dataSources.put(dataSource.getFeatureType(), dataSource);
    }

    public boolean isActive() {
        return active;
    }

    public void activate() throws ModuleActivationException {
        try {
            this.initialize();
        }
        catch (Exception e) {
            throw new ModuleActivationException(e.getMessage(), e);
        }
        getOsmLayerManager().activate();
        getOpenDataLayerManager().activate();
        if (usePolygonFile()) {
            polygonDataLayer = new PolygonLayerManager(this);
            polygonDataLayer.activate();
        }
        active = true;
        JMenu menu = OpenDataServicesPlugin.INSTANCE.getMenu();
        for (OdsAction action : getActions()) {
            menu.add(action);
        }
    }

    public void deActivate() {
        if (isActive()) {
            getOsmLayerManager().deActivate();
            getOpenDataLayerManager().deActivate();
            polygonDataLayer.deActivate();
            active = false;
        }
    }

    public List<OdsAction> getActions() {
        return actions;
    }

    public void addAction(OdsAction action) {
        actions.add(action);
    }

    void activateOsmLayer() {
        Main.getLayerManager().setActiveLayer(getOsmLayerManager().getOsmDataLayer());
    }

    @Override
    public void layerAdded(LayerAddEvent event) {
        if (!isActive()) return;
    }

    @Override
    public void layerRemoving(LayerRemoveEvent event) {
        if (isActive() && !isExiting()) {
            LayerManager layerManager = this.getLayerManager(event.getRemovedLayer());
            if (layerManager != null && layerManager.isActive()) {
                layerManager.deActivate();
                String message = tr("You removed one of the layers that belong to the {0} module." +
                        " For the stability of the {0} module, you have to reset the module.", getName());
                JOptionPane.showMessageDialog(null, message, tr("ODS layer removed."), JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private static boolean isExiting() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if ("exitJosm".equals(element.getMethodName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void layerOrderChanged(LayerOrderChangeEvent e) {
        // No action required
    }

    public abstract Bounds getBounds();

    public abstract MainDownloader getDownloader();

    @SuppressWarnings("static-method")
    public boolean usePolygonFile() {
        return false;
    }

    public String getPluginDir() {
        return plugin.getPluginDir();
    }

    public abstract List<EntityUpdater> getUpdaters();

    /**
     * Reset the module
     * @throws OdsException
     */
    public void reset() throws OdsException {
        cleanUp();
        initialize();
    }

    /**
     * Clean up the module.
     */
    private void cleanUp() {
        matcherManager.reset();
        osmLayerManager.reset();
        openDataLayerManager.reset();
        entityBuilders.clear();
    }

    /**
     * Get the tolerance (in degrees) used to match nearby nodes and lines.
     * TODO provide more versatile configuration option
     *
     * @return
     */
    public abstract Double getTolerance();

    public GeoRepository getRepository() {
        return repository;
    }

    public <T extends EntityType> T getEntityType(Class<T> type) {
        @SuppressWarnings("unchecked")
        T entityType = (T) entityTypes.get(type);
        return entityType;
    }
}
