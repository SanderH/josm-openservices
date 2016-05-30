package org.openstreetmap.josm.plugins.ods.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.io.OsmReader;

/**
 * This class provides utility methods to load a DataSet with test data.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class TestDataLoader {
    private static Map<URL, DataSet> cache = new HashMap<>();
    
    public static OsmDataLayer loadTestData(Class<?> clazz, String name) throws IOException, IllegalDataException {
        URL url = clazz.getResource(name);
        DataSet dataSet = loadTestData(url);
        File file = new File(url.getFile());
        return new OsmDataLayer(dataSet, "test", file);
    }

//    public static OsmDataLayer loadTestData(String path) throws IOException, IllegalDataException {
//        File file = new File(path);
//        URL url = file.toURI().toURL();
//        DataSet dataSet = loadTestData(url);
//        return new OsmDataLayer(dataSet, "test", file);
//    }
    
    private static DataSet loadTestData(URL url) {
        if (url == null) {
            Assert.fail("The file with test data could not be found");
        }
        DataSet dataSet = cache.get(url);
        if (dataSet != null) {
            return dataSet.clone();
        }
        try (InputStream stream = url.openStream()) {
            dataSet = OsmReader.parseDataSet(stream, null);
            dataSet.setUploadDiscouraged(true);
            cache.put(url,  dataSet);
            return dataSet;
        } catch (IOException e) {
            Assert.fail("The file with test data could not be read.");
            return null;
        } catch (IllegalDataException e) {
            Assert.fail("The file with test data is not a valid OSM file.");
            return null;
        }
    }
}
