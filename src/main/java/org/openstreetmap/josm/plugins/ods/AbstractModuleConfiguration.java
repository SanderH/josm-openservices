package org.openstreetmap.josm.plugins.ods;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.io.Host;

public class AbstractModuleConfiguration implements OdsModuleConfiguration {

    private final Map<String, Host> hosts = new HashMap<>();
    private final List<OdsFeatureSource> featureSources = new LinkedList<>();
    private final Map<String, OdsDataSource> dataSources = new HashMap<>();
    
    protected void addHost(Host host) {
        hosts.put(host.getName(), host);
    }

    protected void addFeatureSource(OdsFeatureSource featureSource) {
        featureSources.add(featureSource);
        addHost(featureSource.getHost());
    }
    
    @Override
    public Collection<Host> getHosts() {
        return hosts.values();
    }

    @Override
    public List<? extends OdsFeatureSource> getFeatureSources() {
        return featureSources;
    }

    @Override
    public Collection<OdsDataSource> getDataSources() {
        return dataSources.values();
    }

    protected void addDataSource(OdsDataSource dataSource) {
        dataSources.put(dataSource.getFeatureType(), dataSource);
    }

    @Override
    public OdsDataSource getDataSource(String name) throws OdsException {
        OdsDataSource dataSource = dataSources.get(name);
        if (dataSource == null) {
            String msg = String.format("Unknown feature type: %s", name);
            throw new OdsException(msg);
        }
        return dataSource;
    }
}
