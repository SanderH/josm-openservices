package org.openstreetmap.josm.plugins.ods.wfs.file;

import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.geotools.data.DataAccess;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

public class FileWFSSimpleFeatureSource implements SimpleFeatureSource {
    private final FileWFSDataStore dataStore;
    private final SimpleFeatureType featureType;
    private QueryCapabilities queryCapabilities;
    private final Set<FeatureListener> featureListeners = new HashSet<>();
    private ReferencedEnvelope bounds;

    public FileWFSSimpleFeatureSource(FileWFSDataStore dataStore,
            SimpleFeatureType featureType) {
        this.dataStore = dataStore;
        this.featureType = featureType;
    }

    @Override
    public Name getName() {
        return featureType.getName();
    }

    @Override
    public ResourceInfo getInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataAccess<SimpleFeatureType, SimpleFeature> getDataStore() {
        return dataStore;
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        if (queryCapabilities == null) {
            queryCapabilities = new QueryCapabilities();
        }
        return queryCapabilities;
    }

    @Override
    public void addFeatureListener(FeatureListener listener) {
        featureListeners.add(listener);
    }

    @Override
    public void removeFeatureListener(FeatureListener listener) {
        featureListeners.remove(listener);
    }

    @Override
    public SimpleFeatureType getSchema() {
        return featureType;
    }

    @Override
    public ReferencedEnvelope getBounds() throws IOException {
        return bounds;
    }

    @Override
    public ReferencedEnvelope getBounds(Query query) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getCount(Query query) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Key> getSupportedHints() {
        return Collections.emptySet();
    }

    @Override
    public SimpleFeatureCollection getFeatures() throws IOException {
        // TODO use proper iterator instead of Memory collection
        MemoryFeatureCollection featureCollection = new MemoryFeatureCollection(getSchema());
        try (
            FeatureReader<SimpleFeatureType, SimpleFeature> featureReader = new FileWFSSimpleFeatureReader(dataStore, featureType);
        ) {
            while (featureReader.hasNext()) {
                featureCollection.add(featureReader.next());
            }
        }
        return featureCollection;
    }

    @Override
    public SimpleFeatureCollection getFeatures(Filter filter)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SimpleFeatureCollection getFeatures(Query query) throws IOException {
        throw new UnsupportedOperationException();
    }
}
