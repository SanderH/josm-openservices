package org.openstreetmap.josm.plugins.ods.wfs.file;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.DefaultOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.properties.SimpleEntityMapper;

public class FileWFSDataSource extends DefaultOdsDataSource {

    public FileWFSDataSource(OdsFeatureSource odsFeatureSource, Query query,
            SimpleEntityMapper entityMapper) {
        super(odsFeatureSource, query, entityMapper);
    }

}
