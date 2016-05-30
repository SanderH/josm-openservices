package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;

public class StringAttributeMapper implements AttributeMapper<String> {
    private boolean required;
    private String featureName;
    
    public StringAttributeMapper(boolean required, String featureName) {
        super();
        this.required = required;
        this.featureName = featureName;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public String get(SimpleFeature feature) {
        return FeatureUtil.getString(feature, featureName);
    }
    
}
