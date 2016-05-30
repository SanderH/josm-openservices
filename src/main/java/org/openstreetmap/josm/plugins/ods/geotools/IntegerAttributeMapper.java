package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;

public class IntegerAttributeMapper implements AttributeMapper<Integer> {
    private boolean required;
    private String attributeName;
    
    public IntegerAttributeMapper(boolean required, String attributeName) {
        super();
        this.required = required;
        this.attributeName = attributeName;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public Integer get(SimpleFeature feature) {
        return FeatureUtil.getInteger(feature, attributeName);
    }
}
