package org.openstreetmap.josm.plugins.ods.geotools;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.AbstractOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;

public class GtDataSource extends AbstractOdsDataSource {

    public GtDataSource(OdsFeatureSource odsFeatureSource, Query query) {
        super(odsFeatureSource, query, null);
    }
}
