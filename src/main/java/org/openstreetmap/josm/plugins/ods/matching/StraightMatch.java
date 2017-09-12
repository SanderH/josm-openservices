package org.openstreetmap.josm.plugins.ods.matching;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public class StraightMatch<T extends EntityType> implements Od2OsmMatch<T>, Osm2OdMatch<T> {
    private final OsmEntity<T> osmEntity;
    private final OdEntity<T> openDataEntity;
    private StatusDifference statusDifference = null;
    private GeometryDifference geometryDifference = null;
    private final List<TagDifference> attributeDifferences = new LinkedList<>();

    public StraightMatch(OsmEntity<T> osmEntity, OdEntity<T> openDataEntity) {
        super();
        this.osmEntity = osmEntity;
        this.openDataEntity = openDataEntity;
    }

    @Override
    public OsmEntity<T> getOsmEntity() {
        return osmEntity;
    }

    @Override
    public OdEntity<T> getOpenDataEntity() {
        return openDataEntity;
    }

    @Override
    public StatusDifference getStatusDifference() {
        return statusDifference;
    }

    @Override
    public void setStatusDifference(StatusDifference statusDifference) {
        this.statusDifference = statusDifference;
    }

    @Override
    public GeometryDifference getGeometryDifference() {
        return geometryDifference;
    }

    @Override
    public void setGeometryDifference(GeometryDifference geometryDifference) {
        this.geometryDifference = geometryDifference;
    }

    @Override
    public List<TagDifference> getAttributeDifferences() {
        return attributeDifferences;
    }

    @Override
    public void addAttributeDifference(TagDifference difference) {
        attributeDifferences.add(difference);
    }

    @Override
    public void clearDifferences() {
        this.statusDifference = null;
        this.geometryDifference = null;
        this.attributeDifferences.clear();
    }

    @Override
    public boolean hasDifferences() {
        return statusDifference != null || geometryDifference != null
                || !attributeDifferences.isEmpty();
    }
}
