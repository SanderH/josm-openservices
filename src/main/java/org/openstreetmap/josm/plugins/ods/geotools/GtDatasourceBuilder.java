package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.LinkedList;
import java.util.List;

import org.geotools.data.Query;
import org.opengis.filter.Filter;
import org.openstreetmap.josm.plugins.ods.entities.EntityMapperFactory;

public class GtDatasourceBuilder {
    private GtFeatureSource featureSource;
    private String[] properties;
    private String[] uniqueKey;
    private List<FilterFactory> filters = new LinkedList<>();
    private EntityMapperFactory entityMapperFactory;
    
    public GtDatasourceBuilder setFeatureSource(GtFeatureSource featureSource) {
        this.featureSource = featureSource;
        return this;
    }
    
    public GtDatasourceBuilder setProperties(String... properties) {
        this.properties = properties;
        return this;
    }

    public GtDatasourceBuilder setUniqueKey(String... uniqueKey) {
        this.uniqueKey = uniqueKey;
        return this;
    }
    
    public GtDatasourceBuilder setEntityMapperFactory(EntityMapperFactory entityMapperFactory) {
        this.entityMapperFactory = entityMapperFactory;
        return this;
    }

    public GtDataSource build() {
        Query query = createQuery();
        if (uniqueKey != null) {
            filters.add(new UniqueKeyFilterFactory(uniqueKey));
        }
        return new GtDataSource(featureSource, query, filters, entityMapperFactory);
    }
    
    private Query createQuery() {
        return new Query(featureSource.getFeatureName(), Filter.INCLUDE, properties);
    }
}