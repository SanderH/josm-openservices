package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;

import org.geotools.data.Query;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.util.ProgressListener;

/**
 * Read a single page (batch) of features from a Geotools datasource.
 * 
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface GtPageReader {
    /**
     * Read the features and return them in a SimpleFeatureCollection
     * 
     * @param query The query for the next page
     * @param progressListener May be null
     * @return
     *     The SimpleFeatureCollection containing the downloaded features.
     *     The collection may be empty.
     * @throws IOException
     */
    DefaultFeatureCollection read(Query query, ProgressListener progressListener) throws IOException;
}
