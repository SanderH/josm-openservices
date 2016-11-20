package org.openstreetmap.josm.plugins.ods.test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.io.IllegalDataException;

public class TestData {
    private final static String DEFAULT_TEST_DATA = "testdata.osm";
    private final DataSet dataSet;
    private final TestLayerManager layerManager;
    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, Way> ways = new HashMap<>();
    private final Map<String, Relation> relations = new HashMap<>();
    private final Map<Long, Node> nodeIds = new HashMap<>();
    private final Map<Long, Way> wayIds = new HashMap<>();
    private final Map<Long, Relation> relationIds = new HashMap<>();
    
    public TestData(Object object) throws IOException, IllegalDataException {
        this(object, DEFAULT_TEST_DATA);
    }
    
    public TestData(Object object, String name) throws IOException, IllegalDataException {
        this(object.getClass(), name);
    }

    public TestData(Class<?> clazz) throws IOException, IllegalDataException {
        this(clazz, DEFAULT_TEST_DATA);
    }

    public TestData(Class<?> clazz, String name) throws IOException, IllegalDataException {
        this.layerManager = TestDataLoader.loadTestData(clazz, name);
        this.dataSet = layerManager.getOsmDataLayer().data;
        for (OsmPrimitive primitive :dataSet.allPrimitives()) {
            String ref = primitive.get("ref:test");
            switch (primitive.getType()) {
            case NODE:
                nodeIds.put(primitive.getId(), (Node) primitive);
                if (ref != null) {
                    nodes.put(ref, (Node)primitive);
                }
                break;
            case WAY:
                wayIds.put(primitive.getId(), (Way) primitive);
                if (ref != null) {
                    ways.put(ref, (Way)primitive);
                }
                break;
            case RELATION:
                relationIds.put(primitive.getId(), (Relation) primitive);
                if (ref != null) {
                    relations.put(ref, (Relation)primitive);
                }
                break;
            default:
                break;
            }
        }
    }
    
    public OsmDataLayer getDataLayer() {
        return layerManager.getOsmDataLayer();
    }

    public Node getNode(Long id) {
        Node node = nodeIds.get(id);
        if (node == null) {
            fail("No node with id '" + id + "' was found.");
        }
        return node;
    }
    
    public Way getWay(Long id) {
        Way way = wayIds.get(id);
        if (way == null) {
            fail("No way with id '" + id + "' was found.");
        }
        return way;
    } 
    
    public Relation getRelation(Long id) {
        Relation relation = relationIds.get(id);
        if (relation == null) {
            fail("No relation with id '" + id + "' was found.");
        }
        return relation;
    } 

    public Node getNode(String ref) {
        Node node = nodes.get(ref);
        if (node == null) {
            fail("No node with tag ref:test=" + ref + " was found.");
        }
        return nodes.get(ref);
    }
    
    public Way getWay(String ref) {
        Way way = ways.get(ref);
        if (way == null) {
            fail("No way with tag ref:test=" + ref + " was found.");
        }
        return way;
    } 
    
    public Relation getRelation(String ref) {
        Relation relation = relations.get(ref);
        if (relation == null) {
            fail("No relation with tag ref:test=" + ref + " was found.");
        }
        return relation;
    }

    public TestLayerManager getLayerManager() {
        return layerManager;
    }
}
