package org.openstreetmap.josm.plugins.ods.wfs.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.geotools.data.wfs.v1_1_0.DefaultWFSStrategy;
import org.geotools.data.wfs.v1_1_0.WFSStrategy;
//import org.geotools.data.wfs.internal.WFSStrategy;
//import org.geotools.data.wfs.internal.v2_0.StrictWFS_2_0_Strategy;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;

public class TestFileWFSHost {
    private final static String BAG_URI = "http://bag.geonovum.nl";
    private static FileWFSHost host;
    
    @BeforeClass
    public static void beforeClass() throws IOException {
        Logger.getGlobal().setLevel(Level.FINEST);
//        WFSStrategy strategy = new StrictWFS_2_0_Strategy();
        WFSStrategy strategy = new DefaultWFSStrategy();
        File testDir = new File(TestFileWFSHost.class.getResource("inktpot_1_1_0").getPath());
        host = new FileWFSHost(strategy, testDir);
    }
    
    @Test
    public void testFirstFeatureType() throws IOException, CRSException {
        SimpleFeatureType featureType = host.getFeatureType(new QName(BAG_URI, "pand"));
        assertEquals(9, featureType.getAttributeCount());
    }
}
