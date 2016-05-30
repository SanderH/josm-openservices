package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;

public class CharacterAttributeMapper implements AttributeMapper<Character> {
    private boolean required;
    private String featureName;
    
    public CharacterAttributeMapper(boolean required, String featureName) {
        super();
        this.required = required;
        this.featureName = featureName;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public Character get(SimpleFeature feature) {
        String value = FeatureUtil.getString(feature, featureName);
        if (value == null || value.length() == 0) return null;
        return value.charAt(0);
    }
}
