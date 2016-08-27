package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.openstreetmap.josm.plugins.ods.properties.EntityType;
import org.openstreetmap.josm.plugins.ods.properties.PropertyHandler;

public class SimpleFeatureEntityType implements EntityType<SimpleFeature> {
//    private final FeaturePropertyHandlerFactory propertyHandlerFactory = new FeaturePropertyHandlerFactory();
    private SimpleFeatureType featureType;
    
    public SimpleFeatureEntityType(SimpleFeatureType featureType) {
        super();
        this.featureType = featureType;
    }
    
    @Override
    public PropertyHandler<SimpleFeature, ?> createAttributeHandler(String attributeName) {
        if ("#ID".equals(attributeName)) {
            return new FeaturePropertyHandlerFactory.FeatureIDPropertyHandler();
        }
        AttributeDescriptor descriptor = featureType.getDescriptor(attributeName);
        if (descriptor == null) {
            return null;
        }
        return new FeaturePropertyHandlerFactory().createPropertyHandler(featureType, descriptor.getType().getBinding(), attributeName);
    }
 
}
