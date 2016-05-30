package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.feature.simple.SimpleFeature;

public class IdAttributeMapper implements AttributeMapper<String> {

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public String get(SimpleFeature feature) {
        return feature.getID();
    }
}
