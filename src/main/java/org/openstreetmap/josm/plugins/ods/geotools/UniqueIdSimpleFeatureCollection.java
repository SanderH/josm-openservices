package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.collection.DecoratingSimpleFeatureCollection;
import org.geotools.feature.collection.DecoratingSimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.identity.FeatureId;

/**
 * Decorating feature collection that removes duplicate feature based on a
 * single attribute. The value of this attribute will be the id for the resulting
 * feature.
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class UniqueIdSimpleFeatureCollection extends DecoratingSimpleFeatureCollection {

    private int idAtributeIndex;

    public UniqueIdSimpleFeatureCollection(SimpleFeatureCollection delegate, int idAtributeIndex) {
        super(delegate);
    }

    @Override
    public SimpleFeatureIterator features() {
        return new UniqueIdSimpleFeatureIterator(super.features(), idAtributeIndex);
    }

    class UniqueIdSimpleFeatureIterator extends DecoratingSimpleFeatureIterator {
        private final int idAttributeIndex;
        private SimpleFeature currentFeature = null;
        private boolean hasNext = false;

        public UniqueIdSimpleFeatureIterator(SimpleFeatureIterator delegate, int idAttributeIndex) {
            super(delegate);
            this.idAttributeIndex = idAttributeIndex;
        }
        
        @Override
        public boolean hasNext() {
            if (currentFeature == null) {
                this.hasNext = super.hasNext();
                if (hasNext) {
                    currentFeature = super.next();
                }
            }
            return hasNext;
        }

        @Override
        public SimpleFeature next() throws NoSuchElementException {
            if (!hasNext) {
                throw  new NoSuchElementException();
            }
            if (!super.hasNext()) {
                hasNext = false;
                return cloneFeature(currentFeature, idAttributeIndex);
            }
            SimpleFeature nextFeature = super.next();
            while (hasSameKey(nextFeature)) {
                if (super.hasNext()) {
                    nextFeature = super.next();
                }
                else {
                    hasNext=false;
                    break;
                }
            }
            SimpleFeature result = currentFeature;
            if (hasNext()) {
                currentFeature = nextFeature;
            }
            return cloneFeature(result, idAttributeIndex);
        }

        /**
         * Clone a feature, using the attribute value at index idIndex as the new identifier.
         * 
         * @param feature
         * @param idIndex
         * @return
         */
        private SimpleFeature cloneFeature(SimpleFeature feature,
                int idIndex) {
            FeatureId id = new FeatureIdImpl(feature.getAttribute(idIndex).toString());
            return new SimpleFeatureImpl(feature.getAttributes(), 
                feature.getFeatureType(), id);
        }

        private boolean hasSameKey(SimpleFeature f) {
            return Objects.equals(f.getAttribute(idAttributeIndex), currentFeature.getAttribute(idAttributeIndex));
        }
    }
}
