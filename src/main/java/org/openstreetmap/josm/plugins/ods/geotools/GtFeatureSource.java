package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;
import java.time.LocalDate;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.tools.I18n;

public class GtFeatureSource implements OdsFeatureSource {
    private final boolean initialized = false;
    private boolean available = false;
    private final GtHost host;
    private final String featureName;
    private CoordinateReferenceSystem crs;
    private MetaData metaData;
    private SimpleFeatureSource featureSource;
    private FeatureType featureType;

    public GtFeatureSource(GtHost host, String featureName) {
        this.host = host;
        this.featureName = featureName;
    }

    @Override
    public final String getFeatureName() {
        return featureName;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    protected void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public GtHost getHost() {
        return host;
    }

    @Override
    public void initialize() throws OdsException {
        if (initialized) return;
        metaData = new MetaData(host.getMetaData());
        if (!getHost().hasFeatureType(featureName)) {
            String msg = I18n.tr("The feature named ''{0}'' is not known to host ''{1}''",
                    this.getFeatureName(),
                    getHost().getName());
            throw new OdsException(msg);
        }
        try {
            /*
             *  First use-a dataStore object with a short timeout, so it will fail
             *  fast if the service is not available;
             */
            DataStore dataStore = getHost().getDataStore();
            featureSource = dataStore.getFeatureSource(featureName);
            crs = featureSource.getInfo().getCRS();
            featureType = featureSource.getSchema();
        }
        catch (IOException e) {
            String msg = I18n.tr("The feature named ''{0}'' is not accessable, " +
                    "because of a network timeout on host {1}",
                    getFeatureName(),
                    getHost().getName());
            throw new OdsException(msg);
        }
        // TODO do we want these lines here? The source.date should be a response parameter
        if (!metaData.containsKey("source.date")) {
            metaData.put("source.date", LocalDate.now());
        }
        setAvailable(true);
    }

    @Override
    public MetaData getMetaData() {
        return metaData;
    }

    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }

    @Override
    public FeatureType getFeatureType() {
        assert isAvailable();
        return featureType;
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        assert isAvailable();
        return crs;
    }

    @Override
    public String getSRS() {
        assert isAvailable();
        ReferenceIdentifier rid = crs.getIdentifiers().iterator().next();
        return rid.toString();
    }

    @Override
    public Long getSRID() {
        assert isAvailable();
        ReferenceIdentifier rid = crs.getIdentifiers().iterator().next();
        return Long.parseLong(rid.getCode());
    }
}
