package org.openstreetmap.josm.plugins.ods.shp;

import java.io.IOException;

import org.geotools.data.DataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;

/**
 * Class to represent a shp file odsFeatureSource host.
 * 
 * @author Gertjan Idema
 * 
 */
public class ShapefileHost extends GtHost {
    private DataStore dataStore;
    
    public ShapefileHost(String name, String urlString) {
        super(name, urlString, -1);
    }

    /**
     * Retrieve a new DataStore for this host with the default timeout
     * 
     * @return the DataStore object
     * @throws OdsException 
     */
    public DataStore getDataStore() throws OdsException {
        return getDataStore(-1);
    }
    
    /**
     * Retrieve a new DataStore for this host with the given timeout
     * 
     * @param timeout A timeout in milliseconds
     * @return the DataStore object
     * @throws OdsException 
     */
    @Override
    public DataStore getDataStore(Integer timeout) throws OdsException {
        if (dataStore == null) {
            ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
            try {
                dataStore = factory.createDataStore(getUrl());
            } catch (IOException e) {
                throw new OdsException(e);
            }
        }
        return dataStore;
    }
}
