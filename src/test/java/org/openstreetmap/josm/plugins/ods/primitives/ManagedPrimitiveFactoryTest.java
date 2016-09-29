package org.openstreetmap.josm.plugins.ods.primitives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.TestLayerManager;
import org.openstreetmap.josm.plugins.ods.test.TestData;

public class ManagedPrimitiveFactoryTest {
    private TestData testData;
    private ManagedPrimitiveFactory factory;

    @Before
    public void setup() throws IOException, IllegalDataException {
        try {
            testData = new TestData(this);
            LayerManager layerManager = new TestLayerManager(testData.getDataLayer());
            factory = new ManagedPrimitiveFactory(layerManager);
        }
        catch (FileNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateNode() {
        Node node = testData.getNode("moreelsepark3");
        ManagedNode managedNode = factory.createNode(node);
        assertEquals("Streetname should be 'Moreelsepark'", "Moreelsepark", managedNode.getKeys().get("addr:street"));
    }
    
    @Test
    public void testCreateNodeTwice() {
        Node node = testData.getNode("moreelsepark3");
        ManagedNode mn1 = factory.createNode(node);
        ManagedNode mn2 = factory.createNode(node);
        assertSame("Two calls to createNode with same parameter should return same result", mn1, mn2);
    }
    
    @Test
    public void testCreateSimpleRing() {
        Way way = testData.getWay("inktpotInner1");
        assert way.isClosed();
        ManagedRing managedRing = factory.createRing(way);
        assertNotNull("Ring shouldExist", managedRing);
    }
    
    @Test
    public void testCreateSimpleRingTwice() {
        Way way = testData.getWay("inktpotInner1");
        assert way.isClosed();
        ManagedRing mr1 = factory.createRing(way);
        ManagedRing mr2 = factory.createRing(way);
        assertSame("Two calls to createRing with same parameter should return same result", mr1, mr2);
    }
    
    @Test
    public void testSimpleRingNodeCount() {
        Way way = testData.getWay("inktpotInner1");
        SimpleManagedRing ring = factory.createRing(way);
        assertEquals("Number of nodes in a ring should be 1 less than in the related closed way", way.getNodesCount() - 1, ring.getNodesCount());
    }
    

//    @Test
//    public void testCreateRingNode() {
//        Way way = testData.getWay("inktpotInner1");
//        assert way.isClosed();
//        Node node = testData.getNode("inktpotInner1Node6");
//        ManagedRing mr = factory.createRing(way);
//        Iterator<ManagedNode> it = mr.getNodeIterator();
//        for (int i = 0; i < 6; i++) {
//            it.next();
//        }
//        ManagedNode mn = factory.createNode(node);
//        assertSame("Related nodes should be equal", it.next(), mn);
//    }
    
    @Test
    public void testCreateArea_type() {
        Relation relation = testData.getRelation("inktpotInner2");
        ManagedPrimitive area = factory.createArea(relation);
        assertTrue("Area should exist and be of type 'MultiPolygon'", 
            area != null && area instanceof ManagedJosmMultiPolygon);
    }
    
    @Test
    public void testCreateArea_tags() {
        Relation relation = testData.getRelation("inktpotInner2");
        ManagedPrimitive  area = factory.createArea(relation);
        assertEquals("Area should have the same tags as the related Relation", 
            area.getKeys(), relation.getKeys());
    }
    
    @Test
    public void testCreateArea2() {
        Relation relation = testData.getRelation("inktpot");
//        assert way.isClosed();
        ManagedPrimitive area = factory.createArea(relation);
        assertNotNull("Area shouldExist", area);
    }
    


    @After
    public void tearDown() {
        testData = null;
        factory = null;
    }
}
