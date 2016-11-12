package org.openstreetmap.josm.plugins.ods.wfs.file;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.internal.v2_0.StrictWFS_2_0_Strategy;
import org.junit.Test;

public class TestFileWFSDataStore {
    private static org.geotools.data.wfs.internal.WFSStrategy strategy = new StrictWFS_2_0_Strategy();

    @Test
    public void test() throws IOException {
        File dir = new File(getClass().getResource("inktpot_1_1_0").getFile());
        FileWFSDataStore dataStore = new FileWFSDataStore(strategy, dir);
        SimpleFeatureSource featureSource = dataStore.getFeatureSource("verblijfsobject");
        assertNotNull(featureSource);
    }
}
