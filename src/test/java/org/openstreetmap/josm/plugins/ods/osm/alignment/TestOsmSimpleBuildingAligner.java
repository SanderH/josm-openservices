package org.openstreetmap.josm.plugins.ods.osm.alignment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingAligner;
import org.openstreetmap.josm.plugins.ods.test.TestData;

public class TestOsmSimpleBuildingAligner {
    private TestData testData;
    
    @BeforeClass
    public static void setUpBeforeClass() {
//        JOSMFixture.createUnitTestFixture().init();
    }

    @Before
    public void init() throws IOException, IllegalDataException {
        try {
            testData = new TestData(this, "buildingAligner.osm");
        }
        catch (FileNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testBuilding1() {
        Way building = testData.getWay("Eduard Verkadestraat 121");
        BuildingAligner aligner = new BuildingAligner(Collections.singleton(building));
        aligner.run();
    }
}
