package org.openstreetmap.josm.plugins.ods.geotools.impl;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.geotools.FeatureExtractor;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureReader;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;

public class GtFeatureExtractor implements FeatureExtractor {
    GtDataSource dataSource;

    public GtFeatureExtractor(GtDataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    @Override
    public Callable<SimpleFeatureCollection> getCallable(DownloadRequest request) {
        return new ExtractTask(request);
    }

    class ExtractTask implements Callable<SimpleFeatureCollection> {
        private final DownloadRequest request;

        public ExtractTask(DownloadRequest request) {
            super();
            this.request = request;
        }

        @Override
        public SimpleFeatureCollection call() {
            DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
            GtFeatureReader reader = new GtFeatureReaderImpl(dataSource, dataSource.getQuery(request));
            FeatureVisitor consumer = new FeatureVisitor() {
                @Override
                public void visit(Feature feature) {
                    featureCollection.add((SimpleFeature) feature);
                }
            };
            try {
                reader.read(consumer, null);
            }
            catch (IOException e) {
                featureCollection.clear();
                e.printStackTrace(System.err);
            }
            return featureCollection;
        }
    }
}
