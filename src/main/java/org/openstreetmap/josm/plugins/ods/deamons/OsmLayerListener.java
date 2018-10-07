package org.openstreetmap.josm.plugins.ods.deamons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.actions.MergeNodesAction;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.event.AbstractDatasetChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataSetListener;
import org.openstreetmap.josm.data.osm.event.NodeMovedEvent;
import org.openstreetmap.josm.data.osm.event.PrimitivesAddedEvent;
import org.openstreetmap.josm.data.osm.event.PrimitivesRemovedEvent;
import org.openstreetmap.josm.data.osm.event.RelationMembersChangedEvent;
import org.openstreetmap.josm.data.osm.event.TagsChangedEvent;
import org.openstreetmap.josm.data.osm.event.WayNodesChangedEvent;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntitiesBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

/**
 * Implementation of @DataSetListener that caches the change events and processes them in a background thread.
 *
 * @author Gertjan Idema
 *
 */
public class OsmLayerListener implements DataSetListener, Runnable {
    private final List<DataSetListener> childListeners = new LinkedList<>();
    private final List<PrimitivesAddedEvent> primitivesAddedCache = new LinkedList<>();
    private final List<PrimitivesRemovedEvent> primitivesRemovedCache = new LinkedList<>();
    private final List<WayNodesChangedEvent> wayNodesChangedCache = new LinkedList<>();
    private final Map<Node, NodeMovedEvent> nodesMovedCache = new HashMap<>();

