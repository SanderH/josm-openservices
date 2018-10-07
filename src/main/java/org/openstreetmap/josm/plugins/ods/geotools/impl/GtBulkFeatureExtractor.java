package org.openstreetmap.josm.plugins.ods.geotools.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;

public class GtBulkFeatureExtractor {
    private final long timeout = 30;
    private ExecutorService executor;
    private final List<GtFeatureExtractor> extractors;
    private List<Future<SimpleFeatureCollection>> tasks;

    public GtBulkFeatureExtractor(Collection<GtDataSource> dataSources) {
        super();
        this.extractors = new ArrayList<>(dataSources.size());
        dataSources.forEach(ds -> {
            extractors.add(new GtFeatureExtractor(ds));
        });
    }

    public void run(DownloadRequest request) {
        tasks = new ArrayList<>(extractors.size());
        extractors.forEach(extractor -> {
            Callable<SimpleFeatureCollection> callable = extractor.getCallable(request);
            tasks.add(executor.submit(callable));
        });
        executor.shutdown();
        try {
            executor.awaitTermination(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }

    public void cancel() {
        executor.shutdownNow();
    }
}
