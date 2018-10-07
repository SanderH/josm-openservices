package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.concurrent.Callable;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;

/**
 * The FeatureExtractor handles the Extraction part of the ETL process.
 * It downloads the features from the data source based on a request, and caches the results.
 * On completion and/or abortion, the extractor signal the caller. The caller can then retrieve
 * the results.
 *
 * @author Gertjan Idema
 *
 */
public interface FeatureExtractor {
    Callable<SimpleFeatureCollection> getCallable(DownloadRequest request);

}
