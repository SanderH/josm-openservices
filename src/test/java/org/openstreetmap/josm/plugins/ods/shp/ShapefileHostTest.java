package org.openstreetmap.josm.plugins.ods.shp;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.Test;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;

public class ShapefileHostTest {

    @Test
    public void test() throws OdsException {
        URL url = getClass().getResource("shapefiles/pand.shp");

        ShapefileHost host = new ShapefileHost("test", url.toString());
        host.initialize();
        assertNotNull("The test resource must exist", host);
    }
}
