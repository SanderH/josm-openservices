package org.openstreetmap.josm.plugins.ods.test.file.wfs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.wfs.internal.WFSStrategy;
import org.geotools.data.wfs.internal.v2_0.StrictWFS_2_0_Strategy;
import org.openstreetmap.josm.plugins.ods.wfs.file.FileWFSDataStore;

/**
 * This class provides utility methods to load a DataSet with test data.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class TestDataLoader {
    private static WFSStrategy strategy= new StrictWFS_2_0_Strategy();
    private static Map<File, TestData> cache = new HashMap<>();
    
    public static TestData loadTestData(File dir, String[] features) throws IOException {
        TestData testData = cache.get(dir);
        if (testData == null) {
            FileWFSDataStore dataStore = new FileWFSDataStore(strategy, dir);
            testData = new TestData(dataStore, features);
        }
        return testData;
    }
}
