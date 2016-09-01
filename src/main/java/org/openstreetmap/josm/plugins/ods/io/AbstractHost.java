package org.openstreetmap.josm.plugins.ods.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.OdsConfigurationException;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataLoader;
import org.openstreetmap.josm.tools.I18n;

/**
 * Abstract implementation of the @Host class that provides basic functionality.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public abstract class AbstractHost implements Host {
    private boolean initialized = false;
    private String name;
    private String type;
    private String uncheckedUrl;
    private URL url;
    private Integer maxFeatures;
    private MetaData metaData;
    /**
     * A list of metaData downloaders that can retrieve extra meta data about the host. Mainly intended to process OWS meta data in the future,
     * but currently not actively used. 
     */
    private final List<MetaDataLoader> metaDataLoaders = new LinkedList<>();

    public AbstractHost(String name, String url) {
        this(name, url, -1);
    }

    public AbstractHost(String name, String url, Integer maxFeatures) {
        super();
        this.name = name;
        this.uncheckedUrl = url;
        this.maxFeatures = maxFeatures;
    }

    public boolean isInitialized() {
        return initialized;
    }
    
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
    
    /**
     * @throws OdsConfigurationException 
     * @see org.openstreetmap.josm.plugins.ods.io.Host#initialize()
     */
    @Override
    public synchronized void initialize() throws OdsException {
        if (isInitialized()) return;
        try {
            url = new URL(uncheckedUrl);
        } catch (MalformedURLException e) {
            setInitialized(false);
            String msg = I18n.tr("Invalid url: {0}", uncheckedUrl);
            throw new OdsException(msg);
        }
        metaData = new MetaData();
        for (MetaDataLoader loader : metaDataLoaders) {
            try {
                loader.populateMetaData(metaData);
            } catch (MetaDataException e) {
                setInitialized(false);
                throw new OdsException("Invalid meta data", e);
            }
        }
        setInitialized(true);
        return;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#getName()
     */
    @Override
    public final String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#getUrl()
     */
    @Override
    public final URL getUrl() {
        return url;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#getType()
     */
    @Override
    public final String getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#getMaxFeatures()
     */
    @Override
    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    public void setMaxFeatures(Integer maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    public void addMetaDataLoader(MetaDataLoader metaDataLoader) {
        metaDataLoaders.add(metaDataLoader);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#getMetaData()
     */
    @Override
    public MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    public boolean equals(Host other) {
        return other.getName().equals(name) && other.getType().equals(type)
                && other.getUrl().equals(url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getType(), getUrl());
    }
}
