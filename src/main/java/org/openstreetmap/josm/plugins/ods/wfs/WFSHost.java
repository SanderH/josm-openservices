package org.openstreetmap.josm.plugins.ods.wfs;

import static org.geotools.data.wfs.impl.WFSDataAccessFactory.BUFFER_SIZE;
import static org.geotools.data.wfs.impl.WFSDataAccessFactory.MAXFEATURES;
import static org.geotools.data.wfs.impl.WFSDataAccessFactory.PROTOCOL;
import static org.geotools.data.wfs.impl.WFSDataAccessFactory.TIMEOUT;
import static org.geotools.data.wfs.impl.WFSDataAccessFactory.URL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.data.wfs.WFSServiceInfo;
import org.geotools.data.wfs.internal.WFSGetCapabilities;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.exceptions.UnavailableHostException;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;
import org.openstreetmap.josm.tools.I18n;

import net.opengis.ows11.CapabilitiesBaseType;
import net.opengis.ows11.DomainType;
import net.opengis.ows11.OperationType;
import net.opengis.ows11.OperationsMetadataType;
import net.opengis.ows11.ValueType;

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
    private WFSDataStore dataStore;
    private final int initTimeout;
    private final int dataTimeout;
    private boolean supportsPaging = false;
    
    public WFSHost(String name, String urlString, Integer maxFeatures, int initTimeout, int dataTimeout) {
        super(name, urlString, maxFeatures);
        this.initTimeout = initTimeout;
        this.dataTimeout = dataTimeout;
    }
    
    public boolean isSupportsPaging() {
        return supportsPaging;
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
    private WFSDataStore createDataStore(Integer timeout) throws OdsException {
        Map<String, Object> connectionParameters = new HashMap<>();
        URL capabilitiesUrl = WFSDataStoreFactory
                .createGetCapabilitiesRequest(getUrl());
        connectionParameters.put(URL.key, capabilitiesUrl);
        if (timeout > 0) {
            connectionParameters.put(TIMEOUT.key, timeout);
        }
        connectionParameters.put(BUFFER_SIZE.key, 1000);
        connectionParameters.put(PROTOCOL.key, true);
        connectionParameters.put(MAXFEATURES.key, super.getMaxFeatures());
        WFSDataStore ds;
        try {
            ds = (WFSDataStore) DataStoreFinder.getDataStore(connectionParameters);
            if (ds == null) {
                throw new OdsException("No data store could be found");
            }
            WFSServiceInfo serviceInfo = ds.getInfo();
            switch (serviceInfo.getVersion()) {
            case "1.0.0":
            case "1.1.0":
                break;
            case "2.0.0":
                WFSGetCapabilities wfsCapabilities = ds.getWfsClient().getCapabilities();
                processWFSCapabilities(wfsCapabilities);
            }
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
    
    private void processWFSCapabilities(WFSGetCapabilities wfsCapabilities) {
        CapabilitiesBaseType capabilitiesType = (CapabilitiesBaseType) wfsCapabilities.getParsedCapabilities();
        OperationsMetadataType metaData = capabilitiesType.getOperationsMetadata();
        processConstraints(metaData.getConstraint());
        processOperations(metaData.getOperation());
    }

    private void processConstraints(EList<?> constraints) {
        @SuppressWarnings("unchecked")
        Iterator<DomainType> it = (Iterator<DomainType>) constraints.iterator();
        while (it.hasNext()) {
            DomainType domainType = it.next();
            ValueType valueType = domainType.getDefaultValue();
            String sDefault = (valueType == null ? null :valueType.getValue());
            switch (domainType.getName()) {
            case "ImplementsResultPaging":
                supportsPaging = Boolean.parseBoolean(sDefault); 
                break;
            }
        }
    }

    private void processOperations(EList<?> operations) {
        @SuppressWarnings("unchecked")
        Iterator<OperationType> it = (Iterator<OperationType>) operations.iterator();
        while (it.hasNext()) {
            OperationType operation = it.next();
            switch (operation.getName()) {
            case "GetFeature":
                processGetFeatureConstraints(operation.getConstraint());
                break;
            }
        }
    }

    private void processGetFeatureConstraints(EList<?> constraints) {
        @SuppressWarnings("unchecked")
        Iterator<DomainType> it = (Iterator<DomainType>) constraints.iterator();
        while (it.hasNext()) {
            DomainType constraint = it.next();
            switch (constraint.getName()) {
            case "CountDefault":
                String sValue = constraint.getDefaultValue().getValue();
//                this.setMaxFeatures(Integer.parseInt(sValue));
            }
        }
    }
}
