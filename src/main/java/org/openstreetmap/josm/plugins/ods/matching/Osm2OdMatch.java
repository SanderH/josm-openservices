package org.openstreetmap.josm.plugins.ods.matching;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public interface Osm2OdMatch<T extends EntityType> {
    public OsmEntity<T> getOsmEntity();

    public void clearDifferences();

    public boolean hasDifferences();

    public StatusDifference getStatusDifference();

    public void setStatusDifference(StatusDifference statusDifference);

    public GeometryDifference getGeometryDifference();

    public void setGeometryDifference(GeometryDifference geometryDifference);

    public List<TagDifference> getAttributeDifferences();

    public void addAttributeDifference(TagDifference difference);
}
