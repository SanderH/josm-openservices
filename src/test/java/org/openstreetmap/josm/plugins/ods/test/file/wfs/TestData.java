package org.openstreetmap.josm.plugins.ods.test.file.wfs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;

public class TestData {
    private final Map<Name, Map<String, SimpleFeature>> features = new HashMap<>();
    private final DataStore dataStore;

    public TestData(DataStore dataStore, String[] typeNames) throws IOException {
        this.dataStore = dataStore;
        for (String typeName : typeNames) {
            SimpleFeatureSource fs = dataStore.getFeatureSource(typeName);
            SimpleFeatureCollection featureCollection = fs.getFeatures();
            addAll(featureCollection);
        }
    }
    
    public DataStore getDataStore() {
        return dataStore;
    }

    public SimpleFeature getFeature(Name typeName, String id) {
        Map<String, SimpleFeature> featureMap = features.get(typeName);
        if (featureMap == null) {
            return null;
        }
        return featureMap.get(id);
    }
    
    public void addFeature(SimpleFeature feature) {
        Name typeName = feature.getFeatureType().getName();
        Map<String, SimpleFeature> featureMap = features.get(typeName);
        if (featureMap == null) {
            featureMap = new HashMap<>();
            features.put(typeName, featureMap);
        }
        featureMap.put(feature.getID(), feature);
    }
    
    public void addAll(SimpleFeatureCollection featureCollection) {
        Name typeName = featureCollection.getSchema().getName();
        Map<String, SimpleFeature> featureMap = features.get(typeName);
        if (featureMap == null) {
            featureMap = new HashMap<>();
            features.put(typeName, featureMap);
        }
        try (
            SimpleFeatureIterator it = featureCollection.features();
        )  {
            while (it.hasNext()) {
                SimpleFeature feature = it.next();
                featureMap.put(feature.getID(), feature);
            }
        }
        finally {};
    }
}
