package org.openstreetmap.josm.plugins.ods.shp;

import java.io.IOException;

import org.geotools.data.DataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;

/**
 * Class to represent a shp file odsFeatureSource host.
 * Still work in progress.
 * 
 * @author Gertjan Idema
 * 
 */
public class ShapefileHost extends GtHost {
    private DataStore dataStore;
    
    public ShapefileHost(String name, String urlString) {
        super(name, urlString, -1);
    }

    
    @Override
    public DataStore getDataStore() throws OdsException {
        return dataStore;
    }

    @Override
    public synchronized void initialize() throws OdsException {
        if (isInitialized()) return;
        super.initialize();
        try {
            createDataStore();
            setInitialized(true);
        }
        catch (OdsException e) {
            setInitialized(false);
            throw e;
        }
    }


    /**
     * Retrieve a new DataStore for this host with the default timeout
     * 
     * @return the DataStore object
     * @throws OdsException 
     */
    public void createDataStore() throws OdsException {
        ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
        try {
            dataStore = factory.createDataStore(getUrl());
        } catch (IOException e) {
            throw new OdsException(e);
        }
    }
}
