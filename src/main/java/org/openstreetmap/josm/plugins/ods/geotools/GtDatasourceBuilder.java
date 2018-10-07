package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;

public class GtDatasourceBuilder {
    private final CRSUtil crsUtil;
    private GtFeatureSource featureSource;
    private List<String> properties;
    private final List<FilterFactory> filters = new LinkedList<>();
    private int pageSize;

    public GtDatasourceBuilder(CRSUtil crsUtil) {
        super();
        this.crsUtil = crsUtil;
    }

    public GtDatasourceBuilder setFeatureSource(GtFeatureSource featureSource) {
        this.featureSource = featureSource;
        return this;
    }

    public GtDatasourceBuilder setProperties(String... properties) {
        this.properties = Arrays.asList(properties);
        return this;
    }

    public GtDatasourceBuilder setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public GtDataSource build() {
        return new GtDataSource(featureSource, crsUtil, properties, filters, pageSize);
    }
}