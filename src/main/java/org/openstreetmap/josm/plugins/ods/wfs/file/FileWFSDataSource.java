package org.openstreetmap.josm.plugins.ods.wfs.file;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.entities.EntityMapperFactory;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class FileWFSDataSource implements OdsDataSource {
    private final OdsFeatureSource featureSource;
    private final Query query;

    public FileWFSDataSource(OdsFeatureSource featureSource, Query query,
            EntityMapperFactory factory) {
        this.featureSource = featureSource;
        this.query = query;
    }

    @Override
    public void initialize() throws OdsException {
        // TODO Auto-generated method stub
    }

    @Override
    public String getFeatureType() {
        return featureSource.getFeatureType().getName().getLocalPart();
    }

    @Override
    public OdsFeatureSource getOdsFeatureSource() {
        return featureSource;
    }

    @Override
    public Query getQuery(DownloadRequest request) {
        return query;
    }

    @Override
    public MetaData getMetaData() {
        return new MetaData();
    }
}
