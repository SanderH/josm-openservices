package org.openstreetmap.josm.plugins.ods.wfs.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.v1_1_0.DefaultWFSStrategy;
import org.geotools.data.wfs.v1_1_0.WFSStrategy;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

public class TestFileWFSSimpleFeatureSource {
    private static WFSStrategy strategy = new DefaultWFSStrategy();

    @Test
    public void test() throws IOException {
        File dir = new File(getClass().getResource("inktpot_1_1_0").getFile());
        FileWFSDataStore dataStore = new FileWFSDataStore(strategy, dir);
        SimpleFeatureSource featureSource = dataStore.getFeatureSource("verblijfsobject");
        try (
            SimpleFeatureIterator iterator = featureSource.getFeatures().features();
        ) {
            assertTrue(iterator.hasNext());
            SimpleFeature feature = iterator.next();
            assertEquals("3511EV", feature.getAttribute("postcode"));
        }
    }
}
