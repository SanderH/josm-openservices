package org.openstreetmap.josm.plugins.ods.geotools;

import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;

import org.geotools.data.DataAccess;
import org.geotools.data.FeatureListener;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

/**
 * SimpleFeatureSource to be used as a base implementation to wrap around a delegate
 * SimpleFeatureSource;
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class DecoratingSimpleFeatureSource implements SimpleFeatureSource {
    private final SimpleFeatureSource delegate;
    private final LinkedList<FeatureListener> listeners = new LinkedList<>();
    
    public DecoratingSimpleFeatureSource(SimpleFeatureSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public Name getName() {
        return delegate.getName();
    }

    @Override
    public ResourceInfo getInfo() {
        return delegate.getInfo();
    }

    @Override
    public DataAccess<SimpleFeatureType, SimpleFeature> getDataStore() {
        return delegate.getDataStore();
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return delegate.getQueryCapabilities();
    }

    @Override
    public void addFeatureListener(FeatureListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeFeatureListener(FeatureListener listener) {
        listeners.remove(listener);
    }

    @Override
    public SimpleFeatureType getSchema() {
        return delegate.getSchema();
    }

    @Override
    public ReferencedEnvelope getBounds() throws IOException {
        return delegate.getBounds();
    }

    @Override
    public ReferencedEnvelope getBounds(Query q) throws IOException {
        return delegate.getBounds();
    }

    @Override
    public int getCount(Query q) throws IOException {
        return delegate.getCount(q);
    }

    @Override
    public Set<Key> getSupportedHints() {
        return delegate.getSupportedHints();
    }


    @Override
    public SimpleFeatureCollection getFeatures() throws IOException {
        return delegate.getFeatures();
    }

    @Override
    public SimpleFeatureCollection getFeatures(Filter filter) throws IOException {
        return delegate.getFeatures(filter);
    }

    @Override
    public SimpleFeatureCollection getFeatures(Query query) throws IOException {
        return delegate.getFeatures(query);
    }

}
