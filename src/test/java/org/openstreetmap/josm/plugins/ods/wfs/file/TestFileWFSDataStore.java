package org.openstreetmap.josm.plugins.ods.wfs.file;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.v1_1_0.DefaultWFSStrategy;
import org.geotools.data.wfs.v1_1_0.WFSStrategy;
import org.junit.Test;

public class TestFileWFSDataStore {
    private static WFSStrategy strategy = new DefaultWFSStrategy();

    @Test
    public void test() throws IOException {
        File dir = new File(getClass().getResource("inktpot_1_1_0").getFile());
        FileWFSDataStore dataStore = new FileWFSDataStore(strategy, dir);
        SimpleFeatureSource featureSource = dataStore.getFeatureSource("verblijfsobject");
        assertNotNull(featureSource);
    }
}
