package org.openstreetmap.josm.plugins.ods.io;

import java.net.URL;

import org.openstreetmap.josm.plugins.ods.OdsConfigurationException;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.ServiceException;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

/**
 * Host for 1 or more open data services.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface Host {

    /**
     * Get the name of this host. This is a descriptive name that has no direct
     * relation to any meta data.
     *
     * @return the name
     */
    String getName();

    /**
     * Get the URL of this host. Dependent of the service type, the URL may include a path
     * or even one or more query parameters.
     * Use
     *
     * @return
     */
    URL getUrl();

    /**
     * Get a string that describes the type of this host. The type name is given by
     * the implementor of the Host class and should be different for every host type.
     * The type name is used to distinct host classed of different types that have the same name.
     *
     * @return
     */
    String getType();

    /**
     * Get the default maximum number of features the services of this host will return for
     *     one request.
     * This value can be used to check if results have been truncated by the service.
     *
     * @return The maximum feature count or -1 if unlimited.
     */
    Integer getMaxFeatures();

    /**
     * @return Meta data for this host.
     */
    MetaData getMetaData();

    /**
     * Perform any initialization required for this host.
     * Typically, this included checking availability and building a list
     * of available features.
     *
     * Do nothing if the host has already been initialized.
     * @throws OdsConfigurationException
     */
    void initialize() throws OdsException;

    /**
     * @param feature The requested feature
     * @return true if this host provides a feature with the specified name.
     * @throws ServiceException
     */
    boolean hasFeatureType(String feature) throws ServiceException;

    /**
     * @param feature
     * @return An OdsFeatureSource object for the feature with the specified name
     * @throws ServiceException
     */
    OdsFeatureSource getOdsFeatureSource(String feature)
            throws ServiceException;

    /**
     * Create a feature downloader for passed data source that return results of type
     * <? extends class>
     * @param module The active module
     * @param dataSource
     * @param clazz
     * @return A @FeatureDownloader, or null if not available
     * @throws OdsException
     */
    <T extends EntityType> FeatureDownloader createDownloader(OdsModule module, OdsDataSource dataSource, T entityType) throws OdsException;
}