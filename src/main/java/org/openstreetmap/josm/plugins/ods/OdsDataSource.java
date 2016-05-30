package org.openstreetmap.josm.plugins.ods;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.properties.SimpleEntityMapper;

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

    public Query getQuery();

    public void setIdFactory(DefaultIdFactory idFactory);

    /**
     * Get an IdFactory that can extract a unique Id for the features
     * Retrieved from this dataSource.<br>
     * 
     * @return The IdFactory
     */
    public IdFactory getIdFactory();

    public MetaData getMetaData();

    public SimpleEntityMapper<?, ?> getEntityMapper();

    public boolean isRequired();
}
