package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;

public class LongAttributeMapper implements AttributeMapper<Long> {
    private boolean required;
    private String attributeName;
    
    public LongAttributeMapper(boolean required, String attributeName) {
        super();
        this.required = required;
        this.attributeName = attributeName;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public Long get(SimpleFeature feature) {
        return FeatureUtil.getLong(feature, attributeName);
    }
    
}
