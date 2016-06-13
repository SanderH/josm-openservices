package org.openstreetmap.josm.plugins.ods.wfs.file;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.DefaultOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.entities.EntityMapperFactory;

public class FileWFSDataSource extends DefaultOdsDataSource {

    public FileWFSDataSource(OdsFeatureSource odsFeatureSource, Query query,
            EntityMapperFactory factory) {
        super(odsFeatureSource, query, factory);
    }

}
