package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.geotools.data.DataStore;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.io.AbstractHost;

/**
 * Class to represent a Geotools host.
 * 
 * @author Gertjan Idema
 * 
 */
public abstract class GtHost extends AbstractHost {
    private Set<String> featureTypes = new HashSet<>();

    public GtHost(String name, String url, Integer maxFeatures) {
        super(name, url, maxFeatures);
    }

    synchronized protected void setFeatureTypes(Collection<String> featureTypes) {
        this.featureTypes.clear();
        this.featureTypes.addAll(featureTypes);
    }

    @Override
    public boolean hasFeatureType(String type) {
        return featureTypes.contains(type);
    }

//    public SimpleFeatureSource getFeatureSource(String name, int timeout) {
//        DataStore dataStore = getDataStore(timeout);
//        if (dataStore == null) {
//            return null;
//        }
//        dataStore.getFeatureSource(name);
//    }
    
    @Override
    public OdsFeatureSource getOdsFeatureSource(String feature) {
        return new GtFeatureSource(this, feature, null);
    }

    @Override
    public <T extends Entity> FeatureDownloader createDownloader(
            OdsModule module, OdsDataSource dataSource, Class<T> clazz) throws OdsException {
        return new GtDownloader<>(module, dataSource, clazz);
    }

    public abstract DataStore getDataStore() throws OdsException;
}
