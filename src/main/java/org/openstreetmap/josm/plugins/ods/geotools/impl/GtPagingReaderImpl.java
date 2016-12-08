package org.openstreetmap.josm.plugins.ods.geotools.impl;

import java.io.IOException;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.FeatureVisitor;
import org.opengis.util.ProgressListener;
import org.openstreetmap.josm.plugins.ods.geotools.GtPageReader;
import org.openstreetmap.josm.plugins.ods.geotools.GtPagingReader;

public class GtPagingReaderImpl implements GtPagingReader {
    private Query baseQuery;
    private SimpleFeatureSource featureSource;
    private int pageSize;
    
    public GtPagingReaderImpl(SimpleFeatureSource featureSource, Query query, int pageSize) {
        super();
        this.featureSource = featureSource;
        this.baseQuery = new Query(query);
        this.baseQuery.setMaxFeatures(pageSize);
        this.pageSize = pageSize;
    }

    @Override
    public void read(FeatureVisitor consumer, ProgressListener progressListener) throws IOException {
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
}
