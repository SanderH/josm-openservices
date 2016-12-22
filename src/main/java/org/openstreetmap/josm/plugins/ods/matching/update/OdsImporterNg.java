package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.ArrayList;
import java.util.Collection;
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
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingAligner;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntitiesBuilder;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedPrimitive;

/**
 * TODO fix building alignment. Either in this class or through listeners
 * The importer imports objects from the OpenData layer to the Osm layer.
 * 
 * TODO Use AddPrimitivesCommand
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsImporterNg {
    private final OdsModule module;
    // TODO Make the importfilter(s) configurable
    private final ImportFilter importFilter = new DefaultImportFilter();
    // TODO Move buildingAligner out of this class in favour of a
    // Observer pattern
//    private final BuildingAligner buildingAligner;
    
    public OdsImporterNg(OdsModule module) {
        super();
        this.module = module;
//        this.buildingAligner=new BuildingAligner(module, 
//                module.getOsmLayerManager());
    }

    public void doImport(Collection<OsmPrimitive> primitives) {
        LayerManager layerManager = module.getOpenDataLayerManager();
        Set<OsmPrimitive> primitivesToImport = new HashSet<>();
        for (OsmPrimitive primitive : primitives) {
            ManagedPrimitive managedPrimitive = layerManager.getManagedPrimitive(primitive);
            if (managedPrimitive != null) {
                Entity entity = managedPrimitive.getEntity();
                if (entity != null && entity.getMatch(entity.getBaseType()) == null 
                        && importFilter.test(entity)) {
                    primitivesToImport.add(primitive);
                }
            }
//            for (OsmPrimitive referrer : primitive.getReferrers()) {
//                if (referrer.getType().equals(OsmPrimitiveType.RELATION)) {
//                    Entity referrerEntity = layerManager.getEntity(referrer);
//                    if (referrerEntity != null && referrerEntity.getMatch() == null 
//                          && importFilter.test(referrerEntity)) {
//                        entitiesToImport.add(referrerEntity);
//                    }
//                }
//            }
        }
        importPrimitives(primitivesToImport);
    }
    
    private void importPrimitives(Set<OsmPrimitive> primitives) {
        PrimitiveDataBuilder builder = new PrimitiveDataBuilder(module);
        for (OsmPrimitive primitive : primitives) {
//            if (primitive.g.getType().equals(OsmPrimitiveType.RELATION)) {
//                Relation relation = (Relation) primitive;
//                for (OsmPrimitive member : relation.getMemberPrimitives()) {
//                    primitivesToImport.add(member);
//                    builder.addPrimitive(member);
//                }
//            }
            builder.addPrimitive(primitive);
        }
        Command cmd = builder.getCommand();
        Main.main.undoRedo.add(builder.getCommand());
//        AddPrimitivesCommand cmd = new AddPrimitivesCommand(builder.primitiveData, null,
//            module.getOsmLayerManager().getOsmDataLayer());
//        cmd.executeCommand();
        Collection<? extends OsmPrimitive> importedPrimitives = cmd.getParticipatingPrimitives();
        removeOdsTags(importedPrimitives);
        buildImportedEntities(importedPrimitives);
        updateMatching();
        List<Way> ways = importedPrimitives.stream()
            .filter((OsmPrimitive p) -> p.getType() == OsmPrimitiveType.WAY)
            .map((OsmPrimitive p) -> (Way)p)
            .collect(Collectors.toList());
        if (!ways.isEmpty()) {
            BuildingAligner buildingAligner = new BuildingAligner(ways);
            buildingAligner.run();
        }
    }
    
    private void updateMatching() {
        for (Matcher<?> matcher : module.getMatcherManager().getMatchers()) {
            matcher.run();
        }
    }

    /**
     * Remove the ODS tags from the selected Osm primitives
     * 
     * @param osmData
     */
    private static void removeOdsTags(Collection<? extends OsmPrimitive> primitives) {
        for (OsmPrimitive primitive : primitives) {
            for (String key : primitive.keySet()) {
                if (key.startsWith(ODS.KEY.BASE)) {
                    primitive.remove(key);
                }
            }
        }
    }

    /**
     * Build entities for the newly imported primitives.
     * We could have created these entities from the OpenData entities instead. But by building them
     * from the Osm primitives, we make sure that all entities in the Osm layer are built the same way,
     * making them consistent with each other.
     * 
     * @param importedPrimitives
     */
    private void buildImportedEntities(
            Collection<? extends OsmPrimitive> importedPrimitives) {
        OsmEntitiesBuilder entitiesBuilder = module.getOsmLayerManager().getEntitiesBuilder();
        entitiesBuilder.build(importedPrimitives);
    }
    
    private class PrimitiveDataBuilder {
        private final OsmDataLayer layer;
        private final Map<Node, Node> nodeMap = new HashMap<>();
//        private List<PrimitiveData> primitiveData = new LinkedList<>();
        private List<Command> commands = new LinkedList<>();
        
        public PrimitiveDataBuilder(OdsModule module) {
            this.layer = module.getOsmLayerManager().getOsmDataLayer();
        }

//        public void addPrimitive(ManagedPrimitive managedPrimitive) {
//            OsmPrimitive primitive = managedPrimitive.getPrimitive();
//            primitiveData.add(primitive.save());
//            if (primitive.getType() == OsmPrimitiveType.WAY) {
//                for (Node node :((Way)primitive).getNodes()) {
//                    addPrimitive(node);
//                }
//            }
//            else if (primitive.getType() == OsmPrimitiveType.RELATION) {
//                for (OsmPrimitive osm : ((Relation)primitive).getMemberPrimitives()) {
//                    addPrimitive(osm);
//                }
//            }
//        }
        
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
        
        Command getCommand() {
            return new SequenceCommand("Import objects", commands);
        }
    }
}
