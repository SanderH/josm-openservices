package org.openstreetmap.josm.plugins.ods.update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

public class DefaultGeometryUpdater implements GeometryUpdater {
    final OsmDataLayer osmDataLayer;

    public DefaultGeometryUpdater(OdsModule module) {
        this.osmDataLayer = module.getOsmLayerManager().getOsmDataLayer();
    }

    @Override
    public UpdateResult run(List<Match> allMatches) {
        List<Match> matches = allMatches.stream().filter(m -> m.getGeometryDifference() != null)
                .collect(Collectors.toList());
        Updater updater = new Updater(matches);
        return updater.run();
    }

    private class Updater {
        private final List<Match> matches;
        private final Set<Node> existingNodes = new HashSet<>();
        private final Set<Node> newNodes = new HashSet<>();
        private final Map<Node, Node> nodeMap = new HashMap<>();
        private final Set<Entity<?>> updatedEntities = new HashSet<>();
        private final Set<Way> updatedWays = new HashSet<>();

        public Updater(List<Match> matches) {
            this.matches = matches;
        }

        public UpdateResultImpl run() {
            collectNodes();
            matchNodes();
            updateGeometries();
            cleanUp();
            return new UpdateResultImpl(updatedEntities, updatedWays);
        }

        private void collectNodes() {
            //            for (Match match : this.matches) {
            //                SimpleManagedPolygon polygon = (SimpleManagedPolygon) match
            //                        .getOsmEntity().getPrimitive();
            //                Iterator<Node> it = polygon.getNodeIterator();
            //                while (it.hasNext()) {
            //                    existingNodes.add(it.next());
            //                }
            //                polygon = (SimpleManagedPolygon) match.getOpenDataEntity()
            //                        .getPrimitive();
            //                it = polygon.getNodeIterator();
            //                while (it.hasNext()) {
            //                    newNodes.add(it.next());
            //                }
            //            }
        }

        private void matchNodes() {
            DataSet dataSet = osmDataLayer.data;
            for (Node newNode : newNodes) {
                Iterator<Node> it = dataSet.searchNodes(newNode.getBBox())
                        .iterator();
                Node matchedNode = null;
                while ((matchedNode == null || matchedNode.isDeleted())
                        && it.hasNext()) {
                    matchedNode = it.next();
                }
                if (matchedNode != null && !matchedNode.isDeleted()) {
                    nodeMap.put(newNode, matchedNode);
                }
            }
        }

        private void updateGeometries() {
            //            for (Match match : matches) {
            //                updateGeometry(match.getOsmEntity(), match.getOpenDataEntity());
            //            }
        }

        private void cleanUp() {
            List<Command> commands = new LinkedList<>();
            for (Node node : existingNodes) {
                if (!node.isTagged() && node.getReferrers().isEmpty()
                        && !node.isIncomplete()) {
                    commands.add(new DeleteCommand(node));
                }
            }
            Command cmd = new SequenceCommand("Remove unconnected nodes",
                    commands);
            Main.main.undoRedo.add(cmd);
        }

        private void updateGeometry(Entity<?> osmEntity, Entity<?> odBuilding) {
            ManagedPrimitive osmPrimitive = osmEntity.getPrimitive();
            ManagedPrimitive odPrimitive = odBuilding.getPrimitive();
            // Only update osm ways to start with
            if (osmPrimitive.getPrimitive()
                    .getDisplayType() != OsmPrimitiveType.CLOSEDWAY
                    || odPrimitive.getPrimitive()
                    .getDisplayType() != OsmPrimitiveType.CLOSEDWAY) {
                return;
            }
            Way osmWay = (Way) osmPrimitive.getPrimitive();
            Way odWay = (Way) odPrimitive.getPrimitive();
            DataSet dataSet = osmDataLayer.data;
            List<Node> odNodes = odWay.getNodes();
            List<Node> newWayNodes = new ArrayList<>(odNodes.size());
            for (Node odNode : odNodes) {
                Node newNode = nodeMap.get(odNode);
                if (newNode == null) {
                    newNode = new Node();
                    newNode.load(odNode.save());
                    dataSet.addPrimitive(newNode);
                    nodeMap.put(odNode, newNode);
                }
                newWayNodes.add(newNode);
            }
            Command cmd = new ChangeNodesCommand(osmWay, newWayNodes);
            Main.main.undoRedo.add(cmd);
            updatedEntities.add(osmEntity);
            updatedWays.add(osmWay);
        }
    }
}
