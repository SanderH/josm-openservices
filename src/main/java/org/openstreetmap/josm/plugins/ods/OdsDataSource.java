package org.openstreetmap.josm.plugins.ods;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

/**
 * <p>An OdsDataSource is the interface between the OdsModule and the
 * OdsFeatureSource. It performs the following tasks.</p>
 * <ul>
 * <li>Maintain a filter used when downloading features</li>
 * <li>Create a unique id for each downloaded feature</li>
 * <li>Maintain a list of downloaded feature to prevent duplicates</li>
 *
 * @author Gertjan Idema
 *
 */
public interface OdsDataSource {
    public void initialize() throws OdsException;

    public String getFeatureType();

    public OdsFeatureSource getOdsFeatureSource();

    public Query getQuery(DownloadRequest request);

    public MetaData getMetaData();

    /**
     * Get the page size if this data source is configured for paging requests.
     * @return The page size or 0 if paging has not been configured.
     */
    public default int getPageSize() {
        return 0;
    }

}
