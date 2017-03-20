package org.openstreetmap.josm.plugins.ods.geotools.impl;

import java.io.IOException;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.FeatureVisitor;
import org.opengis.util.ProgressListener;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureReader;
import org.openstreetmap.josm.plugins.ods.geotools.GtPageReader;

public class GtFeatureReaderImpl implements GtFeatureReader {
    private Query baseQuery;
    private SimpleFeatureSource featureSource;
    private int pageSize;
    
    public GtFeatureReaderImpl(SimpleFeatureSource featureSource, Query query) {
        this(featureSource, query, -1);
    }
    
    public GtFeatureReaderImpl(SimpleFeatureSource featureSource, Query query, int pageSize) {
        super();
        this.featureSource = featureSource;
        this.baseQuery = new Query(query);
        if (pageSize > 0) {
            this.baseQuery.setMaxFeatures(pageSize);
        }
        this.pageSize = pageSize;
    }

    @Override
    public void read(FeatureVisitor consumer, ProgressListener progressListener) throws IOException {
        if (pageSize > 0) {
            readWithPaging(consumer, progressListener);
        }
        else {
            readWithoutPaging(consumer, progressListener);
        }
    }
    
    private void readWithPaging(FeatureVisitor consumer, ProgressListener progressListener) throws IOException {
        int pageCount = 0;
        boolean ready = false;
        DefaultFeatureCollection allFeatures = new DefaultFeatureCollection();
        GtPageReader pageReader = new GtPageReaderImpl(featureSource);
        while (!ready && !Thread.currentThread().isInterrupted()) {
            Query query = new Query(baseQuery);
            query.setStartIndex(pageCount * pageSize);
            // TODO run this in a separate thread
            DefaultFeatureCollection features = pageReader.read(query, progressListener);
            allFeatures.addAll((SimpleFeatureCollection)features);
            pageCount++;
            ready = features.size() < pageSize;
        }
        allFeatures.accepts(consumer, null);
    }
    
    private void readWithoutPaging(FeatureVisitor consumer, ProgressListener progressListener) throws IOException {
        DefaultFeatureCollection allFeatures = new DefaultFeatureCollection();
        GtPageReader pageReader = new GtPageReaderImpl(featureSource);
        Query query = new Query(baseQuery);
        // TODO run this in a separate thread
        DefaultFeatureCollection features = pageReader.read(query, progressListener);
        allFeatures.addAll((SimpleFeatureCollection)features);
        allFeatures.accepts(consumer, null);
    }
}
