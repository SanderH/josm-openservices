package org.openstreetmap.josm.plugins.ods.osm;

import static org.junit.Assert.assertSame;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.osm.NodeDWithin;
import org.openstreetmap.josm.plugins.ods.osm.NodeDWithinLatLon;
import org.openstreetmap.josm.plugins.ods.osm.WayAligner;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedRing;
import org.openstreetmap.josm.plugins.ods.primitives.ManagedWay;
import org.openstreetmap.josm.plugins.ods.primitives.SimpleManagedRing;
import org.openstreetmap.josm.plugins.ods.primitives.SimpleManagedWay;
//import org.openstreetmap.josm.plugins.ods.test.JOSMFixture;
import org.openstreetmap.josm.plugins.ods.test.TestData;
import org.openstreetmap.josm.plugins.ods.test.TestLayerManager;

public class WayAlignerTest {
    private TestData testData;
    
    @BeforeClass
    public static void setUpBeforeClass() {
//        JOSMFixture.createUnitTestFixture().init();
    }

    @Before
    public void init() throws IOException, IllegalDataException {
        try {
            testData = new TestData(this);
        }
        catch (FileNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testBuilding3_4() {
        Way building3 = testData.getWay("building3");
        Way building4 = testData.getWay("building4");
        TestLayerManager layerManager = testData.getLayerManager();
        ManagedWay way3 = new SimpleManagedWay(layerManager, building3);
        ManagedWay way4 = new SimpleManagedWay(layerManager, building4);
        ManagedRing ring3 = new SimpleManagedRing(way3);
        ManagedRing ring4 = new SimpleManagedRing(way4);
        NodeDWithin dWithin = new NodeDWithinLatLon(0.05);
//        WayAligner aligner = new WayAligner(ring3, ring4, dWithin, false);
//        aligner.run();
        for (int i=0; i<2; i++) {
            assertSame(building3.getNodes().get(i+2), building4.getNodes().get(i));
        }
    }
}
