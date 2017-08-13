package org.openstreetmap.josm.plugins.ods.update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntitiesBuilder;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

/**
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsImporter {
    private final OdsModule module;

    private List<Way> importedWays;

    public OdsImporter(OdsModule module) {
        super();
        this.module = module;
    }

    public void doImport(Collection<OsmPrimitive> primitives) {
        LayerManager layerManager = module.getOpenDataLayerManager();
        Set<OsmPrimitive> primitivesToImport = new HashSet<>();
        for (OsmPrimitive primitive : primitives) {
            ManagedPrimitive managedPrimitive = layerManager
                    .getManagedPrimitive(primitive);
            if (managedPrimitive != null) {
                OdEntity<?> entity = (OdEntity<?>) managedPrimitive.getEntity();
                if (entity != null) {
                    if (entity.isCreationRequired()) {
                        primitivesToImport.add(primitive);
                    }
                }
                // for (OsmPrimitive referrer : primitive.getReferrers()) {
                // if (referrer.getType().equals(OsmPrimitiveType.RELATION)) {
                // Entity referrerEntity = layerManager.getEntity(referrer);
                // if (referrerEntity != null && referrerEntity.getMatch() ==
                // null
                // && importFilter.test(referrerEntity)) {
                // entitiesToImport.add(referrerEntity);
                // }
                // }
                // }
            }
        }
        importPrimitives(primitivesToImport);
    }

    private void importPrimitives(Set<OsmPrimitive> primitives) {
        PrimitiveDataBuilder builder = new PrimitiveDataBuilder(module);
        for (OsmPrimitive primitive : primitives) {
            // if (primitive.g.getType().equals(OsmPrimitiveType.RELATION)) {
            // Relation relation = (Relation) primitive;
            // for (OsmPrimitive member : relation.getMemberPrimitives()) {
            // primitivesToImport.add(member);
            // builder.addPrimitive(member);
            // }
            // }
            builder.addPrimitive(primitive);
        }
        List<Command> commands = builder.getCommands();
        if (!commands.isEmpty()) {
            Command cmd = new SequenceCommand("Import objects", commands);
            Main.main.undoRedo.add(cmd);
            // Remove any |ODS tags from the imported primitives
            Collection<? extends OsmPrimitive> importedPrimitives = cmd
                    .getParticipatingPrimitives();
            removeOdsTags(importedPrimitives);
            buildImportedEntities(importedPrimitives);
            updateMatching();
            importedWays = importedPrimitives.stream()
                    .filter((OsmPrimitive p) -> p
                            .getType() == OsmPrimitiveType.WAY)
                    .map((OsmPrimitive p) -> (Way) p)
                    .collect(Collectors.toList());
        }
    }

    private void updateMatching() {
        for (Matcher matcher : module.getMatcherManager().getMatchers()) {
            matcher.run();
        }
    }

    public List<Way> getImportedWays() {
        return (importedWays == null ? Collections.emptyList() : importedWays);
    }

    /**
     * Remove the ODS tags from the selected Osm primitives
     *
     * @param osmData
     */
    private static void removeOdsTags(
            Collection<? extends OsmPrimitive> primitives) {
        for (OsmPrimitive primitive : primitives) {
            for (String key : primitive.keySet()) {
                if (key.startsWith(ODS.KEY.BASE)) {
                    primitive.remove(key);
                }
            }
        }
    }

    /**
     * Build entities for the newly imported primitives. We could have created
     * these entities from the OpenData entities instead. But by building them
     * from the Osm primitives, we make sure that all entities in the Osm layer
     * are built the same way, making them consistent with each other.
     *
     * @param importedPrimitives
     */
    private void buildImportedEntities(
            Collection<? extends OsmPrimitive> importedPrimitives) {
        OsmEntitiesBuilder entitiesBuilder = module.getOsmLayerManager()
                .getEntitiesBuilder();
        entitiesBuilder.build(importedPrimitives);
    }

    private class PrimitiveDataBuilder {
        private final OsmDataLayer layer;
        private final Map<Node, Node> nodeMap = new HashMap<>();
        private final List<Command> commands = new LinkedList<>();

        public PrimitiveDataBuilder(OdsModule module) {
            this.layer = module.getOsmLayerManager().getOsmDataLayer();
        }

        public void addPrimitive(OsmPrimitive primitive) {
            switch (primitive.getType()) {
            case NODE:
                addNode((Node) primitive);
                break;
            case WAY:
                addWay((Way) primitive);
                break;
            case RELATION:
                break;
            default:
                break;
            }
        }

        public void addWay(Way odWay) {
            List<Node> nodes = new ArrayList<>(odWay.getNodesCount());
            for (Node odNode : odWay.getNodes()) {
                nodes.add(getNode(odNode, true));
            }
            Way newWay = new Way();
            newWay.setKeys(odWay.getKeys());
            newWay.setNodes(nodes);
            commands.add(new AddCommand(layer, newWay));
        }

        public void addNode(Node odNode) {
            getNode(odNode, false);
        }

        private Node getNode(Node odNode, boolean merge) {
            Node node = null;
            if (merge) {
                node = nodeMap.get(odNode);
                if (node == null) {
                    List<Node> nodes = layer.data.searchNodes(odNode.getBBox());
                    if (!nodes.isEmpty() && !nodes.get(0).isDeleted()) {
                        node = nodes.get(0);
                    }
                }
            }
            if (node == null) {
                node = new Node();
                node.load(odNode.save());
                commands.add(new AddCommand(layer, node));
                if (merge) {
                    nodeMap.put(odNode, node);
                }
            }
            return node;
        }

        List<Command> getCommands() {
            return commands;
        }
    }
}
