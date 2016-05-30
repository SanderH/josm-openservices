package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.exceptions.OdsException;
import org.openstreetmap.josm.plugins.ods.properties.SimpleEntityMapper;

public interface EntityMapperFactory {

    SimpleEntityMapper<?, ?> create(OdsFeatureSource odsFeatureSource) throws OdsException;

}
