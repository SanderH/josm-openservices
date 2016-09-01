package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.Iterator;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

public class GroupByFeatureCollection extends OdsFeatureCollection {
    private final SimpleFeatureCollection wrapped;
    private final GroupByQuery query;

    public GroupByFeatureCollection(SimpleFeatureCollection wrapped, GroupByQuery query) {
        super(wrapped);
        this.wrapped = wrapped;
        this.query = query;
    }

    @Override
    protected OdsFeatureIterator getFeatureIterator(SimpleFeatureIterator wrappedFeatures) {
        return new GroupByFeatureIterator(wrapped.features(), query);
    }
    
    
    @Override
    public int size() {
        // TODO This result is not accurate, because the duplicate features are counted;
        // However, the main use of the size is to determine if the result has been truncated
        // to the maximum number of features. For this purpose the size of the wrapped iterator
        // is what we need.
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return wrapped.getBounds();
    }

    class SimpleFeatureIteratorIterator implements Iterator<SimpleFeature>, SimpleFeatureIterator {
        
        SimpleFeatureIterator fi;
        
        public SimpleFeatureIteratorIterator(SimpleFeatureIterator fi) {
            super();
            this.fi = fi;
        }

        @Override
        public boolean hasNext() {
            return fi.hasNext();
        }

        @Override
        public SimpleFeature next() {
            return fi.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void close() {
            fi.close();
        }
    }
}
