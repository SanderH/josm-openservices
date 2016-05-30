package org.openstreetmap.josm.plugins.ods.test.file.wfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import org.geotools.feature.NameImpl;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.plugins.ods.wfs.file.FileWFSDataStore;

public class TestTestDataLoader {
    @Test
    public void testLoading() throws IOException, IllegalDataException {
        File dir = new File (FileWFSDataStore.class.getResource("inktpot/1_1_0").getFile());
        TestData testData = TestDataLoader.loadTestData(dir, new String[] {"verblijfsobject"});
        Name typeName = new NameImpl("http://bag.geonovum.nl", "verblijfsobject");
        SimpleFeature feature = testData.getFeature(typeName, "verblijfsobject.5880559");
        assertNotNull(feature);
        assertEquals(new BigDecimal(344010000076262L),feature.getAttribute("identificatie"));
    }
}
