package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;

import org.opengis.feature.FeatureVisitor;
import org.opengis.util.ProgressListener;

/**
 * Geotools feature reader that supports paging.
 * Paging means that the complete results may be retrieved in several smaller
 * batches (pages), to improve performance.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface GtFeatureReader {
    void read(FeatureVisitor consumer, ProgressListener progressListener)
            throws IOException;
}
