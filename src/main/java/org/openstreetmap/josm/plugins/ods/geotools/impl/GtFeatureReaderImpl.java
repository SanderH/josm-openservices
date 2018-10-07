package org.openstreetmap.josm.plugins.ods.geotools.impl;

import java.io.IOException;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.FeatureVisitor;
import org.opengis.util.ProgressListener;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureReader;
import org.openstreetmap.josm.plugins.ods.geotools.GtPageReader;

public class GtFeatureReaderImpl implements GtFeatureReader {
    private final GtDataSource dataSource;
    private final Query baseQuery;
    private final int pageSize;

    public GtFeatureReaderImpl(GtDataSource dataSource, Query query) {
        super();
        this.dataSource = dataSource;
        this.baseQuery = query;
        this.pageSize = dataSource.getPageSize();
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
        GtPageReader pageReader = new GtPageReaderImpl(dataSource.getOdsFeatureSource().getFeatureSource());
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
        GtPageReader pageReader = new GtPageReaderImpl(dataSource.getOdsFeatureSource().getFeatureSource());
        // TODO run this in a separate thread
        DefaultFeatureCollection features = pageReader.read(baseQuery, progressListener);
        allFeatures.addAll((SimpleFeatureCollection)features);
        allFeatures.accepts(consumer, null);
    }
}
