package org.openstreetmap.josm.plugins.ods.wfs;

import static org.geotools.data.wfs.WFSDataStoreFactory.BUFFER_SIZE;
import static org.geotools.data.wfs.WFSDataStoreFactory.PROTOCOL;
import static org.geotools.data.wfs.WFSDataStoreFactory.TIMEOUT;
import static org.geotools.data.wfs.WFSDataStoreFactory.URL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.exceptions.UnavailableHostException;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;

/**
 * Class to represent a WFS odsFeatureSource host.
 * 
 * @author Gertjan Idema
 * 
 */
public class WFSHost extends GtHost {
    /**
     * We use two separate data stores. The reason for this is that currently Geotools stores the timeout as a parameter
     * in the data store.
     * A large time-out in the initialization phase would cause 
     */
    private DataStore dataStore;
    private OdsException hostException;
    private final int initTimeout;
    private final int dataTimeout;
    
    public WFSHost(String name, String urlString, Integer maxFeatures, int initTimeout, int dataTimeout) {
        super(name, urlString, maxFeatures);
        this.initTimeout = initTimeout;
        this.dataTimeout = dataTimeout;
    }
    
    @Override
    public synchronized void initialize() throws OdsException {
        if (isInitialized()) {
            return;
        }
        super.initialize();
        try {
            dataStore = createDataStore(initTimeout);
        } catch (OdsException e) {
            setAvailable(false);
            throw new UnavailableHostException(this, e);
        }
        try {
            dataStore = createDataStore(dataTimeout);
            setFeatureTypes(Arrays.asList(dataStore.getTypeNames()));
        } catch (OdsException|IOException e) {
            setAvailable(false);
            throw new UnavailableHostException(this, e);
        }
        setAvailable(true);
        return;
    }

//    @Override
//    public synchronized void initialize() throws OdsException {
//        if (isInitialized()) return;
//        super.initialize();
//    }

    /**
     * Retrieve a new DataStore for this host with the default timeout
     * 
     * @return the DataStore object
     * @throws OdsException 
     */
    @Override
    public DataStore getDataStore() throws OdsException {
        if (hostException != null) {
            throw hostException;
        }
        return dataStore;
    }
    
    /**
     * Create a new DataStore for this host with the given timeout
     * 
     * @param timeout A timeout in milliseconds
     * @return the DataStore object
     * @throws OdsException 
     */
    private DataStore createDataStore(Integer timeout) throws OdsException {
        Map<String, Object> connectionParameters = new HashMap<>();
        URL capabilitiesUrl = WFSDataStoreFactory
                .createGetCapabilitiesRequest(getUrl());
        connectionParameters.put(URL.key, capabilitiesUrl);
        if (timeout > 0) {
            connectionParameters.put(TIMEOUT.key, timeout);
        }
        connectionParameters.put(BUFFER_SIZE.key, 1000);
        connectionParameters.put(PROTOCOL.key, false);
        DataStore ds;
        try {
            ds = DataStoreFinder.getDataStore(connectionParameters);
        } catch (UnknownHostException e) {
            String msg = String.format("Host %s (%s) doesn't exist",
                    getName(), getUrl().getHost());
            hostException = new OdsException(msg);
            throw hostException;
        } catch (SocketTimeoutException e) {
            String msg = String.format("Host %s (%s) timed out when trying to open the datastore",
                    getName(), getUrl().toString());
            hostException = new OdsException(msg);
            throw hostException;
        } catch (FileNotFoundException e) {
            String msg = String.format("No dataStore for Host %s could be found at this url: %s",
                    getName(), getUrl().toString());
            hostException = new OdsException(msg);
            throw hostException;
        } catch (IOException e) {
            String msg = String.format("No dataStore for Host %s (%s) could be created",
                    getName(), getUrl().toString());
            hostException = new OdsException(msg);
            throw hostException;
        }
        return ds;
    }
}