    public OsmLayerListener(OsmLayerManager layerManager) {
        super();
        childListeners.add(new ODSTagRemover());
        childListeners.add(new MergeNodesHandler(layerManager));
        childListeners.add(new UpdateWayHandler(layerManager));
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            // continue processing
            try {
                Thread.sleep(200);
                realRun();
            } catch (InterruptedException e) {
                // good practice
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void realRun() {
        processPrimitivesAddedEvents();
        processPrimitivesRemovedEvents();
        processWayNodesChangedEvents();
        processNodesMovedEvents();
    }

    private void processPrimitivesAddedEvents() {
        // Take a snapshot from the event cache and clear the cache
        Collection<PrimitivesAddedEvent> events;
        synchronized (this) {
            events = new ArrayList<>(primitivesAddedCache);
            primitivesAddedCache.clear();
        }
        // pass the new events to the child listeners
        for (PrimitivesAddedEvent event : events) {
            for (DataSetListener listener : childListeners) {
                listener.primitivesAdded(event);
            }
        }
    }

    private void processPrimitivesRemovedEvents() {
        // Take a snapshot from the event cache and clear the cache
        Collection<PrimitivesRemovedEvent> events;
        synchronized (this) {
            events = new ArrayList<>(primitivesRemovedCache);
            primitivesRemovedCache.clear();
        }
        // pass the new events to the child listeners
        for (PrimitivesRemovedEvent event : events) {
            for (DataSetListener listener : childListeners) {
                listener.primitivesRemoved(event);
            }
        }
    }

    private void processWayNodesChangedEvents() {
        // Take a snapshot from the event cache and clear the cache
        Collection<WayNodesChangedEvent> events;
        synchronized (this) {
            events = new ArrayList<>(wayNodesChangedCache);
            wayNodesChangedCache.clear();
        }
        // pass the new events to the child listeners
        for (WayNodesChangedEvent event : events) {
            for (DataSetListener listener : childListeners) {
                listener.wayNodesChanged(event);
            }
        }
    }

    private void processNodesMovedEvents() {
        // Take a snapshot from the event cache and clear the cache
        if (nodesMovedCache.isEmpty()) return;
        Collection<NodeMovedEvent> events;
        synchronized (this) {
            events = new ArrayList<>(nodesMovedCache.values());
            nodesMovedCache.clear();
        }
        // pass the new events to the child listeners
        for (NodeMovedEvent event : events) {
            for (DataSetListener listener : childListeners) {
                listener.nodeMoved(event);
            }
        }
    }

    @Override
    public void primitivesAdded(PrimitivesAddedEvent event) {
        primitivesAddedCache.add(event);
    }

    @Override
    public void primitivesRemoved(PrimitivesRemovedEvent event) {
        primitivesRemovedCache.add(event);
    }

    @Override
    public void tagsChanged(TagsChangedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void nodeMoved(NodeMovedEvent event) {
        nodesMovedCache.put(event.getNode(), event);

    }

    @Override
    public void wayNodesChanged(WayNodesChangedEvent event) {
        //        if (event.getChangedWay().isModified()) {
        wayNodesChangedCache.add(event);
        //        }
    }

    @Override
    public void relationMembersChanged(RelationMembersChangedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void otherDatasetChange(AbstractDatasetChangedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dataChanged(DataChangedEvent event) {
        // TODO Auto-generated method stub

    }

    private static class UpdateWayHandler extends DefaultDataSetListener {
        private final OsmLayerManager layerManager;
        private final OsmEntitiesBuilder osmEntitiesBuilder;

        public UpdateWayHandler(OsmLayerManager layerManager) {
            super();
            this.layerManager = layerManager;
            this.osmEntitiesBuilder = layerManager.getEntitiesBuilder();
        }

        @Override
        public void wayNodesChanged(WayNodesChangedEvent event) {
            osmEntitiesBuilder.updatedGeometry(event.getChangedWay());
        }

        @Override
        public void nodeMoved(NodeMovedEvent event) {
            Node node = event.getNode();
            osmEntitiesBuilder.updatedGeometry(node);
            for (OsmPrimitive primitive : node.getReferrers()) {
                if (primitive.getType() == OsmPrimitiveType.WAY) {
                    osmEntitiesBuilder.updatedGeometry((Way) primitive);
                }
            }
        }

        @Override
        public void primitivesRemoved(PrimitivesRemovedEvent event) {
            for (OsmPrimitive primitive : event.getPrimitives()) {
                ManagedPrimitive mPrimitive = layerManager.getManagedPrimitive(primitive);
                if (mPrimitive != null && mPrimitive.getEntity() != null) {
                    //                    Entity entity = mPrimitive.getEntity();
                    //                    entity.getMatch();
                }
            }
        }
    }

    /**
     * Merge nodes of buildings copied to the Ods Osm layer.
     *
     * @author Gertjan Idema <mail@gertjanidema.nl>
     *
     */
    private static class MergeNodesHandler extends DefaultDataSetListener {
        private final OsmLayerManager layerManager;

        public MergeNodesHandler(OsmLayerManager layerManager) {
            super();
            this.layerManager = layerManager;
        }

        @Override
        public void primitivesAdded(PrimitivesAddedEvent event) {
            for (OsmPrimitive primitive : event.getPrimitives()) {
                if (primitive.getType() == OsmPrimitiveType.NODE) {
                    mergeNode((Node)primitive);
                }
            }
        }

        private void mergeNode(Node newNode) {
            // Skip nodes that have been deleted
            if (newNode.isDeleted()) {
                return;
            }
            DataSet data = layerManager.getOsmDataLayer().getDataSet();
            List<Node> nodes = data.searchNodes(new BBox(newNode));
            nodes.remove(newNode);
            if (nodes.size() == 1) {
                Node targetNode = nodes.get(0);
                if (!targetNode.hasKeys() && !targetNode.isDeleted()) {
                    Command cmd = MergeNodesAction.mergeNodes(
                            Collections.singleton(newNode), targetNode, targetNode);
                    // TODO Do we need an Undo option for this command?
                    cmd.executeCommand();
                }
            }
        }

    }
    /**
     * Remove any ODS tags from primitives that have been added to the internal (OSM) layer.
     *
     * @author Gertjan Idema <mail@gertjanidema.nl>
     *
     */
    private static class ODSTagRemover extends DefaultDataSetListener {
        public ODSTagRemover() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void primitivesAdded(PrimitivesAddedEvent event) {
            for (OsmPrimitive primitive : event.getPrimitives()) {
                if (primitive.hasKey(ODS.KEY.BASE)) {
                    for (Entry<String, String> entry : primitive.getKeys().entrySet()) {
                        if (entry.getKey().startsWith(ODS.KEY.BASE)) {
                            primitive.remove(entry.getKey());
                        }
                    }
                }
            }
        }
    }

    /**
     * Implementation of @DataSetListener that ignores all events.
     * Classes that extends this class only have to override the methods for the event they care about.
     *
     * @author Gertjan Idema <mail@gertjanidema.nl>
     *
     */
    private static class DefaultDataSetListener implements DataSetListener {

        public DefaultDataSetListener() {
            // Default constructor
        }

        @Override
        public void primitivesAdded(PrimitivesAddedEvent event) {
            // ignore
        }

        @Override
        public void primitivesRemoved(PrimitivesRemovedEvent event) {
            // ignore
        }

        @Override
        public void tagsChanged(TagsChangedEvent event) {
            // ignore
        }

        @Override
        public void nodeMoved(NodeMovedEvent event) {
            // ignore
        }

        @Override
        public void wayNodesChanged(WayNodesChangedEvent event) {
            // ignore
        }

        @Override
        public void relationMembersChanged(RelationMembersChangedEvent event) {
            // ignore
        }

        @Override
        public void otherDatasetChange(AbstractDatasetChangedEvent event) {
            // ignore
        }

        @Override
        public void dataChanged(DataChangedEvent event) {
            // ignore
        }
    }
}
