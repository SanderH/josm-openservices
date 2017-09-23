package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.data.Query;
import org.opengis.feature.FeatureVisitor;
import org.openstreetmap.josm.plugins.ods.DefaultOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.entities.EntityMapperFactory;

public class GtDataSource extends DefaultOdsDataSource {
    private final List<FilterFactory> filters;

    public GtDataSource(OdsFeatureSource odsFeatureSource, Query query, EntityMapperFactory entityMapperFactory) {
        this(odsFeatureSource, query, new ArrayList<>(), entityMapperFactory);
    }

    public GtDataSource(OdsFeatureSource odsFeatureSource, Query query, List<FilterFactory> filters, EntityMapperFactory entityMapperFactory) {
        super(odsFeatureSource, query, entityMapperFactory);
        this.filters = filters;
    }

    /**
     * Build a FeatureVisitor that chains any filters in the right order.
     *
     * @param consumer
     * @return
     */
    public FeatureVisitor createVisitor(FeatureVisitor consumer) {
        if (filters.isEmpty()) {
            return consumer;
        }
        ArrayList<FilterFactory> filterFactories = new ArrayList<>(filters);
        Collections.reverse(filterFactories);
        FeatureVisitor result = consumer;
        for (FilterFactory factory : filterFactories) {
            FilteringFeatureVisitor visitor = factory.instance();
            visitor.setConsumer(result);
            result = visitor;
        }
        return result;
    }
}