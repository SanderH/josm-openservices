package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeatureType;
import org.openstreetmap.josm.plugins.ods.entities.EntityMapperFactory;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;

public abstract class GtEntityMapperFactory implements EntityMapperFactory {
    private GtHost host;
    private DataStore dataStore;
    
    public GtEntityMapperFactory(GtHost host) {
        super();
        this.host = host;
    }

    /**
     * Constructor for testing purposed only. Allows to pass a custom datastore.
     * @param dataStore
     */
    public GtEntityMapperFactory(DataStore dataStore) {
        this.dataStore = dataStore;
    }


    final protected DataStore getDataStore() throws OdsException {
        if (host != null) {
            return host.getDataStore(-1);
        }
        return dataStore;
    }
    
    protected SimpleFeatureType getFeatureType(String featureName) throws OdsException {
        try {
            SimpleFeatureSource featureSource = getDataStore().getFeatureSource(featureName);
            return featureSource.getSchema();
        } catch (IOException e) {
            throw new OdsException(String.format("Feature %s doesn't exist."), e);
        }
    }
//    
//    @Override
//    public SimpleEntityMapper<?, ?> create(OdsFeatureSource odsFeatureSource)
//            throws OdsException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
}
