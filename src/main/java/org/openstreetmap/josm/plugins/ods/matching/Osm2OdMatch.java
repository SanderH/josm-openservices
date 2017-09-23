package org.openstreetmap.josm.plugins.ods.matching;

import java.util.List;

public interface Osm2OdMatch {

    public void clearDifferences();

    public boolean hasDifferences();

    public StatusDifference getStatusDifference();

    public void setStatusDifference(StatusDifference statusDifference);

    public GeometryDifference getGeometryDifference();

    public void setGeometryDifference(GeometryDifference geometryDifference);

    public List<TagDifference> getAttributeDifferences();

    public void addAttributeDifference(TagDifference difference);
}
