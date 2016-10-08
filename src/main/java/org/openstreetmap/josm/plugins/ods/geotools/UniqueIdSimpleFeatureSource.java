package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

public class UniqueIdSimpleFeatureSource extends DecoratingSimpleFeatureSource {
    private final int idIndex;
    
    /**
     * TODO use factory to create the featureSource with proper error handling
     * @param newName
     * @param delegate
     * @param idAttributeName
     */
    public UniqueIdSimpleFeatureSource(Name newName, SimpleFeatureSource delegate, String idAttributeName) {
        super(delegate);
        idIndex = delegate.getSchema().indexOf(idAttributeName);
    }

    @Override
    public SimpleFeatureCollection getFeatures() throws IOException {
        SimpleFeatureCollection delegateFeatures = super.getFeatures();
        return new UniqueIdSimpleFeatureCollection(delegateFeatures, idIndex);
    }

    @Override
    public SimpleFeatureCollection getFeatures(Filter filter) throws IOException {
        SimpleFeatureCollection delegateFeatures = super.getFeatures(filter);
        return new UniqueIdSimpleFeatureCollection(delegateFeatures, idIndex);
    }

    @Override
    public SimpleFeatureCollection getFeatures(Query query) throws IOException {
        SimpleFeatureCollection delegateFeatures = super.getFeatures(query);
        return new UniqueIdSimpleFeatureCollection(delegateFeatures, idIndex);
    }

}
