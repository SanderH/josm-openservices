package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

public interface Od2OsmMatch<T extends EntityType> {

    public OdEntity<T> getOpenDataEntity();
}
