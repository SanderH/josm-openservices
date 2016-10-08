package org.openstreetmap.josm.plugins.ods.wfs;

import static org.geotools.data.wfs.WFSDataStoreFactory.BUFFER_SIZE;
import static org.geotools.data.wfs.WFSDataStoreFactory.PROTOCOL;
import static org.geotools.data.wfs.WFSDataStoreFactory.TIMEOUT;
import static org.geotools.data.wfs.WFSDataStoreFactory.URL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
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
import org.openstreetmap.josm.tools.I18n;

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
    private final int initTimeout;
    private final int dataTimeout;
    
    public WFSHost(String name, String urlString, Integer maxFeatures, int initTimeout, int dataTimeout) {
        super(name, urlString, maxFeatures);
        this.initTimeout = initTimeout;
        this.dataTimeout = dataTimeout;
    }
    
    @Override
    public synchronized void initialize() throws OdsException {
        if (!isInitialized()) {
            super.initialize();
            setInitialized(false);
            try {
                dataStore = createDataStore(initTimeout);
            } catch (OdsException e) {
                throw new UnavailableHostException(this, e);
            }
            try {
                dataStore = createDataStore(dataTimeout);
                setFeatureTypes(Arrays.asList(dataStore.getTypeNames()));
            } catch (OdsException|IOException e) {
                throw new UnavailableHostException(this, e);
            }
            setInitialized(true);
        }
        return;
    }

    /**
     * Retrieve a new DataStore for this host with the default timeout
     * 
     * @return the DataStore object
     * @throws OdsException 
     */
    @Override
    public DataStore getDataStore() throws OdsException {
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
        connectionParameters.put(PROTOCOL.key, true);
        DataStore ds;
        try {
            ds = DataStoreFinder.getDataStore(connectionParameters);
        } catch (UnknownHostException e) {
            String msg = I18n.tr("Host {0} ({1}) doesn't exist",
                    getName(), getUrl().getHost());
            OdsException hostException = new OdsException(msg);
            throw hostException;
        } catch (SocketException|SocketTimeoutException e) {
            String msg = I18n.tr("Host {0} ({1}) timed out when trying to open the datastore",
                    getName(), getUrl().toString());
            OdsException hostException = new OdsException(msg);
            throw hostException;
        } catch (FileNotFoundException e) {
            String msg = I18n.tr("No dataStore for Host {0} could be found at this url: {1}",
                    getName(), getUrl().toString());
            OdsException hostException = new OdsException(msg);
            throw hostException;
        } catch (IOException e) {
            String msg = I18n.tr("No dataStore for Host {0} ({1}) could be created",
                    getName(), getUrl().toString());
            OdsException hostException = new OdsException(msg);
            throw hostException;
        }
        return ds;
    }
}
