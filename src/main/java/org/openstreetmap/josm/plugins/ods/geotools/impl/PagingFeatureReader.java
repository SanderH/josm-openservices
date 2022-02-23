package org.openstreetmap.josm.plugins.ods.geotools.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.util.ProgressListener;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureReader;
import org.openstreetmap.josm.plugins.ods.geotools.GtPageReader;
import org.openstreetmap.josm.tools.Logging;

public class PagingFeatureReader implements GtFeatureReader {
    private final static FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

    private final GtDataSource dataSource;
    private final Query baseQuery;
    private final int pageSize;

    public PagingFeatureReader(GtDataSource dataSource, Query query) {
        super();
        this.dataSource = dataSource;
        this.baseQuery = query;
        this.pageSize = dataSource.getPageSize();
    }

    @Override
    public void read(Consumer<SimpleFeature> consumer, ProgressListener progressListener) throws IOException {
        int index = 0;
        boolean ready = false;
        GtPageReader pageReader = new DefaultGtPageReader(dataSource.getOdsFeatureSource().getFeatureSource());
        while (!ready && !Thread.currentThread().isInterrupted()) {
            Query query = new Query(baseQuery);
            query.setStartIndex(index);
            query.setMaxFeatures(pageSize);
            // TODO move to dataSource
            SortBy sortBy = ff.sort("identificatie", SortOrder.ASCENDING);
            query.setSortBy(new SortBy[] {sortBy});
            
            Logging.info("Downloading feature: " + query.getTypeName() + " startIndex: " + index + " pageSize: " + pageSize + "...");
            
            // TODO run this in a separate thread
            Collection<SimpleFeature> features = pageReader.read(query, progressListener);
            features.forEach(consumer);
            
            Logging.info("Downloaded feature: " + query.getTypeName() + " features:" + features.size() + " total: " + (index + features.size()));
            
            index += pageSize;
            ready = features.size() < pageSize;
        }
    }
}
